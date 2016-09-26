package gec.scf.file.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gec.scf.file.converter.FileConverter;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.handler.ImportFileHandler;

public class FileImporter {

	private FileConverter fixLengthFileConverter;

	private ImportFileHandler importHandler;

	private int totalSuccess;
	private int totalFailed;

	public FileImporterResult doImport(InputStream documentFile) {
		totalSuccess = 0;

		FileImporterResult importerResult = new FileImporterResult();
		try {
			fixLengthFileConverter.checkFileFormat(documentFile);

			DetailResult detailResult = null;
			totalFailed = 0;

			while ((detailResult = fixLengthFileConverter.getDetail()) != null) {

				if (detailResult.isSuccess()) {
					if (importHandler != null) {
						importHandler.onImportData(detailResult);
					}
					totalSuccess++;
				}
				else {
					if (importHandler != null) {
						importHandler.onConvertFailed(detailResult);
					}
					totalFailed++;
				}
			}

			importerResult.setStatus(ResultType.SUCCESS);
			importerResult.setTotalSuccess(totalSuccess);
			importerResult.setTotalFailed(totalFailed);

		}
		catch (WrongFormatFileException e) {
			List<ErrorLineDetail> errorLineDetails = new ArrayList<ErrorLineDetail>();

			ErrorLineDetail errorLineDetail = new ErrorLineDetail();
			errorLineDetail.setErrorMessage(e.getErrorMessage());
			errorLineDetail.setErrorLineNo(e.getErrorLineNo());
			errorLineDetails.add(errorLineDetail);

			importerResult.setStatus(ResultType.FAIL);
			importerResult.setErrorLineDetails(errorLineDetails);
		}

		return importerResult;
	}

	public void setConverter(FileConverter fixLengthFileConverter) {
		this.fixLengthFileConverter = fixLengthFileConverter;
	}

	public void setHandler(ImportFileHandler importHandler) {
		this.importHandler = importHandler;
	}

}
