package gec.scf.file.validator;

import java.util.List;

public class DataValidatorFactory {

	public DataValidator getDataValidator(List<? extends DataCondition> conditionals,
			ConditionMatchingProvider conditionMatchingProvider) {

		DataValidator dataValidator = new DataValidator(conditionals);
		dataValidator.setConditionMatchingProvider(conditionMatchingProvider);

		return dataValidator;
	}
}
