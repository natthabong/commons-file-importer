package gec.scf.file.converter;

import java.io.File;

import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public interface FileConverter<T> {

	public void checkFileFormat(File documentFile) throws WrongFormatFileException;

	public DetailResult getDetail();

}
