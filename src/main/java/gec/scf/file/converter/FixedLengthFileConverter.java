package gec.scf.file.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gec.scf.file.common.DateTimeImpl;
import gec.scf.file.common.DateTimeProvider;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.ErrorLineDetail;

public class FixedLengthFileConverter<T> extends AbstractFileConverter<T> {

	private static final Logger log = Logger.getLogger(FixedLengthFileConverter.class);

	private FileLayoutConfig fileLayoutConfig;

	private Map<RecordType, List<FileLayoutConfigItem>> fileConfigItems = null;

	private Map<RecordType, RecordTypeExtractor> extractors = new EnumMap<RecordType, RecordTypeExtractor>(
	        RecordType.class);

	private int currentLineNo;

	private int totalDetailRecord;

	private BigDecimal totalDetailDocAmount = new BigDecimal("0");

	private BigDecimal totalDetailOutstandingAmount = new BigDecimal("0");

	private Long lastDocumentNo;

	private File tempFile;

	private BufferedReader tempFileReader;

	private FileLayoutConfigItem detailDocAmountLayoutConfig;

	private FileLayoutConfigItem detailOutstandingAmountLayoutConfig;

	private FileLayoutConfigItem footerTotalDocLayoutConfig;

	private FileLayoutConfigItem footerTotalDocAmountLayoutConfig;

	private FileLayoutConfigItem footerTotalOutstandingAmountLayoutConfig;

	private Map<String, String> headerData = new HashMap<String, String>();

	private Map<String, String> footerData = new HashMap<String, String>();

	private DateTimeProvider currentDateTime;

	private FileObserverFactory fileObserverFactory;

	private Map<RecordType, List<FileObserver>> observers;

	// private List<FieldValidator> footerValidators ;

	// private FieldValidatorFactory fieldValidatorFactory;

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		super(fileLayoutConfig, clazz);
		currentDateTime = new DateTimeImpl();
		// footerValidators = new ArrayList<FieldValidator>();
		// this.fieldValidatorFactory = fieldValidatorFactory;
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		fileConfigItems = prepareConfiguration(fileLayoutConfig.getConfigItems());

		BufferedWriter bufferedWriter = null;
		BufferedReader bufferReader = null;

		currentLineNo = 1;

		try {
			if (fileContent == null) {
				throw new IllegalArgumentException("Invalid file content");
			}

			tempFile = File.createTempFile("DOCUMENT_T_", ".temp");
			bufferedWriter = new BufferedWriter(new FileWriter(tempFile));

			InputStream tempFileContent = new FileInputStream(tempFile);

			if (fileLayoutConfig.isCheckBinaryFile()) {
				fileContent = validateBinaryFile(fileContent);
			}

			tempFileReader = new BufferedReader(
			        new InputStreamReader(tempFileContent, "UTF-8"));

			bufferReader = new BufferedReader(
			        new InputStreamReader(fileContent, "UTF-8"));

			RecordTypeExtractor headerRecordTypeExtractor = extractors
			        .get(RecordType.HEADER);
			RecordTypeExtractor footerRecordTypeExtractor = extractors
			        .get(RecordType.FOOTER);

			boolean hasCheckedHeader = false;
			boolean hasCheckedFooter = false;

			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {

				// When found a blank line
				if (StringUtils.isBlank(currentLine)) {

					// Found a blank line on the end of file, do nothing
					if (hasCheckedHeader && hasCheckedFooter) {
						continue;
					}
					else if (!hasCheckedHeader) {

						// Found a blank line on top of file, throw an error
						FileLayoutConfigItem headerRecordType = headerRecordTypeExtractor
						        .getConfig();
						throw new WrongFormatFileException(MessageFormat.format(
						        CovertErrorConstant.HEADER_NOT_FIRST_LINE_OF_FILE,
						        headerRecordType.getDisplayValue(),
						        fileLayoutConfig.getHeaderFlag()));
					}
					else if (!hasCheckedFooter) {

						// Found a blank line between header and detail, throw
						// an error
						throw new WrongFormatFileException(
						        CovertErrorConstant.FILE_INVALID_FORMAT);
					}
				}

				if (!hasCheckedHeader) {

					String recordType = headerRecordTypeExtractor.extract(currentLine);

					if (StringUtils.isBlank(recordType.trim())) {
						// Found a blank recoredId
						FileLayoutConfigItem headerRecordType = headerRecordTypeExtractor
						        .getConfig();
						throw new WrongFormatFileException(MessageFormat.format(
						        CovertErrorConstant.HEADER_NOT_FIRST_LINE_OF_FILE,
						        headerRecordType.getDisplayValue(),
						        fileLayoutConfig.getHeaderFlag()));
					}
					else if (fileLayoutConfig.getHeaderFlag().equals(recordType)) {
						List<FileLayoutConfigItem> headerConfigItems = fileConfigItems
						        .get(RecordType.HEADER);

						validateLineDataFormat(currentLine, headerConfigItems, headerData,
						        null);

						// validateLineDataFormat(currentLine, headerConfigItems,
						// headerData);
						hasCheckedHeader = true;
						continue;
					}
					else {

						FileLayoutConfigItem recordTypeLayoutCofig = headerRecordTypeExtractor
						        .getConfig();

						throw new WrongFormatFileException(MessageFormat.format(
						        CovertErrorConstant.RECORD_ID_MISS_MATCH,
						        recordTypeLayoutCofig.getDisplayValue(), recordType));
					}
				}

				if (!hasCheckedFooter) {

					String recordType = footerRecordTypeExtractor.extract(currentLine);
					if (StringUtils.isBlank(recordType.trim())) {
						// Found a blank recoredId
						FileLayoutConfigItem footerRecordType = footerRecordTypeExtractor
						        .getConfig();
						throw new WrongFormatFileException(
						        MessageFormat.format(
						                CovertErrorConstant.FOOTER_NOT_LAST_LINE_OF_FILE,
						                footerRecordType.getDisplayValue(),
						                fileLayoutConfig.getFooterFlag()),
						        currentLineNo);
					}
					else if (fileLayoutConfig.getFooterFlag().equals(recordType)) {
						List<FileLayoutConfigItem> footerConfigItems = fileConfigItems
						        .get(RecordType.FOOTER);

						validateFooter(currentLine, footerConfigItems);
						hasCheckedFooter = true;
						continue;
					}
				}

				RecordTypeExtractor detailRecordTypeExtractor = extractors
				        .get(RecordType.DETAIL);

				String recordType = detailRecordTypeExtractor.extract(currentLine);

				// Validate Detail
				if (fileLayoutConfig.getDetailFlag().equals(recordType)) {

					if (hasCheckedFooter) {

						FileLayoutConfigItem detialRecordTypeConfig = detailRecordTypeExtractor
						        .getConfig();
						throw new WrongFormatFileException(
						        MessageFormat.format(
						                CovertErrorConstant.FOOTER_NOT_LAST_FILE,
						                detialRecordTypeConfig.getDisplayValue(),
						                fileLayoutConfig.getFooterFlag()),
						        currentLineNo);
					}

					List<FileLayoutConfigItem> detailConfigItems = fileConfigItems
					        .get(RecordType.DETAIL);
					//
					validateLineDataLength(currentLine, detailConfigItems);
					try {
						bufferedWriter.write(currentLine);
						bufferedWriter.newLine();
					}
					catch (IOException e) {
						log.error(e.getMessage(), e);
					}

					totalDetailRecord++;

					if (detailDocAmountLayoutConfig != null) {
						try {

							String data = getCuttedData(detailDocAmountLayoutConfig,
							        currentLine);

							BigDecimal docAmount = getBigDecimalValue(
							        detailDocAmountLayoutConfig, data);
							if (detailDocAmountLayoutConfig.getSignFlagConfig() != null) {

								String signFlagData = getCuttedData(
								        detailDocAmountLayoutConfig.getSignFlagConfig(),
								        currentLine);
								totalDetailDocAmount = applySignFlag(totalDetailDocAmount,
								        detailDocAmountLayoutConfig, signFlagData);

							}
							totalDetailDocAmount = totalDetailDocAmount.add(docAmount);
						}
						catch (WrongFormatDetailException e) {
							totalDetailDocAmount = totalDetailDocAmount
							        .add(new BigDecimal("0.0"));
						}
						catch (Exception e) {
							log.warn(e.getMessage(), e);
						}
					}

					if (detailOutstandingAmountLayoutConfig != null) {
						try {

							String data = getCuttedData(
							        detailOutstandingAmountLayoutConfig, currentLine);

							BigDecimal docAmount = getBigDecimalValue(
							        detailOutstandingAmountLayoutConfig, data);
							if (detailOutstandingAmountLayoutConfig
							        .getSignFlagConfig() != null) {

								String signFlagData = getCuttedData(
								        detailOutstandingAmountLayoutConfig
								                .getSignFlagConfig(),
								        currentLine);
								totalDetailOutstandingAmount = applySignFlag(
								        totalDetailOutstandingAmount,
								        detailOutstandingAmountLayoutConfig,
								        signFlagData);

							}
							totalDetailOutstandingAmount = totalDetailOutstandingAmount
							        .add(docAmount);
						}
						catch (WrongFormatDetailException e) {
							totalDetailDocAmount = totalDetailDocAmount
							        .add(new BigDecimal("0.0"));
						}
						catch (Exception e) {
							log.warn(e.getMessage(), e);
						}
					}
				}
				else {
					FileLayoutConfigItem recordTypeLayoutCofig = detailRecordTypeExtractor
					        .getConfig();
					throw new WrongFormatFileException(
					        MessageFormat.format(CovertErrorConstant.RECORD_ID_MISS_MATCH,
					                recordTypeLayoutCofig.getDisplayValue(), recordType));
				}

			}

			if (!hasCheckedFooter) {

				FileLayoutConfigItem recordTypeLayoutCofig = footerRecordTypeExtractor
				        .getConfig();
				throw new WrongFormatFileException(
				        MessageFormat.format(CovertErrorConstant.FOOTER_NOT_LAST_FILE,
				                recordTypeLayoutCofig.getDisplayValue(),
				                fileLayoutConfig.getFooterFlag()));
			}
		}
		catch (IllegalArgumentException e) {
			throw new WrongFormatFileException(CovertErrorConstant.FILE_INVALID_FORMAT);
		}
		catch (WrongFormatFileException e) {
			e.setErrorLineNo(null);
			throw e;
		}
		catch (WrongFormatDetailException e) {
			throw e;
		}

		catch (IOException e) {
			throw new WrongFormatFileException("File read error occurred", null);
		}
		finally {
			currentLineNo = 1;
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
				if (bufferReader != null) {
					bufferReader.close();
				}
			}
			catch (IOException e) {
				log.error(e.getMessage(), e);
			}

		}

	}

	private String getCuttedData(FileLayoutConfigItem configItem, String currentLine) {
		int start = configItem.getStartIndex() - 1;
		int end = (configItem.getStartIndex() + configItem.getLenght()) - 1;

		return currentLine.substring(start, end).trim();
	}

	private void validateFooter(String currentLine,
	        List<FileLayoutConfigItem> footerConfigItems)
	        throws WrongFormatFileException {

		Map<String, Object> dataToValidate = new HashMap<String, Object>();
		dataToValidate.put("COUNT_OF_DOCUMENT_DETAIL", totalDetailRecord);

		validateLineDataFormat(currentLine, footerConfigItems, footerData,
		        dataToValidate);
		// validateLineDataFormat(currentLine, footerConfigItems, footerData);

		// if (footerTotalDocLayoutConfig != null) {
		// int start = footerTotalDocLayoutConfig.getStartIndex() - 1;
		// int end = (footerTotalDocLayoutConfig.getStartIndex()
		// + footerTotalDocLayoutConfig.getLenght()) - 1;
		//
		// String totalDocData = currentLine.substring(start, end).trim();
		// try {
		//
		// Integer footeTotalDocuments = Integer.valueOf(totalDocData);
		//
		// if (totalDetailRecord != footeTotalDocuments) {
		// throw new WrongFormatFileException(
		// MessageFormat.format(
		// CovertErrorConstant.FOOTER_TOTAL_LINE_INVALIDE_LENGTH_MESSAGE,
		// footerTotalDocLayoutConfig.getDisplayValue(),
		// footeTotalDocuments, totalDetailRecord),
		// currentLineNo);
		// }
		//
		// }
		// catch (WrongFormatFileException e) {
		// throw e;
		// }
		// catch (Exception e) {
		// throw new WrongFormatFileException(
		// MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
		// footerTotalDocLayoutConfig.getDisplayValue(),
		// totalDocData),
		// currentLineNo);
		// }
		// }

		if (footerTotalDocAmountLayoutConfig != null) {

			String totalDocAmoutData = getCuttedData(footerTotalDocAmountLayoutConfig,
			        currentLine);
			try {

				BigDecimal footerTotalAmount = getBigDecimalValue(
				        footerTotalDocAmountLayoutConfig, totalDocAmoutData);

				if (footerTotalDocAmountLayoutConfig.getSignFlagConfig() != null) {

					String signFlagData = getCuttedData(
					        footerTotalDocAmountLayoutConfig.getSignFlagConfig(),
					        currentLine);

					footerTotalAmount = applySignFlag(footerTotalAmount,
					        footerTotalDocAmountLayoutConfig, signFlagData);

				}
				if (footerTotalAmount.compareTo(totalDetailDocAmount) != 0) {
					throw new WrongFormatFileException(
					        MessageFormat.format(
					                CovertErrorConstant.FOOTER_TOTAL_AMOUNT_INVALIDE_LENGTH_MESSAGE,
					                footerTotalDocAmountLayoutConfig.getDisplayValue(),
					                footerTotalAmount.doubleValue(),
					                totalDetailDocAmount.doubleValue()),
					        currentLineNo);
				}
			}
			catch (WrongFormatFileException e) {
				throw e;
			}
			catch (WrongFormatDetailException e) {
				throw new WrongFormatFileException(e.getErrorMessage(), currentLineNo);
			}
			catch (Exception e) {
				throw new WrongFormatFileException(
				        MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
				                footerTotalDocAmountLayoutConfig.getDisplayValue(),
				                totalDocAmoutData),
				        currentLineNo);
			}
		}

		if (footerTotalOutstandingAmountLayoutConfig != null) {

			String totalOutstandingAmoutData = getCuttedData(
			        footerTotalOutstandingAmountLayoutConfig, currentLine);
			try {

				BigDecimal footerTotalAmount = getBigDecimalValue(
				        footerTotalOutstandingAmountLayoutConfig,
				        totalOutstandingAmoutData);

				if (footerTotalDocAmountLayoutConfig.getSignFlagConfig() != null) {

					String signFlagData = getCuttedData(
					        footerTotalOutstandingAmountLayoutConfig.getSignFlagConfig(),
					        currentLine);

					footerTotalAmount = applySignFlag(footerTotalAmount,
					        footerTotalOutstandingAmountLayoutConfig, signFlagData);

				}
				if (footerTotalAmount.compareTo(totalDetailOutstandingAmount) != 0) {
					throw new WrongFormatFileException(
					        MessageFormat.format(
					                CovertErrorConstant.FOOTER_TOTAL_AMOUNT_INVALIDE_LENGTH_MESSAGE,
					                footerTotalOutstandingAmountLayoutConfig
					                        .getDisplayValue(),
					                footerTotalAmount.doubleValue(),
					                totalDetailOutstandingAmount.doubleValue()),
					        currentLineNo);
				}
			}
			catch (WrongFormatFileException e) {
				throw e;
			}
			catch (WrongFormatDetailException e) {
				throw new WrongFormatFileException(e.getErrorMessage(), currentLineNo);
			}
			catch (Exception e) {
				throw new WrongFormatFileException(
				        MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
				                footerTotalDocAmountLayoutConfig.getDisplayValue(),
				                totalOutstandingAmoutData),
				        currentLineNo);
			}
		}

		// Matching with header
		for (FileLayoutConfigItem fileLayoutConfigItem : footerConfigItems) {
			String docFieldName = fileLayoutConfigItem.getDocFieldName();

			String footerRawData = footerData.get(docFieldName);

			if (StringUtils.isNotBlank(footerRawData)) {

				String headerRawData = headerData.get(docFieldName);

				if (headerRawData != null && !footerRawData.equals(headerRawData)) {

					throw new WrongFormatFileException(
					        MessageFormat.format(CovertErrorConstant.MISMATCH_WITH_HEADER,
					                fileLayoutConfigItem.getDisplayValue(), footerRawData,
					                headerRawData));
				}
			}
		}

	}

	// private void validateLineDataFormat(String currentLine,
	// List<FileLayoutConfigItem> configItems, Map<String, String> rawDataOfLine)
	// throws WrongFormatFileException {
	//
	// validateLineDataLength(currentLine, configItems);
	//
	// for (FileLayoutConfigItem configItem : configItems) {
	//
	// int start = configItem.getStartIndex() - 1;
	// int end = (configItem.getStartIndex() + configItem.getLenght()) - 1;
	// String dataValidate = currentLine.substring(start, end);
	//
	// if (StringUtils.isNotBlank(configItem.getDocFieldName())) {
	// rawDataOfLine.put(configItem.getDocFieldName(), dataValidate);
	// }
	//
	// if (StringUtils.isNotBlank(configItem.getExpectedValue())) {
	// validateExpectedValue(configItem, dataValidate);
	// }
	//
	// if (StringUtils.isNotBlank(configItem.getDatetimeFormat())) {
	// validateDateFormat(configItem, dataValidate);
	// }
	//
	// if ("documentNo".equals(configItem.getDocFieldName())) {
	// lastDocumentNo = validateDocumentNo(configItem, dataValidate,
	// lastDocumentNo);
	// }
	//
	// if (configItem.getValidationType() != null) {
	// FieldValidator fieldValidator = fieldValidatorFactory.create(configItem);
	// fieldValidator.validate(dataValidate);
	// }
	// }
	// }

	private void validateLineDataFormat(String currentLine,
	        List<FileLayoutConfigItem> configItems, Map<String, String> rawDataOfLine,
	        Map<String, Object> dataToValidate) throws WrongFormatFileException {

		validateLineDataLength(currentLine, configItems);

		for (FileLayoutConfigItem configItem : configItems) {

			int start = configItem.getStartIndex() - 1;
			int end = (configItem.getStartIndex() + configItem.getLenght()) - 1;
			String dataValidate = currentLine.substring(start, end);

			if (StringUtils.isNotBlank(configItem.getDocFieldName())) {
				rawDataOfLine.put(configItem.getDocFieldName(), dataValidate);
			}

			if (StringUtils.isNotBlank(configItem.getExpectedValue())) {
				validateExpectedValue(configItem, dataValidate);
			}

			if (StringUtils.isNotBlank(configItem.getDatetimeFormat())) {
				validateDateFormat(configItem, dataValidate);
			}

			if ("documentNo".equals(configItem.getDocFieldName())) {
				lastDocumentNo = validateDocumentNo(configItem, dataValidate,
				        lastDocumentNo);
			}

			if (configItem.getValidationType() != null) {
				FieldValidator fieldValidator = fieldValidatorFactory.create(configItem);
				fieldValidator.setDataToValidate(dataToValidate);
				fieldValidator.validate(dataValidate);
			}
		}
	}

	private void validateLineDataLength(String currentLine,
	        List<FileLayoutConfigItem> configItems) throws WrongFormatFileException {
		int lineLength = getLengthOfLine(configItems);
		if (lineLength != StringUtils.length(currentLine)) {
			throw new WrongFormatFileException(
			        MessageFormat.format(CovertErrorConstant.DATA_LENGTH_OVER,
			                StringUtils.length(currentLine), lineLength));
		}
	}

	private void validateExpectedValue(FileLayoutConfigItem item, String dataValidate)
	        throws WrongFormatFileException {

		if (StringUtils.isBlank(dataValidate)) {
			String errorMessage = MessageFormat.format(
			        CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE, item.getDisplayValue());
			throwErrorByRecordType(item, errorMessage);
		}
		if (!item.getExpectedValue().equals(dataValidate.trim())) {
			String errorMessage = MessageFormat.format(
			        CovertErrorConstant.MISMATCH_FORMAT, item.getDisplayValue(),
			        dataValidate.trim());
			throwErrorByRecordType(item, errorMessage);
		}
	}

	private void throwErrorByRecordType(FileLayoutConfigItem item, String errorMessage)
	        throws WrongFormatFileException {
		if (RecordType.DETAIL.equals(item.getRecordTypeData())) {
			throw new WrongFormatDetailException(errorMessage);
		}
		else {
			throw new WrongFormatFileException(errorMessage, currentLineNo);
		}
	}

	private int getLengthOfLine(
	        List<? extends FileLayoutConfigItem> fileLayoutConfigItems) {
		int result = 0;
		for (FileLayoutConfigItem item : fileLayoutConfigItems) {
			if (item.getStartIndex() == null) {
				continue;
			}
			int length = (item.getStartIndex() - 1) + item.getLenght();
			if (result <= length)
				result = length;
		}
		return result;
	}

	private Map<RecordType, List<FileLayoutConfigItem>> prepareConfiguration(
	        List<? extends FileLayoutConfigItem> list) {

		Map<RecordType, List<FileLayoutConfigItem>> fileLayoutMapping = new EnumMap<RecordType, List<FileLayoutConfigItem>>(
		        RecordType.class);
		List<FileLayoutConfigItem> headerList = new ArrayList<FileLayoutConfigItem>();
		List<FileLayoutConfigItem> detailList = new ArrayList<FileLayoutConfigItem>();
		List<FileLayoutConfigItem> footerList = new ArrayList<FileLayoutConfigItem>();

		for (FileLayoutConfigItem fileLayoutConfigItem : list) {
			if ("recordId".equals(fileLayoutConfigItem.getDocFieldName())) {
				RecordTypeExtractor extractor = new RecordTypeExtractor(fileLayoutConfigItem);
				extractors.put(fileLayoutConfigItem.getRecordTypeData(), extractor);
			}
			else {
				FileObserver fileObserver = null;
				switch (fileLayoutConfigItem.getRecordTypeData()) {
				case HEADER:
					headerList.add(fileLayoutConfigItem);
					break;
				case FOOTER:
					// footerValidators.add(fieldValidatorFactory.create(fileLayoutConfig));

					// if ("totalDocumentAmount"
					// .equals(fileLayoutConfig.getDocFieldName())) {
					// footerTotalDocAmountLayoutConfig = fileLayoutConfig;
					// }
					// if ("totalOutstandingAmount"
					// .equals(fileLayoutConfig.getDocFieldName())) {
					// footerTotalOutstandingAmountLayoutConfig = fileLayoutConfig;
					// }
					// if ("totalDocumentNumber"
					// .equals(fileLayoutConfig.getDocFieldName())) {
					// footerTotalDocLayoutConfig = fileLayoutConfig;
					// }
					
					
//					fileObserver = fileObserverFactory.create(fileLayoutConfigItem);
//					
//					List<FileObserver> observerList = observers
//					        .get(fileObserver.getRecordType());
//					
//					if(observerList == null){
//						observerList = new ArrayList<FileObserver>();
//					}
//					
//					observerList.add(fileObserver);
//
					footerList.add(fileLayoutConfigItem);
					break;
				case DETAIL:
					if ("documentAmount".equals(fileLayoutConfigItem.getDocFieldName())) {
						detailDocAmountLayoutConfig = fileLayoutConfigItem;
					}
					if ("outstandingAmount".equals(fileLayoutConfigItem.getDocFieldName())) {
						detailOutstandingAmountLayoutConfig = fileLayoutConfigItem;
					}
					detailList.add(fileLayoutConfigItem);
					break;

				default:
					break;
				}
			}

		}

		fileLayoutMapping.put(RecordType.HEADER, headerList);
		fileLayoutMapping.put(RecordType.DETAIL, detailList);
		fileLayoutMapping.put(RecordType.FOOTER, footerList);

		return fileLayoutMapping;
	}

	@Override
	public DetailResult<T> getDetail() {
		DetailResult<T> detailResult = new DetailResult<T>();

		String currentLine = null;
		detailResult.setLineNo(++currentLineNo);
		try {
			currentLine = tempFileReader.readLine();

			if (currentLine != null) {
				T detailObject = convertDetail(currentLine);
				detailResult.setSuccess(true);
				detailResult.setObjectValue(detailObject);
			}
			else {
				detailResult = null;
				tempFileReader.close();
				tempFile.delete();
			}
		}
		catch (WrongFormatDetailException e) {
			detailResult.setErrorLineDetails(e.getErrorLineDetails());
			detailResult.setSuccess(false);
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return detailResult;
	}

	private T convertDetail(String currentLine) {

		List<? extends FileLayoutConfigItem> fileLayoutConfigs = fileConfigItems
		        .get(RecordType.DETAIL);

		T entity = null;
		try {
			entity = (T) getEntityClass().newInstance();
		}
		catch (InstantiationException e1) {
			e1.printStackTrace();
		}
		catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		List<ErrorLineDetail> errorLineDetails = new ArrayList<ErrorLineDetail>();

		boolean isError = false;
		for (FileLayoutConfigItem config : fileLayoutConfigs) {
			if (StringUtils.isNotBlank(config.getDocFieldName())
			        && config.getDocFieldName().equals("recordId")) {
				continue;
			}

			try {

				if (StringUtils.isNotBlank(config.getDefaultValue())) {
					applyObjectValue(entity, config, config.getDefaultValue());
					continue;
				}

				int start = config.getStartIndex() - 1;
				int end = (config.getStartIndex() + config.getLenght()) - 1;
				String data = currentLine.substring(start, end);

				if (config.getExpectedValue() != null) {
					validateExpectedValue(config, data);
				}

				if (StringUtils.isNotBlank(config.getDocFieldName())) {
					String signFlagData = null;
					if (config.getSignFlagConfig() != null) {
						FileLayoutConfigItem signFlagConfig = config.getSignFlagConfig();

						int startSignFlag = signFlagConfig.getStartIndex() - 1;
						int endSignFlag = (signFlagConfig.getStartIndex()
						        + signFlagConfig.getLenght()) - 1;
						signFlagData = currentLine.substring(startSignFlag, endSignFlag);
					}
					applyObjectValue(entity, config, data, signFlagData);
				}

			}
			catch (WrongFormatDetailException e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLineNo);
				errorLineDetail.setErrorMessage(e.getErrorMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			}
			catch (Exception e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLineNo);
				errorLineDetail.setErrorMessage(e.getMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
				e.printStackTrace();
			}
		}
		if (isError) {
			throw new WrongFormatDetailException(errorLineDetails);
		}
		return entity;
	}

	public FileLayoutConfig getFileLayoutConfig() {
		return fileLayoutConfig;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
	}

	public Long getLastDocumentNo() {
		return lastDocumentNo;
	}

	public void setLastDocumentNo(Long lastDocumentNo) {
		this.lastDocumentNo = lastDocumentNo;
	}

	private static class RecordTypeExtractor {

		private FileLayoutConfigItem configItem;

		public RecordTypeExtractor(FileLayoutConfigItem configItem) {
			this.configItem = configItem;
		}

		public FileLayoutConfigItem getConfig() {
			return configItem;
		}

		public String extract(String currentLine) {
			int beginIndex = configItem.getStartIndex() - 1;
			String recordType = null;
			try {
				recordType = currentLine.substring(beginIndex,
				        beginIndex + configItem.getLenght());
			}
			catch (IndexOutOfBoundsException e) {
				log.debug(e.getMessage());
			}
			return recordType;
		}

	}

}
