package gec.scf.file.condition;

import java.util.List;

public class DataValidatorFactory {

	public DataValidator getDataValidator(List<? extends DataCondition> conditionals,
			ConditionMatchingProvider conditionMatchingProvider) {

		DataValidator dataValidator = new DataValidator(conditionals);
		dataValidator.setConditionMatchingProvider(conditionMatchingProvider);

		return dataValidator;
	}
}
