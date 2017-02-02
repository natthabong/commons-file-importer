package gec.scf.file.converter;

import gec.scf.file.exception.WrongFormatFileException;

public interface FieldValidator  {

	void validate(Object dataValidate) throws WrongFormatFileException;

	void setObserver(FileObserver<?> fileObserver);

}
