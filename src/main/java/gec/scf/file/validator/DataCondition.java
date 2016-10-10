package gec.scf.file.validator;

public class DataCondition {

	private ConditionType conditionType;
	private String notExistErrorMessage;
	private String inactiveErrorMessage;

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

}
