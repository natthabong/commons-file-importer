package gec.scf.file.converter;

public interface DataFinder {

	public String find(Object object);
	
	public FinderType getFinderType();

	//TODO WAIT FOR REFACTOR
	public void fixReturnFindResult(String specialVaule);
}
