package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.ErrorLineDetail;

public class FixedLengthFileConverterGetDetailTest {

	private boolean isTransient = false;

	@Test
	public void given_detail_valid_format_should_status_success()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("232112", document.getSupplierCode());
	}

	@Test
	public void given_corporate_code_mismatch_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "Dmak  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Corporate Code (mak) mismatch", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_corporate_code_is_blank_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "D     232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Corporate Code is required", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_supplier_code_has_space_when_get_detail_should_trim_data_and_status_success()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		SponsorDocument actualDocument = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("232112", actualDocument.getSupplierCode());
	}

	@Test
	public void given_supplier_code_is_blank_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK                      1122033             20160910201609010000000100000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000001                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Supplier Code is required", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_receipt_number_is_blank_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001                                   20160910201609010000000100000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Receipt Number is required", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_receipt_date_is_blank_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122034                     201609010000000100000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Receipt Date is required", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_receipt_date_invalid_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122034             20150229201609010000000100000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Receipt Date (20150229) invalid format",
				errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_document_date_has_value_is_0_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122034             20160229000000000000000100000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Document Due Date is required", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_document_due_date_invalid_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122034             20160229201502290000000100000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Document Due Date (20150229) invalid format",
				errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_document_amount_is_0_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             20160812201606010000000000000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "DMAK  MK001               1122033             20160812201606010000000000010001                                                                                                                                                                                                                              ";
		fixedLengthContent[3] = "T0000020000000000010000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Document Amount is required", errorLineDetail.getErrorMessage());
	}

	@Ignore
	@Test
	public void given_document_amount_has_dot_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             2016081220160601000000000100.001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000000100000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("Document Amount (000000000100.00) invalid format",
				errorLineDetail.getErrorMessage());
	}

	@Ignore
	@Test
	public void given_document_amount_and_document_due_date_invalid_format_when_get_detail_should_status_fail_and_has_error_detail_2_record()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             2016081220152601000000000100.001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000000100000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertEquals(2, actualResult.getErrorLineDetails().size());
	}

	@Ignore
	@Test
	public void given_document_amount_and_document_due_date_invalid_format_when_get_detail_should_status_fail_and_has_error_detail_2_message()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             2016081220152601000000000100.001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000000100000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		ErrorLineDetail errorLineDetail1 = actualResult.getErrorLineDetails().get(1);
		assertEquals("Document Due Date (20152601) invalid format",
				errorLineDetail.getErrorMessage());
		assertEquals("Document Amount (000000000100.00) invalid format",
				errorLineDetail1.getErrorMessage());
	}

	@Test
	public void given_document_amount_which_has_pading_charecter_is_space_when_get_detail_should_success()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             2016081220150106         1000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000001000001                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		DefaultFileLayoutConfigItem docAmountConfig = new DefaultFileLayoutConfigItem();
		docAmountConfig.setDocFieldName("documentAmount");
		docAmountConfig.setStartIndex(63);
		docAmountConfig.setLength(15);
		docAmountConfig.setPaddingCharacter(" ");
		docAmountConfig.setPaddingType(PaddingType.LEFT);
		docAmountConfig.setDecimalPlace(2);
		docAmountConfig.setRecordType(RecordType.DETAIL);
		docAmountConfig.setTransient(false);
		docAmountConfig.setRequired(true);
		docAmountConfig.setHasDecimalPlace(false);
		docAmountConfig.setDisplayValue("Document Amount");

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout(docAmountConfig);

		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);

		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
	}

	@Test
	public void given_document_type_not_found_in_file_when_import_the_file_should_set_default_value_to_field_document_type()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("INV", document.getDocumentType());
	}

	@Test
	public void given_supplier_code_is_transient_when_get_detail_then_supplier_code_should_is_null()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             20160812201611010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		isTransient = true;

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertNull(document.getSupplierCode());
	}

	@Test
	public void given_document_amount_flag_is_0_when_get_detail_then_document_amount_should_is_negative_value()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             20160812201611010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();
		FixedLengthFileConverter<SponsorDocument> fileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("-100000.00", document.getDocumentAmount().toString());
	}

	private FileLayoutConfig createFixedLengthFileLayout() {
		return createFixedLengthFileLayout(null);
	}

	private FileLayoutConfig createFixedLengthFileLayout(
			DefaultFileLayoutConfigItem otherConfig) {

		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.FIXED_LENGTH);
		fileLayout.setHeaderFlag("H");
		fileLayout.setDetailFlag("D");
		fileLayout.setFooterFlag("T");

		List<FileLayoutConfigItem> configItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem headerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		headerRecordTypeConfig.setDocFieldName("recordId");
		headerRecordTypeConfig.setStartIndex(1);
		headerRecordTypeConfig.setLength(1);
		headerRecordTypeConfig.setDisplayValue("Record Type");
		headerRecordTypeConfig.setRecordType(RecordType.HEADER);

		configItems.add(headerRecordTypeConfig);

		DefaultFileLayoutConfigItem filterConfig = new DefaultFileLayoutConfigItem();
		filterConfig.setDocFieldName("filter");
		filterConfig.setStartIndex(54);
		filterConfig.setLength(247);
		filterConfig.setRecordType(RecordType.HEADER);

		configItems.add(filterConfig);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setDocFieldName("recordId");
		detailRecordTypeConfig.setDisplayValue("Record Type");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(1);
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
		detailRecordTypeConfig.setTransient(isTransient);

		configItems.add(detailRecordTypeConfig);

		if (otherConfig == null) {
			DefaultFileLayoutConfigItem corporateCode = new DefaultFileLayoutConfigItem();
			corporateCode.setDocFieldName(null);
			corporateCode.setStartIndex(2);
			corporateCode.setLength(5);
			corporateCode.setExpectedValue("MAK");
			corporateCode.setRecordType(RecordType.DETAIL);
			corporateCode.setDisplayValue("Corporate Code");
			corporateCode.setTransient(isTransient);

			configItems.add(corporateCode);

			DefaultFileLayoutConfigItem supplierCode = new DefaultFileLayoutConfigItem();
			supplierCode.setDocFieldName("supplierCode");
			supplierCode.setStartIndex(7);
			supplierCode.setLength(20);
			supplierCode.setRequired(true);
			supplierCode.setRecordType(RecordType.DETAIL);
			supplierCode.setTransient(isTransient);
			supplierCode.setDisplayValue("Supplier Code");

			configItems.add(supplierCode);

			DefaultFileLayoutConfigItem receiptNumber = new DefaultFileLayoutConfigItem();
			receiptNumber.setDocFieldName("documentNo");
			receiptNumber.setStartIndex(27);
			receiptNumber.setLength(20);
			receiptNumber.setRequired(true);
			receiptNumber.setRecordType(RecordType.DETAIL);
			receiptNumber.setTransient(isTransient);
			receiptNumber.setDisplayValue("Receipt Number");

			configItems.add(receiptNumber);

			DefaultFileLayoutConfigItem receiptDate = new DefaultFileLayoutConfigItem();
			receiptDate.setDocFieldName("documentDate");
			receiptDate.setStartIndex(47);
			receiptDate.setLength(8);
			receiptDate.setRequired(true);
			receiptDate.setRecordType(RecordType.DETAIL);
			receiptDate.setTransient(isTransient);
			receiptDate.setDatetimeFormat("yyyyMMdd");
			receiptDate.setDisplayValue("Receipt Date");

			configItems.add(receiptDate);

			DefaultFileLayoutConfigItem documentDueDate = new DefaultFileLayoutConfigItem();
			documentDueDate.setDocFieldName("sponsorPaymentDate");
			documentDueDate.setStartIndex(55);
			documentDueDate.setLength(8);
			documentDueDate.setRequired(true);
			documentDueDate.setRecordType(RecordType.DETAIL);
			documentDueDate.setTransient(isTransient);
			documentDueDate.setDatetimeFormat("yyyyMMdd");
			documentDueDate.setDisplayValue("Document Due Date");

			configItems.add(documentDueDate);

			DefaultFileLayoutConfigItem docAmountFlagConfig = new DefaultFileLayoutConfigItem();
			docAmountFlagConfig.setDocFieldName("optionVarcharField2");
			docAmountFlagConfig.setStartIndex(78);
			docAmountFlagConfig.setLength(1);
			docAmountFlagConfig.setRecordType(RecordType.DETAIL);
			docAmountFlagConfig.setTransient(true);
			docAmountFlagConfig.setPlusSymbol("1");
			docAmountFlagConfig.setMinusSymbol("0");
			docAmountFlagConfig.setDisplayValue("Document Amount Flag");

			configItems.add(docAmountFlagConfig);

			DefaultFileLayoutConfigItem docAmountConfig = new DefaultFileLayoutConfigItem();
			docAmountConfig.setSignFlagConfig(docAmountFlagConfig);
			docAmountConfig.setDocFieldName("documentAmount");
			docAmountConfig.setStartIndex(63);
			docAmountConfig.setLength(15);
			docAmountConfig.setPaddingCharacter("0");
			docAmountConfig.setPaddingType(PaddingType.LEFT);
			docAmountConfig.setDecimalPlace(2);
			docAmountConfig.setRecordType(RecordType.DETAIL);
			docAmountConfig.setTransient(isTransient);
			docAmountConfig.setRequired(true);
			docAmountConfig.setHasDecimalPlace(false);
			docAmountConfig.setDisplayValue("Document Amount");

			configItems.add(docAmountConfig);

			DefaultFileLayoutConfigItem documentType = new DefaultFileLayoutConfigItem();
			documentType.setDocFieldName("documentType");
			documentType.setRecordType(RecordType.DETAIL);
			documentType.setDefaultValue("INV");
			documentType.setTransient(isTransient);

			configItems.add(documentType);
		}
		else {
			configItems.add(otherConfig);
		}

		DefaultFileLayoutConfigItem detailFilterConfig = new DefaultFileLayoutConfigItem();
		detailFilterConfig.setDocFieldName("filter");
		detailFilterConfig.setStartIndex(79);
		detailFilterConfig.setLength(222);
		detailFilterConfig.setRecordType(RecordType.DETAIL);
		detailFilterConfig.setTransient(true);

		configItems.add(detailFilterConfig);

		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		footerRecordTypeConfig.setDocFieldName("recordId");
		footerRecordTypeConfig.setDisplayValue("Record Type");
		footerRecordTypeConfig.setStartIndex(1);
		footerRecordTypeConfig.setLength(1);
		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerRecordTypeConfig);

		DefaultFileLayoutConfigItem footerTotalDocConfig = new DefaultFileLayoutConfigItem();
		footerTotalDocConfig.setDocFieldName("totalDocumentNumber");
		footerTotalDocConfig.setStartIndex(2);
		footerTotalDocConfig.setLength(6);
		footerTotalDocConfig.setDecimalPlace(0);
		footerTotalDocConfig.setPaddingCharacter("0");
		footerTotalDocConfig.setDecimalPlace(0);
		footerTotalDocConfig.setPaddingType(PaddingType.LEFT);
		footerTotalDocConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerTotalDocConfig);

		DefaultFileLayoutConfigItem footerDocAmountFlagConfig = new DefaultFileLayoutConfigItem();
		footerDocAmountFlagConfig.setDocFieldName("optionVarcharField3");
		footerDocAmountFlagConfig.setDisplayValue("Total Document Amount Flag");
		footerDocAmountFlagConfig.setStartIndex(23);
		footerDocAmountFlagConfig.setLength(1);
		footerDocAmountFlagConfig.setRecordType(RecordType.FOOTER);
		footerDocAmountFlagConfig.setPlusSymbol("1");
		footerDocAmountFlagConfig.setMinusSymbol("0");
		configItems.add(footerDocAmountFlagConfig);

		DefaultFileLayoutConfigItem footerDocAmountConfig = new DefaultFileLayoutConfigItem();
		footerDocAmountConfig.setDocFieldName("totalDocumentAmount");
		footerDocAmountConfig.setDisplayValue("Total Document Amount");
		footerDocAmountConfig.setStartIndex(8);
		footerDocAmountConfig.setLength(15);
		footerDocAmountConfig.setDecimalPlace(2);
		footerDocAmountConfig.setHasDecimalPlace(true);
		footerDocAmountConfig.setPaddingCharacter("0");
		footerDocAmountConfig.setPaddingType(PaddingType.LEFT);
		footerDocAmountConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerDocAmountConfig);

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setDocFieldName("filter");
		footerFilterConfig.setStartIndex(24);
		footerFilterConfig.setLength(277);
		footerFilterConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerFilterConfig);

		fileLayout.setConfigItems(configItems);

		setConfigDefault();

		return fileLayout;
	}

	public void setConfigDefault() {
		isTransient = false;
	}
}
