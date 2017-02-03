package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;

public interface DataObserver<T> {

	RecordType getObserveSection();

	void observe(Object currentLine);

	T getValue();

	FileLayoutConfigItem getObserveFieldConfig();

}
