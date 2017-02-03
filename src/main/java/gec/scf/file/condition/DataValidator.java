package gec.scf.file.condition;

import java.util.ArrayList;
import java.util.List;

import gec.scf.file.condition.exception.ConditionMismatchException;

public class DataValidator {

	private List<? extends DataCondition> conditions;

	private ConditionMatchingProvider conditionMatchingProvider;

	public DataValidator(List<? extends DataCondition> conditions) {
		this.setConditions(conditions);
	}

	public DataValidator() {

	}

	public <T> void validate(T object, Class<T> objectClass) throws DataInvalidException {

		if (conditions != null && !conditions.isEmpty()) {

			List<String> errors = new ArrayList<String>();
			for (DataCondition dataCondition : conditions) {

				try {
					ConditionMatching<T> conditionMatching = conditionMatchingProvider
							.create(dataCondition, objectClass);
					conditionMatching.match(object, dataCondition);
				}
				catch (ConditionMismatchException e) {
					errors.add(e.getMessage());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (!errors.isEmpty()) {
				throw new DataInvalidException(errors);
			}
		}

	}

	public List<? extends DataCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<? extends DataCondition> conditions) {
		this.conditions = conditions;
	}

	public ConditionMatchingProvider getConditionMatchingFactory() {
		return conditionMatchingProvider;
	}

	public void setConditionMatchingProvider(
			ConditionMatchingProvider conditionMatchingFactory) {
		this.conditionMatchingProvider = conditionMatchingFactory;
	}

}
