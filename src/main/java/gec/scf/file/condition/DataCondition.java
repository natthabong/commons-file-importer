package gec.scf.file.condition;

@Deprecated
public class DataCondition {

	private ConditionType conditionType;
	private String notExistErrorMessage;
	private String inactiveErrorMessage;
	private String errorMessage;

	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}

	public ConditionType getConditionType() {
		return conditionType;
	}

	public String getNotExistErrorMessage() {
		return notExistErrorMessage;
	}

	public String getInactiveErrorMessage() {
		return inactiveErrorMessage;
	}

	public void setNotExistErrorMessage(String notExistErrorMessage) {
		this.notExistErrorMessage = notExistErrorMessage;
	}

	public void setInactiveErrorMessage(String inactiveErrorMessage) {
		this.inactiveErrorMessage = inactiveErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
