package gec.scf.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DateValidator;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;

public abstract class AbstractFileConverter<T> implements FileConverter<T> {

	private Class<T> entityClass;

	private FileLayoutConfig fileLayoutConfig;

	public AbstractFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this.setFileLayoutConfig(fileLayoutConfig);
		this.entityClass = clazz;
	}

	protected void applyObjectValue(T entity, FileLayoutConfigItem itemConf,
			String recordValue, String signFlagData)
			throws NoSuchFieldException, SecurityException, WrongFormatDetailException,
			WrongFormatFileException, IllegalAccessException, ParseException {

		if (!itemConf.isTransient()) {
			Field field = null;
			field = entityClass.getDeclaredField(itemConf.getDocFieldName());
			field.setAccessible(true);
			Class<?> classType = field.getType();

			if (classType.isAssignableFrom(Date.class)) {

				validateDateFormat(itemConf, recordValue);

				SimpleDateFormat sdf = new SimpleDateFormat(itemConf.getDatetimeFormat(),
						Locale.US);
				Date date = sdf.parse(recordValue);
				field.set(entity, date);
			}
			else if (classType.isAssignableFrom(BigDecimal.class)) {

				BigDecimal valueAmount = getBigDecimalValue(itemConf, recordValue);

				if (StringUtils.isNotBlank(signFlagData)) {
					valueAmount = applySignFlag(valueAmount, itemConf, signFlagData);
				}
				field.set(entity, valueAmount);
			}
			else {
				validateRequiredField(itemConf, recordValue);
				field.set(entity, recordValue.trim());
			}
		}

	}

	protected long validateDocumentNo(FileLayoutConfigItem configItem, String data, Long lastDocumentNo)
			throws WrongFormatDetailException {
		Long docNoValidate = Long.parseLong(data.trim());
		if (docNoValidate <= lastDocumentNo) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.DOCUMENT_NO_INVALID, configItem.getDisplayValue(), data));
		}
		return docNoValidate;
	}

	protected void applyObjectValue(T entity, FileLayoutConfigItem config,
			String recordValue) throws NoSuchFieldException, ParseException,
			IllegalAccessException, WrongFormatDetailException, WrongFormatFileException {

		applyObjectValue(entity, config, recordValue, null);
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

		if (!dateValidator.isValid(data.trim(), configItem.getDatetimeFormat(),
				Locale.US)) {
			if (!configItem.isTransient()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
								configItem.getDisplayValue(), data));
			}
			else {
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
								configItem.getDisplayValue(), data));
			}
		}
	}

	protected void validateRequiredField(FileLayoutConfigItem configItem, String data) {
		if (configItem.isRequired()) {
			if (StringUtils.isBlank(data)) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
								configItem.getDisplayValue()));
			}
			else if (data.length() > configItem.getLenght()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.DATA_OVER_MAX_LENGTH,
								configItem.getDisplayValue(), data.length(),
								configItem.getLenght()));
			}
		}
	}

	private void validateBigDecimalFormat(FileLayoutConfigItem configItem, String data) {

		validateRequiredField(configItem, data);

		String normalNumber = data;

		if (configItem.hasDecimalPlace()) {
			validateDecimalPlace(configItem, normalNumber);

			normalNumber = data.substring(0,
					(data.length() - configItem.getDecimalPlace()));
			normalNumber = normalNumber.replaceAll("\\.", "");
		}
		else {
			if (normalNumber.indexOf("\\.") >= 0) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
								configItem.getDisplayValue(), normalNumber));
			}
		}

		if (configItem.has1000Separator()) {
			try {
				validate1000Seperator(normalNumber);
				normalNumber = normalNumber.replaceAll(",", "");
			}
			catch (IllegalArgumentException e) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
								configItem.getDisplayValue(), data));
			}
		}
		else {
			if (normalNumber.indexOf(",") >= 01) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
								configItem.getDisplayValue(), normalNumber));
			}
		}

		if (configItem.isRequired()) {
			try {
				BigDecimal amount = new BigDecimal(
						StringUtils.defaultString(normalNumber, "0"));
				if (amount.intValue() == 0) {
					throw new WrongFormatDetailException(MessageFormat.format(
							CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
							configItem.getDisplayValue()));
				}
			}
			catch (NumberFormatException e) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
								configItem.getDisplayValue(), data));
			}
		}

	}

	protected BigDecimal getBigDecimalValue(FileLayoutConfigItem configItem, String data)
			throws IllegalAccessException {

		data = data.trim();
		if (PaddingType.LEFT.equals(configItem.getPaddingType())) {
			data = StringUtils.stripStart(data, configItem.getPaddingCharacter());
		}

		validateBigDecimalFormat(configItem, data);

		BigDecimal valueAmount = null;
		try {

			if (StringUtils.isNotBlank(configItem.getPlusSymbol())
					&& data.startsWith(configItem.getPlusSymbol())) {
				data = data.substring(1);
			}

			if (StringUtils.isNotBlank(configItem.getMinusSymbol())
					&& data.startsWith(configItem.getMinusSymbol())) {
				data = "-" + data.substring(1);
			}

			String normalNumber = data;
			String degitNumber = "00";

			if (configItem.getDecimalPlace() != null) {
				normalNumber = data.substring(0,
						(data.length() - configItem.getDecimalPlace()));
				degitNumber = data
						.substring(data.length() - configItem.getDecimalPlace());
				normalNumber = normalNumber.replaceAll("\\.", "");

			}

			if (configItem.has1000Separator()) {
				normalNumber = normalNumber.replaceAll(",", "");

			}

			valueAmount = new BigDecimal(normalNumber + "." + degitNumber)
					.setScale(configItem.getDecimalPlace());

		}
		catch (NumberFormatException e) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
							configItem.getDisplayValue(), data));
		}

		return valueAmount;
	}

	protected void validateDecimalPlace(FileLayoutConfigItem itemConf,
			String recordValue) {
		String[] splitter = recordValue.toString().split("\\.");
		if (splitter.length > 1) {
			int decimalLength = splitter[1].length();
			if (decimalLength != itemConf.getDecimalPlace()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.DIGIT_OVER_MAX_DIGIT,
								itemConf.getDisplayValue(), itemConf.getDecimalPlace()));
			}
		}

	}

	protected BigDecimal applySignFlag(BigDecimal valueAmount,
			FileLayoutConfigItem configItem, String signFlagData) {

		FileLayoutConfigItem signFlagConfig = configItem.getSignFlagConfig();
		if (signFlagData.equals(signFlagConfig.getMinusSymbol())) {
			valueAmount = valueAmount.multiply(new BigDecimal("-1"));
		}
		else if (signFlagData.equals(signFlagConfig.getPlusSymbol())) {
			valueAmount = valueAmount.multiply(new BigDecimal("1"));
		}
		else {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
							signFlagConfig.getDisplayValue(), signFlagData));
		}
		return valueAmount;
	}

	private void validate1000Seperator(String normalNumber) {
		String[] splitter = normalNumber.toString().split("\\,");
		if (splitter.length > 1) {
			for (int index = 1; index < splitter.length; index++) {
				int decimalLength = splitter[index].length();
				if (decimalLength != 3) {
					throw new IllegalArgumentException();
				}
			}
		}

	}

	protected void validateBinaryFile(InputStream fileContent)
			throws IOException, WrongFormatFileException {
		int size = fileContent.available();
		if (size > 1024) {
			size = 1024;
		}
		byte[] data = new byte[size];
		fileContent.read(data);
		fileContent.close();

		int ascii = 0;
		int other = 0;

		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			if (b < 0x09) {
				throw new WrongFormatFileException("Data is binary file");
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
			throw new WrongFormatFileException("Data is binary file");
		}
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
}
