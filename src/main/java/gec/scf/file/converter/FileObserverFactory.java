package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;

public interface FileObserverFactory {

	FileObserver create(FileLayoutConfigItem fileLayoutConfigItem);

}
