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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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

	private BigDecimal totalDetailAmount = new BigDecimal("0");

	private File tempFile;

	private BufferedReader tempFileReader;

	private FileLayoutConfigItem detailDocAmountLayoutConfig;

	private FileLayoutConfigItem footerTotalDocAmountLayoutConfig;

	private FileLayoutConfigItem footerTotalDocLayoutConfig;

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		super(fileLayoutConfig, clazz);
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		fileConfigItems = prepareConfiguration(fileLayoutConfig.getConfigItems());

		BufferedWriter bufferedWriter = null;
		BufferedReader bufferReader = null;

		currentLineNo = 1;

		try {
			tempFile = File.createTempFile("drawdownAdvice", ".temp");
			bufferedWriter = new BufferedWriter(new FileWriter(tempFile));

			InputStream tempFileContent = new FileInputStream(tempFile);
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

						// Found a blank line between header and detail, throw an error
						throw new WrongFormatFileException(
								CovertErrorConstant.FILE_INVALID_FORMAT);
					}
				}

				if (!hasCheckedHeader) {

					String recordType = headerRecordTypeExtractor.extract(currentLine);

					if (fileLayoutConfig.getHeaderFlag().equals(recordType)) {
						List<FileLayoutConfigItem> headerConfigItems = fileConfigItems
								.get(RecordType.HEADER);

						validateLineDataFormat(currentLine, headerConfigItems);
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

					if (fileLayoutConfig.getFooterFlag().equals(recordType)) {
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

				if (fileLayoutConfig.getDetailFlag().equals(recordType)) {
					if (hasCheckedFooter) {
						FileLayoutConfigItem detialRecordTypeConfig = detailRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(MessageFormat.format(
								CovertErrorConstant.FOOTER_NOT_LAST_FILE,
								detialRecordTypeConfig.getDisplayValue(),
								fileLayoutConfig.getFooterFlag()));
					}

					List<FileLayoutConfigItem> detailConfigItems = fileConfigItems
							.get(RecordType.DETAIL);

					validateLineDataFormat(currentLine, detailConfigItems);

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
							int beginIndex = detailDocAmountLayoutConfig.getStartIndex()
									- 1;
							String data = currentLine.substring(beginIndex,
									beginIndex + detailDocAmountLayoutConfig.getLenght());

							BigDecimal docAmount = getBigDecimalValue(
									detailDocAmountLayoutConfig, data);
							totalDetailAmount = totalDetailAmount.add(docAmount);
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
		catch (IOException e) {
			throw new WrongFormatFileException("File read error occurred");
		}
		finally {
			currentLineNo = 0;
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

	private void validateFooter(String currentLine,
			List<FileLayoutConfigItem> footerConfigItems)
			throws WrongFormatFileException {

		validateLineDataFormat(currentLine, footerConfigItems);

		if (footerTotalDocLayoutConfig != null) {
			int start = footerTotalDocLayoutConfig.getStartIndex() - 1;
			int end = (footerTotalDocLayoutConfig.getStartIndex()
					+ footerTotalDocLayoutConfig.getLenght()) - 1;

			String totalDocData = currentLine.substring(start, end).trim();
			try {

				Integer footeTotalDocuments = Integer.valueOf(totalDocData);

				if (totalDetailRecord != footeTotalDocuments) {
					throw new WrongFormatFileException(MessageFormat.format(
							CovertErrorConstant.FOOTER_TOTAL_LINE_INVALIDE_LENGTH_MESSAGE,
							footeTotalDocuments, totalDetailRecord));
				}
			}
			catch (WrongFormatFileException e) {
				throw e;
			}
			catch (Exception e) {
				throw new WrongFormatFileException(MessageFormat.format(
						CovertErrorConstant.FOOTER_TOTAL_LINE_INVALIDE_FORMAT_MESSAGE,
						totalDocData));
			}
		}

		if (footerTotalDocAmountLayoutConfig != null) {
			int start = footerTotalDocAmountLayoutConfig.getStartIndex() - 1;
			int end = (footerTotalDocAmountLayoutConfig.getStartIndex()
					+ footerTotalDocAmountLayoutConfig.getLenght()) - 1;
			String totalDocAmoutData = currentLine.substring(start, end).trim();

			try {

				BigDecimal footerTotalAmount = getBigDecimalValue(
						footerTotalDocAmountLayoutConfig, totalDocAmoutData);

				if (footerTotalAmount.compareTo(totalDetailAmount) != 0) {
					throw new WrongFormatFileException(MessageFormat.format(
							CovertErrorConstant.FOOTER_TOTAL_AMOUNT_INVALIDE_LENGTH_MESSAGE,
							footerTotalAmount.doubleValue(),
							totalDetailAmount.doubleValue()));
				}
			}
			catch (WrongFormatFileException e) {
				throw e;
			}
			catch (Exception e) {
				throw new WrongFormatFileException(MessageFormat.format(
						CovertErrorConstant.FOOTER_TOTAL_AMOUNT_INVALIDE_FORMAT_MESSAGE,
						totalDocAmoutData));
			}
		}
	}

	private void validateLineDataFormat(String currentLine,
			List<FileLayoutConfigItem> configItems) throws WrongFormatFileException {

		int lineLength = getLengthOfLine(configItems);
		if (lineLength != StringUtils.length(currentLine)) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.DATA_LENGTH_OVER,
							StringUtils.length(currentLine), lineLength));
		}

		for (FileLayoutConfigItem configItem : configItems) {

			int start = configItem.getStartIndex() - 1;
			int end = (configItem.getStartIndex() + configItem.getLenght()) - 1;
			String dataValidate = currentLine.substring(start, end).trim();

			if (StringUtils.isNotBlank(configItem.getExpectValue())) {
				validateExpectedValue(configItem, dataValidate);
			}

			if (StringUtils.isNotBlank(configItem.getDatetimeFormat())) {
				validateDateFormat(configItem, dataValidate);
			}
		}

	}

	private void validateExpectedValue(FileLayoutConfigItem item, String dataValidate)
			throws WrongFormatFileException {

		if (!item.getExpectValue().equals(dataValidate.trim())) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.MISMATCH_FORMAT,
							item.getDisplayValue(), dataValidate.trim()));
		}

	}

	private int getLengthOfLine(
			List<? extends FileLayoutConfigItem> fileLayoutConfigItems) {
		int result = 0;
		for (FileLayoutConfigItem item : fileLayoutConfigItems) {
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

		for (FileLayoutConfigItem fileLayoutConfig : list) {
			if ("recordId".equals(fileLayoutConfig.getFieldName())) {
				RecordTypeExtractor extractor = new RecordTypeExtractor(fileLayoutConfig);
				extractors.put(fileLayoutConfig.getRecordType(), extractor);
			}
			else {
				switch (fileLayoutConfig.getRecordType()) {
				case HEADER:
					headerList.add(fileLayoutConfig);
					break;
				case FOOTER:
					if ("totalDocumentAmount".equals(fileLayoutConfig.getFieldName())) {
						footerTotalDocAmountLayoutConfig = fileLayoutConfig;
					}
					if ("totalDocumentNumber".equals(fileLayoutConfig.getFieldName())) {
						footerTotalDocLayoutConfig = fileLayoutConfig;
					}
					footerList.add(fileLayoutConfig);
					break;
				case DETAIL:
					if ("documentAmount".equals(fileLayoutConfig.getFieldName())) {
						detailDocAmountLayoutConfig = fileLayoutConfig;
					}
					detailList.add(fileLayoutConfig);
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
	public DetailResult getDetail() {
		DetailResult detailResult = new DetailResult();

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
			if (config.getFieldName().equals("recordId")) {
				continue;
			}

			int start = config.getStartIndex() - 1;
			int end = (config.getStartIndex() + config.getLenght()) - 1;
			String data = currentLine.substring(start, end);

			try {
				if (StringUtils.isNotBlank(config.getFieldName())) {
					applyObjectValue(data, config, entity);
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
				// TODO manage error message
				e.printStackTrace();
			}
		}
		if (isError) {
			throw new WrongFormatDetailException();
		}
		return entity;
	}

	public FileLayoutConfig getFileLayoutConfig() {
		return fileLayoutConfig;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
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
