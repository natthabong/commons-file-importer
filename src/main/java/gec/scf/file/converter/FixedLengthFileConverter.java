package gec.scf.file.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class FixedLengthFileConverter<T> implements FileConverter<T> {

	private static final Logger log = Logger.getLogger(FixedLengthFileConverter.class);

	private FileLayoutConfig fileLayoutConfig;

	private Map<RecordType, List<FileLayoutConfigItem>> fileConfigItems = null;

	private Map<RecordType, RecordTypeExtractor> extractors = new EnumMap<RecordType, RecordTypeExtractor>(
			RecordType.class);

	private Class<T> domainClass;

	private int currentLineNo;

	private int totalDetailRecord;

	private File tempFile;

	private BufferedReader tempFileReader;

	public FixedLengthFileConverter(Class<T> clazz) {
		this.domainClass = clazz;
	}

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this.fileLayoutConfig = fileLayoutConfig;
		this.domainClass = clazz;
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
						throw new WrongFormatFileException(MessageFormat.format(
								FixedLengthErrorConstant.HEADER_NOT_FIRST_LINE_OF_FILE,
								fileLayoutConfig.getHeaderFlag()));
					}
					else if (!hasCheckedFooter) {

						// Found a blank line between header and detail, throw an error
						throw new WrongFormatFileException(
								FixedLengthErrorConstant.FILE_INVALID_FORMAT);
					}
				}

				if (!hasCheckedHeader) {

					RecordTypeExtractor headerRecordTypeExtractor = extractors
							.get(RecordType.HEADER);
					String recordType = headerRecordTypeExtractor.extract(currentLine);

					if (fileLayoutConfig.getHeaderFlag().equals(recordType)) {
						List<FileLayoutConfigItem> headerConfigItems = fileConfigItems
								.get(RecordType.HEADER);

						validateDataFormat(currentLine, headerConfigItems);
						hasCheckedHeader = true;
						continue;
					}
					else {

						FileLayoutConfigItem recordTypeLayoutCofig = headerRecordTypeExtractor
								.getConfig();

						throw new WrongFormatFileException(MessageFormat.format(
								FixedLengthErrorConstant.RECORD_ID_MISS_MATCH,
								recordTypeLayoutCofig.getDisplayValue(), recordType));
					}
				}

				if (!hasCheckedFooter) {

					RecordTypeExtractor footerRecordTypeExtractor = extractors
							.get(RecordType.FOOTER);
					String recordType = footerRecordTypeExtractor.extract(currentLine);

					if (fileLayoutConfig.getFooterFlag().equals(recordType)) {
						List<FileLayoutConfigItem> footerConfigItems = fileConfigItems
								.get(RecordType.FOOTER);

						validateDataFormat(currentLine, footerConfigItems);
						hasCheckedFooter = true;
						continue;
					}
				}

				RecordTypeExtractor detailRecordTypeExtractor = extractors
						.get(RecordType.DETAIL);

				String recordType = detailRecordTypeExtractor.extract(currentLine);

				if (fileLayoutConfig.getDetailFlag().equals(recordType)) {
					if (hasCheckedFooter) {
						throw new WrongFormatFileException(MessageFormat.format(
								FixedLengthErrorConstant.FOOTER_NOT_LAST_FILE,
								fileLayoutConfig.getFooterFlag()));
					}

					List<FileLayoutConfigItem> detailConfigItems = fileConfigItems
							.get(RecordType.DETAIL);

					validateDataFormat(currentLine, detailConfigItems);

					try {
						bufferedWriter.write(currentLine);
						bufferedWriter.newLine();
					}
					catch (IOException e) {
						log.error(e.getMessage(), e);
					}

					totalDetailRecord++;
				}
				else {
					FileLayoutConfigItem recordTypeLayoutCofig = detailRecordTypeExtractor
							.getConfig();
					throw new WrongFormatFileException(MessageFormat.format(
							FixedLengthErrorConstant.RECORD_ID_MISS_MATCH,
							recordTypeLayoutCofig.getDisplayValue(), recordType));
				}

			}

			if (!hasCheckedFooter) {
				throw new WrongFormatFileException(MessageFormat.format(
						FixedLengthErrorConstant.FOOTER_NOT_LAST_FILE,
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

	private void validateDataFormat(String currentLine,
			List<FileLayoutConfigItem> configItems) throws WrongFormatFileException {

		int lineLength = getLengthOfLine(configItems);
		if (lineLength != StringUtils.length(currentLine)) {
			throw new WrongFormatFileException(
					MessageFormat.format(FixedLengthErrorConstant.DATA_LENGTH_OVER,
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

		if (!dataValidate.equals(item.getExpectValue())) {
			throw new WrongFormatFileException(
					MessageFormat.format(FixedLengthErrorConstant.MISMATCH_FORMAT,
							item.getDisplayValue(), dataValidate));
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
					footerList.add(fileLayoutConfig);
					break;
				case DETAIL:
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

		T domainObj = null;
		try {
			domainObj = (T) domainClass.newInstance();
		}
		catch (InstantiationException e1) {
			e1.printStackTrace();
		}
		catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		// List<DetailError> messageErrorDetails = new ArrayList<DetailError>();
		boolean isError = false;
		for (FileLayoutConfigItem config : fileLayoutConfigs) {
			if (config.getFieldName().equals("recordId")) {
				continue;
			}

			int start = config.getStartIndex() - 1;
			int end = (config.getStartIndex() + config.getLenght()) - 1;
			String data = currentLine.substring(start, end);

			try {

				Field field = domainClass.getDeclaredField(config.getFieldName());
				field.setAccessible(true);
				Class<?> classType = field.getType();

				if (classType.isAssignableFrom(Date.class)) {
					try {
						// validateDateDetail(messageErrorDetails, item, data);
						SimpleDateFormat sdf = new SimpleDateFormat(
								config.getDatetimeFormat(), Locale.US);
						Date date = sdf.parse(data.trim());
						field.set(domainObj, date);
					}
					catch (WrongFormatDetailException e) {
						isError = true;
					}
				}
				else if (classType.isAssignableFrom(BigDecimal.class)) {
					if (StringUtils.isBlank(data)) {
						// setDetailErrorInvalideFormat(messageErrorDetails, item, data);
						isError = true;
					}
					else if (data.contains("+")) {
						// setDetailErrorInvalideFormat(messageErrorDetails, item, data);
						isError = true;
					}
					else {
						BigDecimal valueAmount = getBigDecimalValue(data.trim(), config);
						field.set(domainObj, valueAmount);
					}
				}
				else {
					if (StringUtils.isBlank(data) && config.isRequired()) {
						// setDetailErrorRequire(messageErrorDetails, config);
						isError = true;
					}
					else {
						data = data.trim();
						field.set(domainObj, data);
					}
				}
			}
			catch (NumberFormatException e) {
				// setDetailErrorInvalideFormat(messageErrorDetails, config, data);
				isError = true;
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (isError) {
			throw new WrongFormatDetailException();
		}
		return domainObj;
	}

	private void validateDateFormat(FileLayoutConfigItem configItem, String dataValidate)
			throws WrongFormatFileException {
		DateValidator dateValidator = DateValidator.getInstance();
		if (!dateValidator.isValid(dataValidate.trim(), configItem.getDatetimeFormat(),
				Locale.US)) {
			throw new WrongFormatFileException(
					MessageFormat.format(FixedLengthErrorConstant.INVALIDE_FORMAT,
							configItem.getDisplayValue(), dataValidate.trim()));
		}
	}

	private BigDecimal getBigDecimalValue(String data, FileLayoutConfigItem config)
			throws IllegalAccessException {
		if (StringUtils.isNotBlank(config.getPlusSymbol())
				&& StringUtils.isNotBlank(config.getMinusSymbol())) {
			if (data.startsWith(config.getPlusSymbol())) {
				data = data.substring(1);
			}
			else if (data.startsWith(config.getMinusSymbol())) {
				data = "-" + data.substring(1);
			}
		}
		String normalNumber = data.substring(0,
				(data.length() - config.getDecimalPlace()));
		String degitNumber = data.substring(data.length() - config.getDecimalPlace());

		BigDecimal valueAmount = new BigDecimal(normalNumber + "." + degitNumber)
				.setScale(config.getDecimalPlace());

		return valueAmount;
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
