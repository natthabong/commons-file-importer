package gec.scf.file.converter;

import java.util.Map;

import gec.scf.file.exception.WrongFormatFileException;

public interface FieldValidator {

	void validate(String dataValidate) throws WrongFormatFileException;

	void setDataToValidate(Map<String, Object> dataToValidate);

}
