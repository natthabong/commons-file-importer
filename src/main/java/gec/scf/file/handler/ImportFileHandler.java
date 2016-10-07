package gec.scf.file.handler;

import gec.scf.file.importer.DetailResult;

public interface ImportFileHandler<T> {

	public void onConvertFailed(DetailResult<T> detailResult);

}
