package gec.scf.file.validator;

import java.util.Collection;
import java.util.List;

public class DataInvalidException extends Exception {

	private static final long serialVersionUID = 241195535007946400L;

	private Collection<String> errors;

	public DataInvalidException() {
		super();
	}

	public DataInvalidException(List<String> errors) {
		super();
		this.errors = errors;

	}

	public Collection<String> getErrors() {
		return errors;
	}

	public void setErrors(Collection<String> errors) {
		this.errors = errors;
	}

}
