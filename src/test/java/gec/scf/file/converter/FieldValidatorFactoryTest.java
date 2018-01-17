package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.importer.domain.ImportContext;
import gec.scf.file.validation.CloneHeaderData;
import gec.scf.file.validation.DateTimeFieldValidator;
import gec.scf.file.validation.DetailCountingValidator;
import gec.scf.file.validation.HeaderMatchingValidator;
import gec.scf.file.validation.SummaryFieldValidator;

public class FieldValidatorFactoryTest implements FieldValidatorFactory {

	@Override
	public FieldValidator create(FileLayoutConfigItem configItem , ImportContext context) {

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
			fieldValidator = new HeaderMatchingValidator(configItem);
			break;
		case CLONE_VALUE:
			fieldValidator = new CloneHeaderData(configItem);
			break;
		default:
			break;
		}

		return fieldValidator;
	}

}
