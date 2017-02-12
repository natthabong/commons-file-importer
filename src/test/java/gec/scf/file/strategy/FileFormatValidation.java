package gec.scf.file.strategy;

import java.util.List;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatFileException;

public interface FileFormatValidation {

	void validateLineDataLength(String lineData,
			List<? extends FileLayoutConfigItem> layoutConfigItems)
			throws WrongFormatFileException;
}
