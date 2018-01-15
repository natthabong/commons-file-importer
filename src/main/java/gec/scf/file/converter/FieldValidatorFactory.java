package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.importer.domain.Channel;

public interface FieldValidatorFactory {

	FieldValidator create(FileLayoutConfigItem configItem , Channel channnel);

}
