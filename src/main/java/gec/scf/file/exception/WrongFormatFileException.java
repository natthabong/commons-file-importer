package gec.scf.file.exception;

public class WrongFormatFileException extends Exception {

	private static final long serialVersionUID = 1067717305970974622L;
	private String errorMessage;
	private Integer errorLineNo;

	public WrongFormatFileException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public WrongFormatFileException() {
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorLineNo(Integer errorLineNo) {
		this.errorLineNo = errorLineNo;
	}

	public Integer getErrorLineNo() {
		return errorLineNo;
	}
}
