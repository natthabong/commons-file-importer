package gec.scf.file.importer;

import java.util.Map;

public interface DataImporter {

	public void doImport(Object documents);

	public void setDisplayOfField(Map<String, String> displayOfField);
}
