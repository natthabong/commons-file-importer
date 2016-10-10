package gec.scf.file.converter;

public interface DataReference {

	public String getFieldName();

	public FinderType getFinderType();

	public DataFinder getDataFinder();
}