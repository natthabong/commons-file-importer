package gec.scf.file.configuration;

public interface FileLayoutConfigItem {

	String getFieldName();

	RecordType getRecordType();

	Integer getStartIndex();

	Integer getLenght();

	String getDatetimeFormat();

	String getPlusSymbol();

	String getMinusSymbol();

	Integer getDecimalPlace();

	String getDisplayValue();

	String getExpectValue();

	boolean isRequired();

	boolean isEntityField();

	boolean isCheckAmountZero();

	String getConstantValue();

	boolean isUse1000Separator();

	boolean isUseDecimalPlace();

}
