package gec.scf.file.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import gec.scf.file.converter.FileConverter;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.handler.ImportFileHandler;

public class FixlengthFileImporterTest {

	private static final String RECORD_ID_INVALID = "Record Id (HDR) invalide";
	FileImporter fileImporter;
	private FileConverter fixLengthFileConverter;

	@Before
	public void init() {
		fileImporter = new FileImporter();

		fixLengthFileConverter = Mockito.mock(FileConverter.class);
		fileImporter.setConverter(fixLengthFileConverter);
	}

	@Test
	public void given_valid_file_content_when_import_the_file_then_status_should_be_success() {
		// Arrage
		InputStream validFile = Mockito.mock(InputStream.class);

		// Actual
		FileImporterResult actualResult = fileImporter.doImport(validFile);

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
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

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
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

		// Assert
		assertNull(actualResult.getTotalFailed());
	}

	@Test
	public void given_wrong_format_file_content_when_import_the_file_then_total_success_should_be_0()
			throws WrongFormatFileException {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);
		doThrow(new WrongFormatFileException()).when(fixLengthFileConverter).checkFileFormat(wrongFormatFileContent);

		// Actual
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

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
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

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
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

		// Assert
		List<ErrorLineDetail> errorLineDetails = actualResult.getErrorLineDetails();
		ErrorLineDetail errorLineDetail = errorLineDetails.get(0);

		assertEquals(1, errorLineDetail.getErrorLineNo().intValue());
	}

	@Test
	public void given_detail_has_3_record_wrong_format_detail_1_record_when_import_the_file_total_fail_should_be_1() {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);

		DetailResult validLineDetail1 = new DetailResult();
		validLineDetail1.setLineNo(2);
		validLineDetail1.setSuccess(true);

		DetailResult validLineDetail2 = new DetailResult();
		validLineDetail2.setLineNo(4);
		validLineDetail2.setSuccess(true);

		DetailResult invalidLineDetail = new DetailResult();
		invalidLineDetail.setLineNo(3);
		invalidLineDetail.setSuccess(false);
		
		when(fixLengthFileConverter.getDetail()).thenReturn(validLineDetail1, invalidLineDetail, validLineDetail2,
				null);

		// Actual
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

		// Assert
		verify(fixLengthFileConverter, times(4)).getDetail();
		assertEquals(1, actualResult.getTotalFailed().intValue());
	}

	@Test
	public void given_detail_valid_all_record_when_import_the_file_should_total_success_3_record() {
		// Arrage
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);

		DetailResult validLineDetail1 = new DetailResult();
		validLineDetail1.setLineNo(2);
		validLineDetail1.setSuccess(true);

		DetailResult validLineDetail2 = new DetailResult();
		validLineDetail2.setLineNo(4);
		validLineDetail2.setSuccess(true);

		DetailResult validLineDetail3 = new DetailResult();
		validLineDetail3.setLineNo(3);
		validLineDetail3.setSuccess(true);

		when(fixLengthFileConverter.getDetail()).thenReturn(validLineDetail1, validLineDetail2, validLineDetail3, null);

		// Actual
		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);

		// Assert
		assertEquals(3, actualResult.getTotalSuccess());
		assertEquals(0, actualResult.getTotalFailed().intValue());
	}

	@Test
	public void given_a_wrong_format_detail_when_import_the_file_then_the_onConvertFailed_event_should_be_fired() {
		// Arrange
		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);

		DetailResult invalidLineDetail = createDetailResult(3, false);

		when(fixLengthFileConverter.getDetail()).thenReturn(invalidLineDetail, null, null);

		ImportFileHandler importFileHandler = Mockito.mock(ImportFileHandler.class);
		fileImporter.setHandler(importFileHandler);

		// Actual
		fileImporter.doImport(wrongFormatFileContent);

		// Assert
		ArgumentCaptor<DetailResult> captor = ArgumentCaptor.forClass(DetailResult.class);

		verify(importFileHandler, times(1)).onConvertFailed(captor.capture());
		DetailResult actualFailedDetail = captor.getValue();

		assertEquals(3, actualFailedDetail.getLineNo().intValue());
	}

	@Test
	public void given_a_valid_format_detail_when_import_the_file_then_the_onImportData_event_should_be_fired() {
		// Arrange
		InputStream validFormatFileContent = Mockito.mock(InputStream.class);

		DetailResult validLineDetail = createDetailResult(5, true);

		when(fixLengthFileConverter.getDetail()).thenReturn(validLineDetail, null, null);

		ImportFileHandler importFileHandler = Mockito.mock(ImportFileHandler.class);
		fileImporter.setHandler(importFileHandler);

		// Actual
		fileImporter.doImport(validFormatFileContent);

		// Assert
		ArgumentCaptor<DetailResult> captor = ArgumentCaptor.forClass(DetailResult.class);

		verify(importFileHandler, times(1)).onImportData(captor.capture());
		DetailResult actualFailedDetail = captor.getValue();

		assertEquals(new Integer(5), actualFailedDetail.getLineNo());
	}

//	@Test
//	public void given_a_wrong_format_detail_when_import_the_file_then_error_line_detail_list_should_have_size_3() {
//		// Arrage
//		InputStream wrongFormatFileContent = Mockito.mock(InputStream.class);
//
//		DetailResult inValidLineDetail1 = new DetailResult();
//		inValidLineDetail1.setLineNo(2);
//		inValidLineDetail1.setSuccess(false);
//
//		List<ErrorLineDetail> errors = new ArrayList<ErrorLineDetail>();
//		ErrorLineDetail errorLineDetail1 = new ErrorLineDetail();
//		errorLineDetail1.setErrorLineNo(2);
//		errorLineDetail1.setErrorMessage("SponsorRef invalid format");
//
//		ErrorLineDetail errorLineDetail2 = new ErrorLineDetail();
//		errorLineDetail2.setErrorLineNo(2);
//		errorLineDetail2.setErrorMessage("SupplierId invalid format");
//
//		errors.add(errorLineDetail1);
//		errors.add(errorLineDetail2);
//		inValidLineDetail1.setErrorLineDetails(errors);
//
//		when(fixLengthFileConverter.getDetail()).thenReturn(inValidLineDetail1, null);
//
//		// Actual
//		FileImporterResult actualResult = fileImporter.doImport(wrongFormatFileContent);
//
//		// Assert
//		assertEquals(2, actualResult.getErrorLineDetails());		
//
//	}

	@Test
	public void given_validate_detail_success_should_save_document() {
		// Arrange
		InputStream validFormatFileContent = Mockito.mock(InputStream.class);
		DataImporter dataImporter = Mockito.mock(DataImporter.class);
		Object document = new Object();
		
		DetailResult validLineDetail = createDetailResult(2, true);
		validLineDetail.setObjectValue(document);
		when(fixLengthFileConverter.getDetail()).thenReturn(validLineDetail, null);
		
		fileImporter.setDataImporter(dataImporter);
		// Actual
		fileImporter.doImport(validFormatFileContent);

		// Assert
		verify(dataImporter, times(1)).doImport(document);
	}

	private DetailResult createDetailResult(int lineNo, boolean isSuccess) {
		DetailResult invalidLineDetail = new DetailResult();
		invalidLineDetail.setLineNo(lineNo);
		invalidLineDetail.setSuccess(isSuccess);
		return invalidLineDetail;
	}
}
