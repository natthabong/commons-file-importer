package gec.scf.file.importer;

import java.io.Serializable;

public class ErrorLineDetail implements Serializable{

	private static final long serialVersionUID = -6887731652449627677L;
	private String errorMessage;
	private Integer errorLineNo;

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

}
