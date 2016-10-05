package gec.scf.file.validator;

import java.util.ArrayList;
import java.util.List;

import gec.scf.file.validator.exception.ConditionMismatchException;

public class DataValidator {

	private List<? extends DataCondition> conditions;

	private ConditionMatchingFactory conditionMatchingFactory;

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
					ConditionMatching<T> conditionMatching = conditionMatchingFactory
							.create(dataCondition, objectClass);
					conditionMatching.match(object);
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

	public ConditionMatchingFactory getConditionMatchingFactory() {
		return conditionMatchingFactory;
	}

	public void setConditionMatchingFactory(
			ConditionMatchingFactory conditionMatchingFactory) {
		this.conditionMatchingFactory = conditionMatchingFactory;
	}

}
