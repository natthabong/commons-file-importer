package gec.scf.file.configuration;

public interface FileLayoutConfigItem {

	String getDocFieldName();

	RecordType getRecordType();

	Integer getStartIndex();

	Integer getLenght();

	String getDatetimeFormat();

	String getPositiveFlag();

	String getNegativeFlag();

	Integer getDecimalPlace();

	String getDisplayValue();

	String getExpectedValue();

	boolean isRequired();

	boolean isTransient();

	String getDefaultValue();

	Boolean has1000Separator();

	Boolean hasDecimalPlace();

	PaddingType getPaddingType();

	String getPaddingCharacter();

	FileLayoutConfigItem getSignFlagConfig();

}
