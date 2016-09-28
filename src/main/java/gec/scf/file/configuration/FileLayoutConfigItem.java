package gec.scf.file.configuration;

public interface FileLayoutConfigItem {

	String getFieldName();

	RecordType getRecordType();

	int getStartIndex();

	Integer getLenght();

	String getDatetimeFormat();

	String getPlusSymbol();

	String getMinusSymbol();

	Integer getDecimalPlace();

	String getDisplayOfField();

	String getExpectValue();

}
