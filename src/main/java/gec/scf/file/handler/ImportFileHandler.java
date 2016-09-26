package gec.scf.file.handler;

import gec.scf.file.importer.DetailResult;

public interface ImportFileHandler {

	public void onConvertFailed(DetailResult detailResult);

	public void onImportData(DetailResult detailResult);

}
