package gec.scf.file.condition;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import gec.scf.file.condition.exception.ConditionMismatchException;

@Deprecated
public class DataValidator {

	private List<? extends DataCondition> conditions;

	private ConditionMatchingProvider conditionMatchingProvider;

	private static Logger log = Logger.getLogger(DataValidator.class);

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
					
					if(conditionMatching!=null)
						conditionMatching.match(object, dataCondition);
				}
				catch (ConditionMismatchException e) {
					errors.add(e.getMessage());
				}
				catch (Exception e) {
					log.error(e.getMessage(), e);
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
