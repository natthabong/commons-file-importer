package gec.scf.file.observer;

import java.math.BigDecimal;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.FileObserver;

public class SummaryFieldObserver implements FileObserver<BigDecimal> {

	/**
	 * 
	 */

	private BigDecimal value = new BigDecimal("0.0");

	private FileLayoutConfigItem aggregationFieldConfig;

	public SummaryFieldObserver(FileLayoutConfigItem fileLayoutConfigItem) {
		this.aggregationFieldConfig = fileLayoutConfigItem
				.getValidationRecordFieldConfig();
	}

	@Override
	public RecordType getObserveSection() {
		return RecordType.DETAIL;
	}

	@Override
	public void observe(Object data) {
		value = value.add((BigDecimal) data);
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return aggregationFieldConfig;
	}

}