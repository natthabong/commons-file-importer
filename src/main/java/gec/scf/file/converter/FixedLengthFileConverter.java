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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class FixedLengthFileConverter<T> implements FileConverter<T> {

	private static final Logger log = Logger.getLogger(FixedLengthFileConverter.class);

	private FileLayoutConfig fileLayoutConfig;

	private int currentLineNo;

	// private int totalDetailRecord;

	private File tempFile;

	private BufferedReader tempFileReader;

	private Map<String, List<? extends FileLayoutConfigItem>> fileConfigItems = null;

	private static final String DETAIL = "DETAIL";

	private Class<T> domainClass;

	public FixedLengthFileConverter(Class<T> clazz) {
		this.domainClass = clazz;
	}

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this.fileLayoutConfig = fileLayoutConfig;
		this.domainClass = clazz;
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		fileConfigItems = prepareConfigData(fileLayoutConfig.getConfigItems());

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

			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {
				String recordId = currentLine.substring(0, 3);
				if (recordId.startsWith(fileLayoutConfig.getDetailFlag())) {
					try {
						bufferedWriter.write(currentLine);
						bufferedWriter.newLine();
					}
					catch (IOException e) {
						log.error(e.getMessage(), e);
					}
					// totalDetailRecord++;
				}
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

	private Map<String, List<? extends FileLayoutConfigItem>> prepareConfigData(
			List<? extends FileLayoutConfigItem> list) {

		Map<String, List<? extends FileLayoutConfigItem>> fileLayoutMapping = new HashMap<String, List<? extends FileLayoutConfigItem>>();
		List<FileLayoutConfigItem> detailList = new ArrayList<FileLayoutConfigItem>();

		for (FileLayoutConfigItem fileLayoutConfig : list) {
			if (fileLayoutConfig.getRecordType().equals(DETAIL)) {
				detailList.add(fileLayoutConfig);
			}

		}

		fileLayoutMapping.put(DETAIL, detailList);

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
				T drawdownAdvice = convertDetail(currentLine);
				detailResult.setSuccess(true);
				detailResult.setObjectValue(drawdownAdvice);
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

	public T convertDetail(String currentLine) {

		List<? extends FileLayoutConfigItem> fileLayoutConfigs = fileConfigItems
				.get(DETAIL);

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
					if (StringUtils.isBlank(data) && (!config.getFieldName()
							.equals("returnCode")
							&& !config.getFieldName().equals("returnMessage")
							&& !config.getFieldName().equals("interestCode")
							&& !config.getFieldName().equals("interestSpread")
							&& !config.getFieldName().equals("allInterestRate"))) {
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
}
