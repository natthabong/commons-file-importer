package gec.scf.file.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gec.scf.file.converter.FileConverter;
import gec.scf.file.exception.WrongFormatFileException;

public class FileImporter {
	private FileConverter fixLengthFileConverter;
	public FileImporterResult doImport(InputStream documentFile){
		FileImporterResult importerResult = new FileImporterResult();
		try {
			fixLengthFileConverter.checkFileFormat(documentFile);
			importerResult.setStatus(ResultType.SUCCESS);
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

}
