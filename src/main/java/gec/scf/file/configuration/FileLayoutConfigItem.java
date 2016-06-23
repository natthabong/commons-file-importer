package gec.scf.file.configuration;

public interface FileLayoutConfigItem {

	String getFieldName();

	String getRecordType();

	Integer getStartIndex();

	Integer getLenght();

	String getDatetimeFormat();

	String getPlusSymbol();

	String getMinusSymbol();

	Integer getDecimalPlace();

}
