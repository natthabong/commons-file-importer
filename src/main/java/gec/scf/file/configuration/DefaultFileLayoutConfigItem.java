package gec.scf.file.configuration;

public class DefaultFileLayoutConfigItem implements FileLayoutConfigItem {

	private Integer startIndex;

	private int length;

	private RecordType recordType;

	private String docFieldName;

	private String displayValue;

	private String expectedValue;

	private boolean isRequired;

	private String datetimeFormat;

	private String paddingCharacter;

	private PaddingType paddingType;

	private String plusSymbol;

	private String minusSymbol;

	private Integer decimalPlace;

	private boolean isTransient;
	
	private boolean hasDecimalPlace;
	
	private boolean has1000Separator;
	
	private String defaultValue;

	@Override
	public String getDocFieldName() {
		return docFieldName;
	}

	@Override
	public RecordType getRecordType() {
		return recordType;
	}

	@Override
	public Integer getStartIndex() {
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
		return plusSymbol;
	}

	@Override
	public String getMinusSymbol() {
		return minusSymbol;
	}

	@Override
	public Integer getDecimalPlace() {
		return decimalPlace;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public void setLength(int length) {
		this.length = length;

	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;

	}

	public void setDocFieldName(String docFieldName) {
		this.docFieldName = docFieldName;
	}

	@Override
	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public String getExpectedValue() {
		return expectedValue;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public boolean isRequired() {
		return isRequired;
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

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;

	}

	public void setDecimalPlace(Integer decimalPlace) {
		this.decimalPlace = decimalPlace;
	}

	public void setPaddingCharacter(String paddingCharacter) {
		this.paddingCharacter = paddingCharacter;

	}

	public String getPaddingCharacter() {
		return paddingCharacter;
	}

	public void setPaddingType(PaddingType paddingType) {
		this.paddingType = paddingType;
	}

	public PaddingType getPaddingType() {
		return paddingType;
	}

	public void setDecimalPlace(int decimalPlace) {
		this.decimalPlace = decimalPlace;
	}

	public void setMinusSymbol(String minusSymbol) {
		this.minusSymbol = minusSymbol;
	}

	@Override
	public boolean isTransient() {
		return isTransient;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean has1000Separator() {
		return has1000Separator;
	}

	@Override
	public boolean hasDecimalPlace() {
		return hasDecimalPlace;
	}

	public void setHasDecimalPlace(boolean hasDecimalPlace) {
		this.hasDecimalPlace = hasDecimalPlace;
	}

	public void setHas1000Separator(boolean has1000Separator) {
		this.has1000Separator = has1000Separator;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@Override
	public FileLayoutConfigItem getSignFlagConfig() {
		return null;
	}

	public void setPlusSymbol(String plusSymbol) {
		this.plusSymbol = plusSymbol;

	}

}
