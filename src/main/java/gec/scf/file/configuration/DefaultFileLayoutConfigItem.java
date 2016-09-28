package gec.scf.file.configuration;

public class DefaultFileLayoutConfigItem implements FileLayoutConfigItem {

	private int startIndex;

	private int length;

	private RecordType recordType;

	private String fieldName;

	private String displayValue;

	private String expectValue;

	private boolean required;

	private String datetimeFormat;

	private Integer decimalPlace;


	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public RecordType getRecordType() {
		return recordType;
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
		return datetimeFormat;
	}

	@Override
	public String getPlusSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMinusSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getDecimalPlace() {
		return decimalPlace;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;

	}

	public void setLength(int length) {
		this.length = length;

	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;

	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;

	}

	@Override
	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public String getExpectValue() {
		return expectValue;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public boolean isRequired() {
		return required;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;

	}

	public void setDatetimeFormat(String datetimeFormat) {
		this.datetimeFormat = datetimeFormat;
	}

	public int getLength() {
		return length;
	}

	public void setExpectValue(String expectValue) {
		this.expectValue = expectValue;

	}

	public void setDecimalPlace(Integer decimalPlace) {
		this.decimalPlace = decimalPlace;
	}

}
