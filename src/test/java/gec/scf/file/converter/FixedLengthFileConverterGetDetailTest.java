package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	@Test
	public void given_detail_valid_format_should_status_success() throws WrongFormatFileException {
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
	public void given_corporate_code_mismatch_should_status_fail() throws WrongFormatFileException {
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
	public void given_corporate_code_is_blank_should_status_fail() throws WrongFormatFileException {
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
	public void given_supplier_code_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK                      1122033             20160910201609010000000100000001                                                                                                                                                                                                                              ";
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
		assertEquals("Supplier Code is required", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_receipt_number_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
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
	public void given_receipt_date_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
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
	public void given_receipt_date_invalid_when_get_detail_should_status_fail() throws WrongFormatFileException {
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
		assertEquals("Receipt Date (20150229) invalid format", errorLineDetail.getErrorMessage());
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
	public void given_document_due_date_invalid_when_get_detail_should_status_fail() throws WrongFormatFileException {
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
		assertEquals("Document Due Date (20150229) invalid format", errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_document_amount_is_0_when_get_detail_should_status_fail() throws WrongFormatFileException {
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

@Test
	public void given_document_amount_has_dot_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             2016081220160601000000000100.001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "DMAK  MK001               1122033             20160812201606010000000000100001                                                                                                                                                                                                                              ";
		fixedLengthContent[3] = "T0000020000000000100000                                                                                                                                                                                                                                                                                     ";
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
		assertEquals("Document Due Date (20152601) invalid format", errorLineDetail.getErrorMessage());
		assertEquals("Document Amount (000000000100.00) invalid format", errorLineDetail1.getErrorMessage());
	}

	@Test
	public void given_document_amount_which_has_wrong_padding_charecter_A_invalid_format_when_get_detail_should_has_error_detail_invalid_format()
			throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  MK001               1122033             2016081220150106A000000001000001                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "DMAK  MK001               1122033             20160812201501060000000001000001                                                                                                                                                                                                                              ";
		fixedLengthContent[3] = "T0000020000000001000001                                                                                                                                                                                                                                                                                     ";
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
		assertEquals("Document Amount (A00000000100000) invalid format",
				errorLineDetail.getErrorMessage());
	}

	@Test
	public void given_document_type_not_found_in_file_when_import_the_file_should_set_constance_value_to_field_document_type()
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
		headerRecordTypeConfig.setTransient(true);
		headerRecordTypeConfig.setRecordType(RecordType.HEADER);

		configItems.add(headerRecordTypeConfig);

		DefaultFileLayoutConfigItem filterConfig = new DefaultFileLayoutConfigItem();
		filterConfig.setDocFieldName("filter");
		filterConfig.setStartIndex(54);
		filterConfig.setLength(247);
		filterConfig.setTransient(true);
		filterConfig.setRecordType(RecordType.HEADER);

		configItems.add(filterConfig);

		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		footerRecordTypeConfig.setDocFieldName("recordId");
		footerRecordTypeConfig.setDisplayValue("Record Type");
		footerRecordTypeConfig.setStartIndex(1);
		footerRecordTypeConfig.setLength(1);
		footerRecordTypeConfig.setTransient(true);
		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerRecordTypeConfig);

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setDocFieldName("filter");
		footerFilterConfig.setStartIndex(54);
		footerFilterConfig.setLength(247);
		footerFilterConfig.setTransient(true);
		footerFilterConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerFilterConfig);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setDocFieldName("recordId");
		detailRecordTypeConfig.setDisplayValue("Record Type");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(1);
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
		detailRecordTypeConfig.setTransient(true);
		
		configItems.add(detailRecordTypeConfig);

		if (otherConfig == null) {

			DefaultFileLayoutConfigItem corporateCode = new DefaultFileLayoutConfigItem();
			corporateCode.setDocFieldName(null);
			corporateCode.setStartIndex(2);
			corporateCode.setLength(5);
			corporateCode.setExpectedValue("MAK");
			corporateCode.setRecordType(RecordType.DETAIL);
			corporateCode.setDisplayValue("Corporate Code");
			corporateCode.setTransient(false);

			configItems.add(corporateCode);

			DefaultFileLayoutConfigItem supplierCode = new DefaultFileLayoutConfigItem();
			supplierCode.setDocFieldName("supplierCode");
			supplierCode.setStartIndex(7);
			supplierCode.setLength(20);
			supplierCode.setRequired(true);
			supplierCode.setRecordType(RecordType.DETAIL);
			supplierCode.setTransient(false);
			supplierCode.setDisplayValue("Supplier Code");

			configItems.add(supplierCode);

			DefaultFileLayoutConfigItem receiptNumber = new DefaultFileLayoutConfigItem();
			receiptNumber.setDocFieldName("documentNo");
			receiptNumber.setStartIndex(27);
			receiptNumber.setLength(20);
			receiptNumber.setRequired(true);
			receiptNumber.setRecordType(RecordType.DETAIL);
			receiptNumber.setTransient(false);
			receiptNumber.setDisplayValue("Receipt Number");

			configItems.add(receiptNumber);

			DefaultFileLayoutConfigItem receiptDate = new DefaultFileLayoutConfigItem();
			receiptDate.setDocFieldName("documentDate");
			receiptDate.setStartIndex(47);
			receiptDate.setLength(8);
			receiptDate.setRequired(true);
			receiptDate.setRecordType(RecordType.DETAIL);
			receiptDate.setTransient(false);
			receiptDate.setDatetimeFormat("yyyyMMdd");
			receiptDate.setDisplayValue("Receipt Date");

			configItems.add(receiptDate);

			DefaultFileLayoutConfigItem documentDueDate = new DefaultFileLayoutConfigItem();
			documentDueDate.setDocFieldName("sponsorPaymentDate");
			documentDueDate.setStartIndex(55);
			documentDueDate.setLength(8);
			documentDueDate.setRequired(true);
			documentDueDate.setRecordType(RecordType.DETAIL);
			documentDueDate.setTransient(false);
			documentDueDate.setDatetimeFormat("yyyyMMdd");
			documentDueDate.setDisplayValue("Document Due Date");

			configItems.add(documentDueDate);

			DefaultFileLayoutConfigItem docAmountConfig = new DefaultFileLayoutConfigItem();
			docAmountConfig.setDocFieldName("documentAmount");
			docAmountConfig.setStartIndex(63);
			docAmountConfig.setLength(15);
			docAmountConfig.setPaddingCharacter("0");
			docAmountConfig.setPaddingType(PaddingType.LEFT);
			docAmountConfig.setDecimalPlace(2);
			docAmountConfig.setRecordType(RecordType.DETAIL);
			docAmountConfig.setTransient(false);
			docAmountConfig.setRequired(true);
			docAmountConfig.setHasDecimalPlace(false);
			docAmountConfig.setDisplayValue("Document Amount");

			configItems.add(docAmountConfig);

			DefaultFileLayoutConfigItem documentType = new DefaultFileLayoutConfigItem();
			documentType.setDocFieldName("documentType");
			documentType.setRecordType(RecordType.DETAIL);
			documentType.setDefaultValue("INV");
			documentType.setTransient(false);

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

		fileLayout.setConfigItems(configItems);

		return fileLayout;

	}
}
