package gec.scf.file.condition.exception;

public class ConditionMismatchException extends Exception {

	private static final long serialVersionUID = 5165504380531795260L;

	public ConditionMismatchException(String errorMessage) {
		super(errorMessage);
	}

	public ConditionMismatchException() {
		this(null);
	}
}
