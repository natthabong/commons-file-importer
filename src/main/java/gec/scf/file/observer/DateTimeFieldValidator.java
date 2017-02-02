package gec.scf.file.observer;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.FileObserver;
import gec.scf.file.exception.WrongFormatFileException;

public class DateTimeFieldValidator implements FieldValidator {

	/**
	 * 
	 */
	private final FileLayoutConfigItem configItem;

	public DateTimeFieldValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern(configItem.getDatetimeFormat(), Locale.US);
		LocalDate documentDateTime = LocalDate.parse(String.valueOf(data), formatter);
		LocalDate currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);

		switch (configItem.getValidationType()) {
		case EQUAL_TO_UPLOAD_DATE:

			if (!documentDateTime.isEqual(currentDateTime)) {
				String errorMessage = MessageFormat.format(
						CovertErrorConstant.EQUAL_TO_UPLOAD_DATE_INVALID,
						configItem.getDisplayValue(), formatter.format(documentDateTime),
						formatter.format(currentDateTime));
				throw new WrongFormatFileException(errorMessage);
			}
			break;
		case EQUAL_OR_GREATER_THAN_UPLOAD_DATE:

			boolean isEqualCurrentUploadDate = false;
			boolean isGreaterThanCurrentDate = false;
			if (documentDateTime.isEqual(currentDateTime)) {
				isEqualCurrentUploadDate = true;
			}

			if (documentDateTime.isAfter(currentDateTime)) {
				isGreaterThanCurrentDate = true;
			}

			if (!isEqualCurrentUploadDate && !isGreaterThanCurrentDate) {
				String errorMessage = MessageFormat.format(
						CovertErrorConstant.EQUAL_OR_GREATER_THAN_UPLOAD_DATE_INVALID,
						configItem.getDisplayValue(), formatter.format(documentDateTime),
						formatter.format(currentDateTime));
				throw new WrongFormatFileException(errorMessage);
			}
			break;
		case GREATER_THAN_UPLOAD_DATE:
			if (!documentDateTime.isAfter(currentDateTime)) {
				String errorMessage = MessageFormat.format(
						CovertErrorConstant.GREATER_THAN_UPLOAD_DATE_INVALID,
						configItem.getDisplayValue(), formatter.format(documentDateTime),
						formatter.format(currentDateTime));
				throw new WrongFormatFileException(errorMessage);
			}
			break;

		case EQUAL_OR_LESS_THAN_UPLOAD_DATE:
			isEqualCurrentUploadDate = false;
			boolean isLessThanCurrentDate = false;
			if (documentDateTime.isEqual(currentDateTime)) {
				isEqualCurrentUploadDate = true;
			}

			if (documentDateTime.isBefore(currentDateTime)) {
				isLessThanCurrentDate = true;
			}

			if (!isEqualCurrentUploadDate && !isLessThanCurrentDate) {
				String errorMessage = MessageFormat.format(
						CovertErrorConstant.EQUAL_OR_LESS_THAN_UPLOAD_DATE_INVALID,
						configItem.getDisplayValue(), formatter.format(documentDateTime),
						formatter.format(currentDateTime));
				throw new WrongFormatFileException(errorMessage);
			}
			break;
		case LESS_THAN_UPLOAD_DATE:
			if (!documentDateTime.isBefore(currentDateTime)) {
				String errorMessage = MessageFormat.format(
						CovertErrorConstant.LESS_THAN_UPLOAD_DATE_INVALID,
						configItem.getDisplayValue(), formatter.format(documentDateTime),
						formatter.format(currentDateTime));
				throw new WrongFormatFileException(errorMessage);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void setObserver(FileObserver<?> fileObserver) {

	}

}