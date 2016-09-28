package gec.scf.file.exception;

import java.util.List;

import gec.scf.file.importer.ErrorLineDetail;

public class WrongFormatDetailException extends RuntimeException {

	private static final long serialVersionUID = 1567306686291944506L;
	private List<ErrorLineDetail> errorLineDetails;
	private String errorMessage ;

	public WrongFormatDetailException(List<ErrorLineDetail> errorLineDetails) {
		super();
		this.errorLineDetails = errorLineDetails;
	}

	public WrongFormatDetailException() {
	}

	public WrongFormatDetailException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<ErrorLineDetail> getErrorLineDetails() {
		return errorLineDetails;
	}

	public void setErrorLineDetails(List<ErrorLineDetail> errorLineDetails) {
		this.errorLineDetails = errorLineDetails;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
