package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;

public class SponsorFileLayoutConfigItem implements FileLayoutConfigItem {

	private int length;
	private String fieldName;
	private int startIndex;

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public RecordType getRecordType() {
		return null;
	}

	@Override
	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public Integer getLenght() {
		return length;
	}

	@Override
	public String getDatetimeFormat() {
		return null;
	}

	@Override
	public String getPlusSymbol() {
		return null;
	}

	@Override
	public String getMinusSymbol() {
		return null;
	}

	@Override
	public Integer getDecimalPlace() {
		return null;
	}

	public void setLength(int length) {
		this.length = length;		
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;		
	}

}
