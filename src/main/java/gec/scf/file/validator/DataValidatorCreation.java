package gec.scf.file.validator;

import java.util.List;

public class DataValidatorCreation {

	public DataValidator getDataValidator(List<DataCondition> conditionals) {
		DataValidator dataValidator = new DataValidator(conditionals);
		return dataValidator;
	}
}
