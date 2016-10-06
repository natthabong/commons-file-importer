package gec.scf.file.validator;

import java.util.List;

public class DataValidatorCreation {

	public DataValidator getDataValidator(List<? extends DataCondition> conditionals,
			ConditionMatchingFactory conditionMatchingFactory) {

		DataValidator dataValidator = new DataValidator(conditionals);
		dataValidator.setConditionMatchingFactory(conditionMatchingFactory);

		return dataValidator;
	}
}
