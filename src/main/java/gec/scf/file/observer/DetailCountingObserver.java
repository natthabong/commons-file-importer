package gec.scf.file.observer;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.FileObserver;

public class DetailCountingObserver implements FileObserver<Integer> {
	/**
	 * 
	 */
	private int total;

	public DetailCountingObserver(FileLayoutConfigItem fileLayoutConfigItem) {
	}

	@Override
	public RecordType getObserveSection() {
		return RecordType.DETAIL;
	}

	@Override
	public void observe(Object currentLine) {
		total++;

	}

	@Override
	public Integer getValue() {
		return Integer.valueOf(total);
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return null;
	}

}