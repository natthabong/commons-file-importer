package gec.scf.file.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.ItemType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.validation.SummaryFieldValidator;

public abstract class AbstractFileConverter<T> implements FileConverter<T> {

	private Class<T> entityClass;

	private FileLayoutConfig fileLayoutConfig;

	protected FieldValidatorFactory fieldValidatorFactory;

	private Map<RecordType, List<FileLayoutConfigItem>> fileLayoutMapping = new EnumMap<RecordType, List<FileLayoutConfigItem>>(
			RecordType.class);

	private Map<RecordType, RecordTypeExtractor> extractors = new EnumMap<RecordType, RecordTypeExtractor>(
			RecordType.class);

	private Map<RecordType, Set<DataObserver<?>>> observers = new HashMap<RecordType, Set<DataObserver<?>>>();

	private Map<FileLayoutConfigItem, FieldValidator> validators = new HashMap<FileLayoutConfigItem, FieldValidator>();

	private Map<FileLayoutConfigItem, FieldValueSetter> fieldSetters = new HashMap<FileLayoutConfigItem, FieldValueSetter>();

	private Map<FileLayoutConfigItem, ValueAdjustment> adjustments = new HashMap<FileLayoutConfigItem, ValueAdjustment>();

	
	private static final Logger log = Logger.getLogger(AbstractFileConverter.class);

	public AbstractFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this(fileLayoutConfig, clazz, null);
	}

	public AbstractFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz,
			FieldValidatorFactory fieldValidatorFactory) {
		this.fileLayoutConfig = fileLayoutConfig;
		this.entityClass = clazz;
		this.fieldValidatorFactory = fieldValidatorFactory;
		if (fileLayoutConfig != null && fileLayoutConfig.getConfigItems() != null) {
			fileLayoutMapping = prepareConfiguration(fileLayoutConfig.getConfigItems());
		}

	}

	protected void applyObjectValue(T entity, FileLayoutConfigItem itemConfig,
			Object currentLine)
			throws NoSuchFieldException, SecurityException, WrongFormatDetailException,
			WrongFormatFileException, IllegalAccessException, ParseException {

		if (!itemConfig.isTransient()) {

			FieldValueSetter fieldSetter = fieldSetters.get(itemConfig);

			Object value = null;

			String tempDataString = null;

			if (StringUtils.isNotBlank(itemConfig.getDefaultValue())) {
				tempDataString = itemConfig.getDefaultValue();
			}
			else if (ItemType.DATA.equals(itemConfig.getItemType())) {
				if (fieldSetter instanceof DataObserver) {
					DataObserver<?> obs = (DataObserver<?>) fieldSetter;
					tempDataString = String.valueOf(obs.getValue());
				}
			}
			else {
				tempDataString = getCuttedData(itemConfig, currentLine);
				
				validateRequiredField(itemConfig, tempDataString);
				validateData(currentLine, itemConfig);

				if (StringUtils.isNotBlank(itemConfig.getExpectedValue())
						&& itemConfig.getValidationType() == null) {

					validateExpectedValue(itemConfig, tempDataString);
				}
			}

			//TODO refactor when bank use gecscf document field
			String docFieldName = null;
			if (StringUtils.isNotBlank(itemConfig.getDocumentFieldName())) {
				docFieldName = itemConfig.getDocumentFieldName();
			}else{
				docFieldName = itemConfig.getDocFieldName();
			}			
			
			if (StringUtils.isNotBlank(docFieldName)
					&& tempDataString != null) {

				validateDataLength(itemConfig, tempDataString);

				String signFlagData = null;
				if (itemConfig.getSignFlagConfig() != null) {
					FileLayoutConfigItem signFlagConfig = itemConfig.getSignFlagConfig();
					signFlagData = getCuttedData(signFlagConfig, currentLine);
				}

				Field field = entityClass.getDeclaredField(docFieldName);
				field.setAccessible(true);
				Class<?> classType = field.getType();

				if (classType.isAssignableFrom(Date.class)) {

					if ("CURRENT_DATE".equals(tempDataString)) {
						value = new Date();
					}
					else {
						validateDateFormat(itemConfig, tempDataString);
						SimpleDateFormat sdf = new SimpleDateFormat(itemConfig.getDatetimeFormat(), Locale.US);
						if(StringUtils.isNotBlank(tempDataString)){
							value = sdf.parse(tempDataString);
						}
					}
				}
				else if (classType.isAssignableFrom(BigDecimal.class)) {

					BigDecimal amountValue = getBigDecimalValue(itemConfig,
							tempDataString);
					if (amountValue != null) {
						if (StringUtils.isNotEmpty(signFlagData)) {
							amountValue = applySignFlag(amountValue, itemConfig,
									signFlagData);
						}
					}
					value = amountValue;
				}
				else {
					validateRequiredField(itemConfig, tempDataString);
					value = tempDataString.trim();
				}
				try {
					field.set(entity, value);

					if (itemConfig.getApplyValueFieldNames() != null) {
						for (String fieldName : itemConfig.getApplyValueFieldNames()) {
							try {
								Field cloneField = entityClass
										.getDeclaredField(fieldName);
								cloneField.setAccessible(true);
								cloneField.set(entity, value);
							}
							catch (Exception e) {
								log.warn(e.getMessage(), e);
							}
						}

					}
				}
				catch (Exception e) {
					/**
					 * TODO: manage exception
					 **/
				}
			}

			// Additional
			if (fieldSetter != null) {
				validateRequired(itemConfig, value);
				fieldSetter.setValue(entity, value);
			}

			// Apply SupplierId
			if (itemConfig.getExpectedValue() != null
					&& ValidationType.IN_CUSTOMER_CODE_GROUP
							.equals(itemConfig.getValidationType())) {
				fieldSetter.setValue(entity, value);
			}
		}
		else {

			// use in drawdown advice
			validateRequiredField(itemConfig, getCuttedData(itemConfig, currentLine));

			if (itemConfig.getValidationType() != null) {
				validateData(currentLine, itemConfig);
			}
		}

	}

	private void validateDataLength(FileLayoutConfigItem configItem, String data) {
		if (configItem.isRequired() && configItem.getLenght() != null) {
			if (StringUtils.length(data) > configItem.getLenght()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.DATA_OVER_MAX_LENGTH,
								configItem.getDisplayValue(), data.length(),
								configItem.getLenght()));
			}

		}
	}

	private void validateRequired(FileLayoutConfigItem itemConfig, Object value) {

		if (itemConfig.isRequired()) {
			if (value == null) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
								itemConfig.getDisplayValue()));
			}
		}

	}

	protected void validateLineDataFormat(Object currentLine,
			List<FileLayoutConfigItem> configItems) throws WrongFormatFileException {

		validateLineDataLength(currentLine, configItems);

		for (FileLayoutConfigItem configItem : configItems) {

			String dataValidate = getCuttedData(configItem, currentLine);

			if (StringUtils.isNotBlank(configItem.getExpectedValue())
					&& configItem.getValidationType() == null) {

				validateExpectedValue(configItem, dataValidate);
			}

			if (StringUtils.isNotBlank(configItem.getDatetimeFormat())) {
				validateDateFormat(configItem, dataValidate);
			}

			validateData(currentLine, configItem);

		}
	}

	private void validateData(Object currentLine, FileLayoutConfigItem configItem)
			throws WrongFormatFileException {

		FieldValidator fieldValidator = getValidator(configItem);

		if (fieldValidator != null) {
			String dataToValidate = getCuttedData(configItem, currentLine);
			if (fieldValidator instanceof SummaryFieldValidator) {
				String totalAmoutData = getCuttedData(configItem, currentLine);
				try {

					BigDecimal footerTotalAmount = getBigDecimalValue(configItem,
							totalAmoutData);

					if (configItem.getSignFlagConfig() != null) {

						String signFlagData = getCuttedData(
								configItem.getSignFlagConfig(), currentLine);

						footerTotalAmount = applySignFlag(footerTotalAmount, configItem,
								signFlagData);

					}
					fieldValidator.validate(footerTotalAmount);
				}
				catch (WrongFormatFileException | WrongFormatDetailException e) {
					throw e;
				}
				catch (Exception e) {
					throw new WrongFormatFileException(
							MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
									configItem.getDisplayValue(), totalAmoutData));
				}
			}
			else {
				fieldValidator.validate(dataToValidate);
			}
		}
	}

	abstract protected void validateLineDataLength(Object currentLine,
			List<FileLayoutConfigItem> configItems) throws WrongFormatFileException;

	protected void validateExpectedValue(FileLayoutConfigItem config, String data)
			throws WrongFormatFileException {

		if (StringUtils.isBlank(data)) {
			String errorMessage = MessageFormat.format(
					CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
					config.getDisplayValue());
			throwErrorByRecordType(config, errorMessage);
		}
		if (!config.getExpectedValue().equals(data.trim())) {
			String errorMessage = MessageFormat.format(
					CovertErrorConstant.MISMATCH_FORMAT, config.getDisplayValue(),
					data.trim());
			throwErrorByRecordType(config, errorMessage);
		}

	}

	private void throwErrorByRecordType(FileLayoutConfigItem item, String errorMessage)
			throws WrongFormatFileException {
		if (RecordType.DETAIL.equals(item.getRecordTypeData())) {
			throw new WrongFormatDetailException(errorMessage);
		}
		else {
			throw new WrongFormatFileException(errorMessage);
		}
	}

	abstract String getCuttedData(FileLayoutConfigItem itemConf, Object currentLine)
			throws WrongFormatFileException;

	protected long validateDocumentNo(FileLayoutConfigItem configItem, String data,
			Long lastDocumentNo) throws WrongFormatFileException {
		Long docNoValidate = Long.parseLong(data.trim());
		if (docNoValidate <= lastDocumentNo) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.DOCUMENT_NO_INVALID,
							configItem.getDisplayValue(), data));
		}
		return docNoValidate;
	}

	protected void validateDateFormat(FileLayoutConfigItem configItem, String data)
			throws WrongFormatDetailException, WrongFormatFileException {
		String dateZeroPatter = "00000000";
		DateValidator dateValidator = DateValidator.getInstance();

		validateRequiredField(configItem, data);

		if (data.trim().equals(dateZeroPatter)) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
							configItem.getDisplayValue()));
		}

		if (configItem.isRequired() && !dateValidator.isValid(data.trim(), configItem.getDatetimeFormat(),
				Locale.US)) {
			if (configItem.getRecordTypeData() == null
					|| RecordType.DETAIL.equals(configItem.getRecordTypeData())) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
								configItem.getDisplayValue(), data));
			}
			else {
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
								configItem.getDisplayValue(), data));
			}
		}
	}

	protected void validateRequiredField(FileLayoutConfigItem configItem, String data)
			throws WrongFormatFileException {
		if (configItem.isRequired()) {
			if (StringUtils.isBlank(data)) {
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
								configItem.getDisplayValue()));
			}
			else if (configItem.getLenght() != null
					&& StringUtils.length(data) > configItem.getLenght()) {
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.DATA_OVER_MAX_LENGTH,
								configItem.getDisplayValue(), data.length(),
								configItem.getLenght()));
			}
		}
	}

	private void validateBigDecimalFormat(FileLayoutConfigItem configItem, String data) {
		String number = data;
		if (StringUtils.isNotEmpty(configItem.getPaddingCharacter())) {

			String pattern = "^[" + configItem.getPaddingCharacter() + "+0-9,._]*$";

			if (StringUtils.isNumeric(configItem.getPaddingCharacter())) {
				pattern = "^[0-9,._]*$";
			}

			// Create a Pattern object
			Pattern r = Pattern.compile(pattern);

			// Now create matcher object
			Matcher m = r.matcher(data);
			if (!m.matches()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
								configItem.getDisplayValue(), data));
			}
			else {
				number = StringUtils.stripStart(data, configItem.getPaddingCharacter());
			}
		}

		if (configItem.getNegativeFlag() != null
				|| configItem.getPositiveFlag() != null) {
			validateSignFlag(configItem, data);
		}

		// Validate require field
		if (configItem.isRequired()) {
			try {
				// if (StringUtils.isEmpty(number)) {
				// number = "0";
				// }

				number = number.replaceAll(",", "");

				if (StringUtils.isEmpty(number)) {// number 0 is valid
					throw new WrongFormatDetailException(MessageFormat.format(
							CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
							configItem.getDisplayValue()));
				}
			}
			catch (NumberFormatException e) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
								configItem.getDisplayValue(), data));
			}
		}

		// Check decimal place
		if (Boolean.TRUE.equals(configItem.hasDecimalPlace())
				|| (configItem.hasDecimalPlace() == null && data.contains("."))) {

			String dataToCheck = data;
			validateDecimalPlace(configItem, dataToCheck);
		}
		else if (Boolean.FALSE.equals(configItem.hasDecimalPlace())
				&& data.contains(".")) {

			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
							configItem.getDisplayValue(), data));
		}

		// Check 1000 separator place
		if (Boolean.TRUE.equals(configItem.has1000Separator())
				|| (configItem.has1000Separator() == null && data.contains(","))) {

			String dataToCheck = data;
			validate1000Seperator(configItem, dataToCheck);
		}
		else if (Boolean.FALSE.equals(configItem.has1000Separator())
				&& data.contains(",")) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
							configItem.getDisplayValue(), data));
		}

	}

	private void validateSignFlag(FileLayoutConfigItem configItem, String dataToCheck) {

		String plusSymbol = StringUtils.defaultString(configItem.getPositiveFlag(),
				StringUtils.EMPTY);

		String minusSymbol = StringUtils.defaultString(configItem.getNegativeFlag(),
				StringUtils.EMPTY);

		String pattern = "^[" + plusSymbol + minusSymbol + "0-9,._]*$";

		Pattern r = Pattern.compile(pattern);

		// Now create matcher object
		Matcher m = r.matcher(dataToCheck);
		if (!m.matches()) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
							configItem.getDisplayValue(), dataToCheck));
		}
	}

	private void validate1000Seperator(FileLayoutConfigItem configItem,
			String dataToCheck) {

		// Remove
		dataToCheck = dataToCheck.replaceAll("\\.[0-9]+", "");

		String[] splitter = dataToCheck.toString().split(",");

		if (splitter.length > 1) {
			for (int index = 1; index < splitter.length; index++) {
				int decimalLength = splitter[index].length();
				if (decimalLength != 3) {
					throw new WrongFormatDetailException(
							MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
									configItem.getDisplayValue(), dataToCheck));
				}
			}
		}
	}

	protected BigDecimal getBigDecimalValue(FileLayoutConfigItem configItem, String data)
			throws IllegalAccessException {

		validateBigDecimalFormat(configItem, data);

		if (PaddingType.LEFT.equals(configItem.getPaddingType())) {
			data = StringUtils.stripStart(data, configItem.getPaddingCharacter());
		}

		if (StringUtils.isBlank(data) && !configItem.isRequired()) {
			return null;
		}

		validateBigDecimalFormat(configItem, data);

		BigDecimal valueAmount = null;
		try {

			if (StringUtils.isNotBlank(configItem.getPositiveFlag())
					&& data.startsWith(configItem.getPositiveFlag())) {
				data = data.substring(1);
			}

			if (StringUtils.isNotBlank(configItem.getNegativeFlag())
					&& data.startsWith(configItem.getNegativeFlag())) {
				data = "-" + data.substring(1);
			}

			String normalNumber = data;

			if (configItem.getDecimalPlace() != null
					&& (Boolean.FALSE.equals(configItem.hasDecimalPlace()) || configItem.hasDecimalPlace() == null)
					&& !normalNumber.contains(".")) {
				if (FileType.FIXED_LENGTH.equals(this.fileLayoutConfig.getFileType())) {
					normalNumber = data.substring(0, (data.length() - configItem.getDecimalPlace()));
					String degitNumber = data.substring(data.length() - configItem.getDecimalPlace());
					normalNumber = normalNumber + "." + degitNumber;
				}
			}

			normalNumber = normalNumber.replaceAll(",", "");

			valueAmount = new BigDecimal(normalNumber)
					.setScale(configItem.getDecimalPlace(), RoundingMode.HALF_UP);

		}
		catch (NumberFormatException e) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
							configItem.getDisplayValue(), data));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
							configItem.getDisplayValue(), data));
		}

		return valueAmount;
	}

	protected void validateDecimalPlace(FileLayoutConfigItem itemConf,
			String dataToCheck) {
		String[] splitter = dataToCheck.toString().split("\\.");
		if (splitter.length > 1) {
			// TODO: Implements wrong decimal place
			int decimalLength = splitter[1].length();
			if (decimalLength > itemConf.getDecimalPlace()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.DIGIT_OVER_MAX_DIGIT,
								itemConf.getDisplayValue(), itemConf.getDecimalPlace()));
			}
		}

	}

	protected BigDecimal applySignFlag(BigDecimal valueAmount,
			FileLayoutConfigItem configItem, String signFlagData) {

		FileLayoutConfigItem signFlagConfig = configItem.getSignFlagConfig();

		if (ValidationType.IN_MAPPING_TYPE_SIGN_FLAG.equals(signFlagConfig.getValidationType())) {
			ValueAdjustment adjustment = adjustments.get(signFlagConfig);
			valueAmount = (BigDecimal) adjustment.adjust(valueAmount, signFlagData);
		}
		else {
			if (signFlagData.equals(signFlagConfig.getNegativeFlag())) {
				valueAmount = valueAmount.multiply(new BigDecimal("-1"));
			}
			else if (signFlagData.equals(signFlagConfig.getPositiveFlag())) {
				valueAmount = valueAmount.multiply(new BigDecimal("1"));
			}
			else {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
								signFlagConfig.getDisplayValue(), signFlagData));
			}
		}

		return valueAmount;
	}

	protected InputStream validateBinaryFile(InputStream fileContent)
			throws IOException, WrongFormatFileException {
		File tempSourcFile = null;
		File tempCheckFile = null;
		InputStream targetStream = null;
		try {
			String uuid = UUID.randomUUID().toString();
			tempSourcFile = File.createTempFile("temp-source-file-" + uuid, ".txt");
			String absolutePath = tempSourcFile.getAbsolutePath();
			String filePath = absolutePath.substring(0,
					absolutePath.lastIndexOf(File.separator));
			tempCheckFile = new File(filePath,
					"temp-for-check-binary-file-" + uuid + ".txt");

			FileUtils.copyInputStreamToFile(fileContent, tempSourcFile);
			Files.copy(tempSourcFile.toPath(), tempCheckFile.toPath());
			targetStream = FileUtils.openInputStream(tempCheckFile);

			int size = targetStream.available();
			if (size > 1024) {
				size = 1024;
			}
			byte[] data = new byte[size];
			targetStream.read(data);
			targetStream.close();

			int ascii = 0;
			int other = 0;

			for (int i = 0; i < data.length; i++) {
				byte b = data[i];

				if (b < 0x09) {
					throw new WrongFormatFileException("is binary file");
				}

				if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) {
					ascii++;
				}
				else if (b >= 0x20 && b <= 0x7E) {
					ascii++;
				}
				else {
					other++;
				}
			}
			if (100 * other / (ascii + other) > 95) {
				throw new WrongFormatFileException("is binary file");
			}

		}
		catch (WrongFormatFileException e) {
			throw e;
		}
		finally {
			if (targetStream != null) {
				targetStream.close();
			}
			if (tempSourcFile != null && tempSourcFile.exists()) {
				fileContent = FileUtils.openInputStream(tempSourcFile);
				tempSourcFile.delete();
			}
			if (tempCheckFile != null && tempCheckFile.exists()) {
				tempCheckFile.delete();
			}
		}
		return fileContent;
	}

	protected Map<RecordType, List<FileLayoutConfigItem>> prepareConfiguration(
			List<? extends FileLayoutConfigItem> list) {

		Map<RecordType, List<FileLayoutConfigItem>> fileLayoutMapping = new EnumMap<RecordType, List<FileLayoutConfigItem>>(
				RecordType.class);
		List<FileLayoutConfigItem> headerList = new ArrayList<FileLayoutConfigItem>();
		List<FileLayoutConfigItem> detailList = new ArrayList<FileLayoutConfigItem>();
		List<FileLayoutConfigItem> footerList = new ArrayList<FileLayoutConfigItem>();

		for (FileLayoutConfigItem fileLayoutConfigItem : list) {
			
			//TODO refactor when bank use gecscf document field
			String docFieldName = null;
			if (StringUtils.isNotBlank(fileLayoutConfigItem.getDocumentFieldName())) {
				docFieldName = fileLayoutConfigItem.getDocumentFieldName();
			}else{
				docFieldName = fileLayoutConfigItem.getDocFieldName();
			}	
			
			if ("recordId".equals(docFieldName)) {
				RecordTypeExtractor extractor = new RecordTypeExtractor(
						fileLayoutConfigItem);
				extractors.put(fileLayoutConfigItem.getRecordTypeData(), extractor);
			}
			else {

				if (fieldValidatorFactory != null
						&& fileLayoutConfigItem.getValidationType() != null) {

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

						if (fieldValidator instanceof FieldValueSetter) {
							FieldValueSetter fileSetter = (FieldValueSetter) fieldValidator;
							fieldSetters.put(fileLayoutConfigItem, fileSetter);
						}
						
						if(fieldValidator instanceof ValueAdjustment){
							ValueAdjustment valueAdjustment = (ValueAdjustment) fieldValidator;
							adjustments.put(fileLayoutConfigItem, valueAdjustment);
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

	protected static class RecordTypeExtractor {

		private FileLayoutConfigItem configItem;
		private static final Logger log = Logger.getLogger(RecordTypeExtractor.class);

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
			} catch (IndexOutOfBoundsException e) {
				log.debug(e.getMessage());
			}
			return recordType;
		}

	}

	protected RecordTypeExtractor getExtractor(RecordType recordType) {
		return extractors.get(recordType);
	}

	protected List<FileLayoutConfigItem> getFileLayoutMappingFor(RecordType recordType) {
		return fileLayoutMapping.get(recordType);
	}

	public FileLayoutConfig getFileLayoutConfig() {
		return fileLayoutConfig;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Set<DataObserver<?>> getObservers(RecordType recordType) {
		return observers.get(recordType);
	}

	public FieldValidator getValidator(FileLayoutConfigItem configItem) {
		return validators.get(configItem);
	}
}
