package gec.scf.file.importer;

import java.io.Serializable;

import gec.scf.file.exception.WrongFormatFileException;

public class ErrorLineDetail implements Serializable {

	private static final long serialVersionUID = -6887731652449627677L;

	private String errorMessage;

	private Integer errorLineNo;

	public ErrorLineDetail() {
	}

	public ErrorLineDetail(Integer errorLineNo, String errorMessage) {
		super();
		this.errorMessage = errorMessage;
		this.errorLineNo = errorLineNo;
	}

	public ErrorLineDetail(WrongFormatFileException e) {
		this(e.getErrorLineNo(), e.getMessage());
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getErrorLineNo() {
		return errorLineNo;
	}

	public void setErrorLineNo(Integer errorLineNo) {
		this.errorLineNo = errorLineNo;
	}

	@Override
	public String toString() {
		return "ErrorLineDetail [errorMessage=" + errorMessage + ", errorLineNo="
				+ errorLineNo + "]";
	}

}
