package gec.scf.file.configuration;

public interface FileLayoutConfigItem {

	String getDocFieldName();

	RecordType getRecordType();

	Integer getStartIndex();

	Integer getLenght();

	String getDatetimeFormat();

	String getPlusSymbol();

	String getMinusSymbol();

	Integer getDecimalPlace();

	String getDisplayValue();

	String getExpectedValue();

	boolean isRequired();

	boolean isTransient();

	String getDefaultValue();

	boolean has1000Separator();

	boolean hasDecimalPlace();

	PaddingType getPaddingType();

	String getPaddingCharacter();

	FileLayoutConfigItem getSignFlagConfig();

}
