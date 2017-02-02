package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;

public interface FieldValidatorFactory {

	FieldValidator create(FileLayoutConfigItem item);

}
