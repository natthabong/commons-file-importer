package gec.scf.file.converter;

import java.io.InputStream;

import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public interface FileConverter<T> {

	public void checkFileFormat(InputStream documentFile) throws WrongFormatFileException;

	public DetailResult<T> getDetail();

	@Deprecated
	public void setFieldValidatorFactory(FieldValidatorFactory fieldValidatorFactory);

}
