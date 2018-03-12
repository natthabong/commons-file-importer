package gec.scf.file.condition;

import java.util.Collection;
import java.util.List;

public class DataInvalidException extends Exception {

	private static final long serialVersionUID = 241195535007946400L;

	private Collection<String> errors;

	private String message;

	public DataInvalidException() {
		super();
	}

	public DataInvalidException(String message) {
		this.message = message;
	}
	
	public DataInvalidException(List<String> errors) {
		super();
		this.errors = errors;
		if (errors != null && errors.size() > 0) {
			this.message = errors.get(0);
		}
	}

	public Collection<String> getErrors() {
		return errors;
	}

	public void setErrors(Collection<String> errors) {
		this.errors = errors;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
