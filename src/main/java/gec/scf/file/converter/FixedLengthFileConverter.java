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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.ErrorLineDetail;
import gec.scf.file.validation.SummaryFieldValidator;

public class FixedLengthFileConverter<T> extends AbstractFileConverter<T> {

	private static final Logger log = Logger.getLogger(FixedLengthFileConverter.class);

	private Map<RecordType, List<FileLayoutConfigItem>> fileConfigItems = null;

	private Map<RecordType, RecordTypeExtractor> extractors = new EnumMap<RecordType, RecordTypeExtractor>(
			RecordType.class);

	private int currentLineNo;

	private Long lastDocumentNo;

	private File tempFile;

	private BufferedReader tempFileReader;

	private Map<String, String> headerData = new HashMap<String, String>();

	private Map<String, String> footerData = new HashMap<String, String>();

	private Map<RecordType, Set<DataObserver<?>>> observers = new HashMap<RecordType, Set<DataObserver<?>>>();

	private Map<FileLayoutConfigItem, FieldValidator> validators = new HashMap<FileLayoutConfigItem, FieldValidator>();

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		super(fileLayoutConfig, clazz);
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		fileConfigItems = prepareConfiguration(getFileLayoutConfig().getConfigItems());

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

			if (getFileLayoutConfig().isCheckBinaryFile()) {
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
								getFileLayoutConfig().getHeaderFlag()));
					}
					else if (!hasCheckedFooter) {

						// Found a blank line between header and detail, throw
						// an error
						throw new WrongFormatFileException(
								CovertErrorConstant.FILE_INVALID_FORMAT);
					}
				}

				// Enter header
				if (!hasCheckedHeader) {

					String recordType = headerRecordTypeExtractor.extract(currentLine);

					if (StringUtils.isBlank(recordType.trim())) {
						// Found a blank recoredId
						FileLayoutConfigItem headerRecordType = headerRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(MessageFormat.format(
								CovertErrorConstant.HEADER_NOT_FIRST_LINE_OF_FILE,
								headerRecordType.getDisplayValue(),
								getFileLayoutConfig().getHeaderFlag()));
					}
					else if (getFileLayoutConfig().getHeaderFlag().equals(recordType)) {

						List<FileLayoutConfigItem> headerConfigItems = fileConfigItems
								.get(RecordType.HEADER);

						validateLineDataFormat(currentLine, headerConfigItems, headerData,
								RecordType.HEADER);

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

				// Enter footer
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
										getFileLayoutConfig().getFooterFlag()),
								currentLineNo);
					}
					else if (getFileLayoutConfig().getFooterFlag().equals(recordType)) {
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
				if (getFileLayoutConfig().getDetailFlag().equals(recordType)) {

					if (hasCheckedFooter) {

						FileLayoutConfigItem detialRecordTypeConfig = detailRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(
								MessageFormat.format(
										CovertErrorConstant.FOOTER_NOT_LAST_FILE,
										detialRecordTypeConfig.getDisplayValue(),
										getFileLayoutConfig().getFooterFlag()),
								currentLineNo);
					}

					// Enter detail
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

					Set<DataObserver<?>> detailObservers = observers
							.get(RecordType.DETAIL);

					if (detailObservers != null) {
						final String detailData = currentLine;
						detailObservers.forEach(observer -> {

							if (observer instanceof SummaryFieldValidator) {

								FileLayoutConfigItem aggregationFieldConfig = observer
										.getObserveFieldConfig();
								String data = getCuttedData(aggregationFieldConfig,
										detailData);
								try {
									BigDecimal docAmount = getBigDecimalValue(
											aggregationFieldConfig, data);
									if (aggregationFieldConfig
											.getSignFlagConfig() != null) {

										String signFlagData = getCuttedData(
												aggregationFieldConfig
														.getSignFlagConfig(),
												detailData);
										docAmount = applySignFlag(docAmount,
												aggregationFieldConfig, signFlagData);

									}
									observer.observe(docAmount);
								}
								catch (Exception e) {
									log.warn(e.getMessage(), e);
								}
							}
							else {
								observer.observe(detailData);
							}

						});
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
								getFileLayoutConfig().getFooterFlag()));
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

		validateLineDataFormat(currentLine, footerConfigItems, footerData,
				RecordType.FOOTER);

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

	private void validateLineDataFormat(String currentLine,
			List<FileLayoutConfigItem> configItems, Map<String, String> rawDataOfLine,
			RecordType recordType) throws WrongFormatFileException {

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

			FieldValidator fieldValidator = validators.get(configItem);
			if (fieldValidator != null) {
				if (fieldValidator instanceof SummaryFieldValidator) {
					String totalAmoutData = getCuttedData(configItem, currentLine);
					try {

						BigDecimal footerTotalAmount = getBigDecimalValue(configItem,
								totalAmoutData);

						if (configItem.getSignFlagConfig() != null) {

							String signFlagData = getCuttedData(
									configItem.getSignFlagConfig(), currentLine);

							footerTotalAmount = applySignFlag(footerTotalAmount,
									configItem, signFlagData);

						}
						fieldValidator.validate(footerTotalAmount);
					}
					catch (WrongFormatFileException e) {
						if (e.getErrorLineNo() == null) {
							e.setErrorLineNo(currentLineNo);
						}
						throw e;
					}
					catch (WrongFormatDetailException e) {
						throw new WrongFormatFileException(e.getErrorMessage(),
								currentLineNo);
					}
					catch (Exception e) {
						throw new WrongFormatFileException(
								MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
										configItem.getDisplayValue(), totalAmoutData),
								currentLineNo);
					}
				}
				else {
					fieldValidator.validate(dataValidate);
				}
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
				RecordTypeExtractor extractor = new RecordTypeExtractor(
						fileLayoutConfigItem);
				extractors.put(fileLayoutConfigItem.getRecordTypeData(), extractor);
			}
			else {

				if (fileLayoutConfigItem.getValidationType() != null) {

					FieldValidator fieldValidator = fieldValidatorFactory
							.create(fileLayoutConfigItem);
					if (fieldValidator != null) {

						if (fieldValidator instanceof DataObserver) {
							DataObserver<?> fileObserver = (DataObserver<?>) fieldValidator;
							if (observers.get(fileObserver.getObserveSection()) == null) {
								observers.put(fileObserver.getObserveSection(),
										new HashSet<DataObserver<?>>());
							}
							observers.get(fileObserver.getObserveSection())
									.add(fileObserver);
						}
						validators.put(fileLayoutConfigItem, fieldValidator);

					}
				}

				switch (fileLayoutConfigItem.getRecordTypeData()) {
				case HEADER:
					headerList.add(fileLayoutConfigItem);
					break;
				case FOOTER:
					footerList.add(fileLayoutConfigItem);
					break;
				case DETAIL:
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
