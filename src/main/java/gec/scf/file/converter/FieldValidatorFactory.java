package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.observer.DateTimeFieldValidator;
import gec.scf.file.observer.DetailCountingValidator;
import gec.scf.file.observer.SummaryFieldValidator;

public class FieldValidatorFactory {

	FieldValidator create(FileLayoutConfigItem configItem) {
		FieldValidator fieldValidator = null;
		switch (configItem.getValidationType()) {
		case EQUAL_OR_GREATER_THAN_UPLOAD_DATE:
		case GREATER_THAN_UPLOAD_DATE:
		case EQUAL_OR_LESS_THAN_UPLOAD_DATE:
		case EQUAL_TO_UPLOAD_DATE:
		case LESS_THAN_UPLOAD_DATE:
			fieldValidator = new DateTimeFieldValidator(configItem);
			break;
		case COUNT_OF_DOCUMENT_DETAIL:
			fieldValidator = new DetailCountingValidator(configItem);
			break;
		case SUMMARY_OF_FIELD:
			fieldValidator = new SummaryFieldValidator(configItem);
			break;
		case EQUAL_TO_HEADER_FIELD:

		default:
			break;
		}

		return fieldValidator;
	}

}
