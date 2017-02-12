package gec.scf.file.strategy;

import java.util.List;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatFileException;

public class CSVFileFormatValidation implements FileFormatValidation {

	@Override
	public void validateLineDataLength(String lineData,
			List<? extends FileLayoutConfigItem> layoutConfigItems)
			throws WrongFormatFileException {

		

	}

}
