package gec.scf.file.observer;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.FileObserver;

public class HeaderMatchingObserver implements FileObserver<String> {

	/**
	 * 
	 */

	private String value;

	private FileLayoutConfigItem matchingFieldConfig;

	public HeaderMatchingObserver(FileLayoutConfigItem fileLayoutConfigItem) {
		this.matchingFieldConfig = fileLayoutConfigItem.getValidationRecordFieldConfig();
	}

	@Override
	public RecordType getObserveSection() {
		return RecordType.HEADER;
	}

	@Override
	public void observe(Object data) {
		value = String.valueOf(data);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return matchingFieldConfig;
	}

}