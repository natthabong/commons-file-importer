package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.importer.domain.ImportContext;

public interface FieldValidatorFactory {

	FieldValidator create(FileLayoutConfigItem configItem, ImportContext importContext);

}
