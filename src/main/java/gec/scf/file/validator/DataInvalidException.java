package gec.scf.file.validator;

import java.util.Collection;

public class DataInvalidException extends Exception {

	private static final long serialVersionUID = 241195535007946400L;

	private Collection<String> errors;

	public Collection<String> getErrors() {
		return errors;
	}

	public void setErrors(Collection<String> errors) {
		this.errors = errors;
	}

}
