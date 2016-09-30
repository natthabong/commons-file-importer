package gec.scf.file.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gec.scf.file.converter.FileConverter;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.handler.ImportFileHandler;

public class FileImporter {

	private FileConverter<?> fileConverter;

	private ImportFileHandler importHandler;

	private int totalSuccess;
	private int totalFailed;

	private DataImporter dataImporter;

	public FileImporterResult doImport(InputStream documentFile) {
		totalSuccess = 0;

		String processNo = UUID.randomUUID().toString();
		dataImporter.setProcessNo(processNo);

		FileImporterResult importerResult = new FileImporterResult();
		importerResult.setProcessNo(processNo);

		List<ErrorLineDetail> errorLineDetailList = new ArrayList<ErrorLineDetail>();

		try {
			fileConverter.checkFileFormat(documentFile);

			DetailResult detailResult = null;
			totalFailed = 0;

			int lineCount = 0;
			while ((detailResult = fileConverter.getDetail()) != null) {
				lineCount++;
				if (detailResult.isSuccess()) {
					if (importHandler != null) {
						importHandler.onImportData(detailResult);
					}
					try {
						if (dataImporter != null) {
							dataImporter.doImport(detailResult.getObjectValue());
						}
						totalSuccess++;
					}
					catch (IllegalArgumentException e) {
						ErrorLineDetail errorLineDetail = new ErrorLineDetail();
						errorLineDetail.setErrorMessage(e.getMessage());
						errorLineDetail.setErrorLineNo(lineCount);
						errorLineDetailList.add(errorLineDetail);
						totalFailed++;
					}
				}
				else {
					if (importHandler != null) {
						importHandler.onConvertFailed(detailResult);
					}
					for(ErrorLineDetail errorDetail : detailResult.getErrorLineDetails()){
						ErrorLineDetail errorLineDetail = new ErrorLineDetail();
						errorLineDetail.setErrorMessage(errorDetail.getErrorMessage());
						errorLineDetail.setErrorLineNo(errorDetail.getErrorLineNo());
						errorLineDetailList.add(errorLineDetail);
					}
					totalFailed++;
				}
			}

			if (totalFailed == 0) {
				importerResult.setStatus(ResultType.SUCCESS);
			}
			else if (totalSuccess == 0) {
				importerResult.setStatus(ResultType.FAIL);
			}
			else {
				importerResult.setStatus(ResultType.INCOMPLETE);
			}

			importerResult.setTotalSuccess(totalSuccess);
			importerResult.setTotalFailed(totalFailed);
			if (errorLineDetailList.size() > 0) {
				importerResult.setErrorLineDetails(errorLineDetailList);
			}

		}
		catch (WrongFormatFileException e) {
			ErrorLineDetail errorLineDetail = new ErrorLineDetail();
			errorLineDetail.setErrorMessage(e.getErrorMessage());
			errorLineDetail.setErrorLineNo(e.getErrorLineNo());
			errorLineDetailList.add(errorLineDetail);
			importerResult.setErrorLineDetails(errorLineDetailList);
			importerResult.setTotalSuccess(totalSuccess);
			importerResult.setStatus(ResultType.FAIL);

		}
		return importerResult;
	}

	public void setConverter(FileConverter<?> fixLengthFileConverter) {
		this.fileConverter = fixLengthFileConverter;
	}

	public void setHandler(ImportFileHandler importHandler) {
		this.importHandler = importHandler;
	}

	public void setDataImporter(DataImporter dataImporter) {
		this.dataImporter = dataImporter;
	}
}
