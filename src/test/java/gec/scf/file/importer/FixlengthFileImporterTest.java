package gec.scf.file.importer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import gec.scf.file.converter.FileConverter;
import gec.scf.file.exception.WrongFormatFileException;

public class FixlengthFileImporterTest {

	private static final String RECORD_ID_INVALID = "Record Id (HDR) invalide";
	FileImporter importer;
	private FileConverter fixLengthFileConverter;

	@Before
	public void init() {
		importer = new FileImporter();
		
		fixLengthFileConverter = Mockito.mock(FileConverter.class);
		importer.setConverter(fixLengthFileConverter);
	}
	
	@Test
	public void given_valid_file_content_when_import_the_file_then_status_should_be_success() {
		// Arrage
		InputStream validFileContent = Mockito.mock(InputStream.class);

		// Actual
		FileImporterResult actualResult = importer.doImport(validFileContent);

		// Assert
		assertEquals(ResultType.SUCCESS, actualResult.getStatus());
	}

	@Test
	public void given_wrong_format_file_content_when_import_the_file_then_status_should_be_fail()
			throws WrongFormatFileException {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);
		doThrow(new WrongFormatFileException()).when(fixLengthFileConverter).checkFileFormat(wrongFormatFileContent);

		// Actual
		FileImporterResult actualResult = importer.doImport(wrongFormatFileContent);

		// Assert
		assertEquals(ResultType.FAIL, actualResult.getStatus());
	}

	@Test
	public void given_wrong_format_file_content_when_import_the_file_then_total_fail_should_be_NULL()
			throws WrongFormatFileException {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);
		doThrow(new WrongFormatFileException()).when(fixLengthFileConverter).checkFileFormat(wrongFormatFileContent);

		// Actual
		FileImporterResult actualResult = importer.doImport(wrongFormatFileContent);

		// Assert
		assertNull(actualResult.getTotalFail());
	}
	
	@Test
	public void given_wrong_format_file_content_when_import_the_file_then_total_success_should_be_0()
			throws WrongFormatFileException {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);
		doThrow(new WrongFormatFileException()).when(fixLengthFileConverter).checkFileFormat(wrongFormatFileContent);

		// Actual
		FileImporterResult actualResult = importer.doImport(wrongFormatFileContent);

		// Assert
		assertEquals(0, actualResult.getTotalSuccess());
	}
	
	@Test
	public void given_wrong_format_file_content_when_import_the_file_then_should_has_an_error_message()
			throws WrongFormatFileException {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);

		WrongFormatFileException wrongFormatFileException = new WrongFormatFileException();
		wrongFormatFileException.setErrorMessage(RECORD_ID_INVALID);
		
		doThrow(wrongFormatFileException).when(fixLengthFileConverter).checkFileFormat(wrongFormatFileContent);

		// Actual
		FileImporterResult actualResult = importer.doImport(wrongFormatFileContent);
		
		// Assert
		List<ErrorLineDetail> errorLineDetails = actualResult.getErrorLineDetails();
		ErrorLineDetail errorLineDetail = errorLineDetails.get(0);
		
		assertEquals(RECORD_ID_INVALID, errorLineDetail.getErrorMessage());
	}
	
	@Test
	public void given_wrong_format_file_content_on_line_no_1_when_import_the_file_then_should_has_an_error_on_line_no_1()
			throws WrongFormatFileException {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);

		WrongFormatFileException wrongFormatFileException = new WrongFormatFileException();
		wrongFormatFileException.setErrorLineNo(1);
		
		doThrow(wrongFormatFileException).when(fixLengthFileConverter).checkFileFormat(wrongFormatFileContent);

		// Actual
		FileImporterResult actualResult = importer.doImport(wrongFormatFileContent);

		// Assert
		List<ErrorLineDetail> errorLineDetails = actualResult.getErrorLineDetails();
		ErrorLineDetail errorLineDetail = errorLineDetails.get(0);
		
		assertEquals(1, errorLineDetail.getErrorLineNo().intValue());
	}
}
