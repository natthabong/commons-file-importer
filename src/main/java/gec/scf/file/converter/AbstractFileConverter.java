package gec.scf.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DateValidator;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;

public abstract class AbstractFileConverter<T> implements FileConverter<T> {

	private Class<T> entityClass;

	private FileLayoutConfig fileLayoutConfig;

	public AbstractFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this.setFileLayoutConfig(fileLayoutConfig);
		this.entityClass = clazz;
	}

	protected void applyObjectValue(String recordValue, FileLayoutConfigItem itemConf, T document)
			throws NoSuchFieldException, ParseException, IllegalAccessException, WrongFormatDetailException,
			WrongFormatFileException {

		if (itemConf.isEntityField()) {
			Field field = null;
			field = entityClass.getDeclaredField(itemConf.getFieldName());
			field.setAccessible(true);
			Class<?> classType = field.getType();

			if (classType.isAssignableFrom(Date.class)) {

				validateDateFormat(itemConf, recordValue);

				SimpleDateFormat sdf = new SimpleDateFormat(itemConf.getDatetimeFormat(), Locale.US);
				Date date = sdf.parse(recordValue);
				field.set(document, date);
			} else if (classType.isAssignableFrom(BigDecimal.class)) {
				validateBigDecimalFormat(itemConf, recordValue);
				BigDecimal valueAmount = getBigDecimalValue(itemConf, recordValue);
				field.set(document, valueAmount);
			} else {
				validateRequiredField(itemConf, recordValue);
				field.set(document, recordValue.trim());
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

	protected void validateDateFormat(FileLayoutConfigItem configItem, String data)
			throws WrongFormatDetailException, WrongFormatFileException {
		
		DateValidator dateValidator = DateValidator.getInstance();

		validateRequiredField(configItem, data);
		
		String dateZeroPatter = "00000000";
		if (data.trim().equals(dateZeroPatter)) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE, configItem.getDisplayValue()));
		}

		if (!dateValidator.isValid(data.trim(), configItem.getDatetimeFormat(), Locale.US)) {
			if (configItem.isEntityField()) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT, configItem.getDisplayValue(), data));
			} else {
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT, configItem.getDisplayValue(), data));
			}
		}
	}

	protected void validateRequiredField(FileLayoutConfigItem configItem, String data) {
		if (configItem.isRequired()) {
			if (StringUtils.isBlank(data)) {
				throw new WrongFormatDetailException(MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
						configItem.getDisplayValue()));
			} else if (data.length() > configItem.getLenght()) {
				throw new WrongFormatDetailException(MessageFormat.format(CovertErrorConstant.DATA_OVER_MAX_LENGTH,
						configItem.getDisplayValue(), data.length(), configItem.getLenght()));
			}
		}
	}

	protected void validateDecimalPlace(FileLayoutConfigItem itemConf, String recordValue) {
		String[] splitter = recordValue.toString().split("\\.");
		if (splitter.length > 1) {
			int decimalLength = splitter[1].length();
			if (decimalLength != itemConf.getDecimalPlace()) {
				throw new WrongFormatDetailException(MessageFormat.format(CovertErrorConstant.DIGIT_OVER_MAX_DIGIT,
						itemConf.getDisplayValue(), itemConf.getDecimalPlace()));
			}
		}
	}

	protected void validateBigDecimalFormat(FileLayoutConfigItem configItem, String data) {

		validateRequiredField(configItem, data);
		if (configItem.isCheckAmountZero()) {
			try {
				BigDecimal amount = new BigDecimal(data);
				if (amount.intValue() == 0) {
					throw new WrongFormatDetailException(MessageFormat
							.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE, configItem.getDisplayValue()));
				}
			} catch (NumberFormatException e) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT, configItem.getDisplayValue(), data));
			}
		}
		if (StringUtils.isBlank(configItem.getPlusSymbol()) && data.contains("+")) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT, configItem.getDisplayValue(), data));
		}
	}

	protected BigDecimal getBigDecimalValue(FileLayoutConfigItem configItem, String data)
			throws IllegalAccessException {
		data = data.trim();
		BigDecimal valueAmount = null;

		if (StringUtils.isNotBlank(configItem.getPlusSymbol()) && StringUtils.isNotBlank(configItem.getMinusSymbol())) {
			if (data.startsWith(configItem.getPlusSymbol())) {
				data = data.substring(1);
			} else if (data.startsWith(configItem.getMinusSymbol())) {
				data = "-" + data.substring(1);
			}
		}

		if (configItem.isUse1000Separator()) {
			data = data.replaceAll(",", "");
		}

		validateDecimalPlace(configItem, data);

		try {
			if (configItem.isUseDecimalPlace()) {
				String normalNumber = data.substring(0, (data.length() - configItem.getDecimalPlace()));
				String degitNumber = data.substring(data.length() - configItem.getDecimalPlace());
				valueAmount = new BigDecimal(normalNumber + "." + degitNumber).setScale(configItem.getDecimalPlace());
			} else {
				valueAmount = new BigDecimal(data);
			}
		} catch (NumberFormatException e) {
			throw new WrongFormatDetailException(
					MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT, configItem.getDisplayValue(), data));
		}

		return valueAmount;
	}

	protected void validateBinaryFile(InputStream fileContent) throws IOException, WrongFormatFileException {
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
			} else if (b >= 0x20 && b <= 0x7E) {
				ascii++;
			} else {
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
