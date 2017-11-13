package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
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
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.DrawdownDocument;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class DrawdownAdviceFileConverterGetDetailTest extends AbstractFixedLengthConverterTest {

	@Test
	@Ignore
	public void given_detail_valid_format_should_status_success() throws WrongFormatFileException {
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "HDR20160620DRAWDOWNADVICE      18             ";
		fixedLengthContent[1] = "DTLEDS000000000012BF14129316          2016112210221100025419            00031323            0000080000000000000000000001000000000101000B 2  Drawdown success                                                                                    1MOR       0000740000000000000+00000000000000000007.40000000                    ";
		fixedLengthContent[2] = "TLR0000036";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createDrawdownAdviceFixedLengthFileLayout();
		FixedLengthFileConverter<DrawdownDocument> fileConverter = new FixedLengthFileConverter<DrawdownDocument>(
				fileLayoutConfig, DrawdownDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		// Actual
		DetailResult<DrawdownDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		DrawdownDocument document = (DrawdownDocument) actualResult.getObjectValue();
		assertEquals("B", document.getReturnStatus());
	}

	// @Test
	// public void given_corporate_code_mismatch_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "Dmak 232112 1122031
	// 20160910201609010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Corporate Code (mak) mismatch",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void given_corporate_code_is_blank_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "D 232112 1122031
	// 20160910201609010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Corporate Code is required",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_supplier_code_has_space_when_get_detail_should_trim_data_and_status_success()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK 232112 1122031
	// 20160910201609010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// SponsorDocument actualDocument = (SponsorDocument)
	// actualResult.getObjectValue();
	// assertEquals("232112", actualDocument.getSupplierCode());
	// }
	//
	// @Test
	// public void
	// given_supplier_code_is_blank_when_get_detail_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK 1122033 20160910201609010000000100000001 ";
	// fixedLengthContent[2] = "T0000010000000100000001 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Supplier Code is required",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_receipt_number_is_blank_when_get_detail_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 20160910201609010000000100000001 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Receipt Number is required",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_receipt_date_is_blank_when_get_detail_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122034 201609010000000100000001 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Receipt Date is required",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_receipt_date_invalid_when_get_detail_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122034
	// 20150229201609010000000100000001 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Receipt Date (20150229) invalid format",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_document_date_has_value_is_0_when_get_detail_should_status_fail()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122034
	// 20160229000000000000000100000001 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Document Due Date is required",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_document_due_date_invalid_when_get_detail_should_status_fail()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122034
	// 20160229201502290000000100000001 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Document Due Date (20150229) invalid format",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_document_amount_is_0_when_get_detail_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[4];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122033
	// 20160812201606010000000000000001 ";
	// fixedLengthContent[2] = "DMAK MK001 1122033
	// 20160812201606010000000000010001 ";
	// fixedLengthContent[3] = "T0000020000000000010000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Document Amount is required",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Ignore
	// @Test
	// public void
	// given_document_amount_has_dot_when_get_detail_should_status_fail() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122033
	// 2016081220160601000000000100.001 ";
	// fixedLengthContent[2] = "T0000010000000000100000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// assertEquals("Document Amount (000000000100.00) invalid format",
	// errorLineDetail.getErrorMessage());
	// }
	//
	// @Ignore
	// @Test
	// public void
	// given_document_amount_and_document_due_date_invalid_format_when_get_detail_should_status_fail_and_has_error_detail_2_record()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122033
	// 2016081220152601000000000100.001 ";
	// fixedLengthContent[2] = "T0000010000000000100000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertEquals(2, actualResult.getErrorLineDetails().size());
	// }
	//
	// @Ignore
	// @Test
	// public void
	// given_document_amount_and_document_due_date_invalid_format_when_get_detail_should_status_fail_and_has_error_detail_2_message()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122033
	// 2016081220152601000000000100.001 ";
	// fixedLengthContent[2] = "T0000010000000000100000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// ErrorLineDetail errorLineDetail =
	// actualResult.getErrorLineDetails().get(0);
	// ErrorLineDetail errorLineDetail1 =
	// actualResult.getErrorLineDetails().get(1);
	// assertEquals("Document Due Date (20152601) invalid format",
	// errorLineDetail.getErrorMessage());
	// assertEquals("Document Amount (000000000100.00) invalid format",
	// errorLineDetail1.getErrorMessage());
	// }
	//
	// @Test
	// public void
	// given_document_amount_which_has_pading_charecter_is_space_when_get_detail_should_success()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122033 2016081220150106 1000001 ";
	// fixedLengthContent[2] = "T0000010000000001000001 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// DefaultFileLayoutConfigItem docAmountConfig = new
	// DefaultFileLayoutConfigItem();
	// docAmountConfig.setDocFieldName("documentAmount");
	// docAmountConfig.setStartIndex(63);
	// docAmountConfig.setLength(15);
	// docAmountConfig.setPaddingCharacter(" ");
	// docAmountConfig.setPaddingType(PaddingType.LEFT);
	// docAmountConfig.setDecimalPlace(2);
	// docAmountConfig.setRecordType(RecordType.DETAIL);
	// docAmountConfig.setTransient(false);
	// docAmountConfig.setRequired(true);
	// docAmountConfig.setHasDecimalPlace(false);
	// docAmountConfig.setDisplayValue("Document Amount");
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout(docAmountConfig);
	//
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	//
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertTrue(actualResult.isSuccess());
	// }
	//
	// @Test
	// public void
	// given_document_type_not_found_in_file_when_import_the_file_should_set_default_value_to_field_document_type()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[3];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK 232112 1122031
	// 20160910201609010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertTrue(actualResult.isSuccess());
	// SponsorDocument document = (SponsorDocument)
	// actualResult.getObjectValue();
	// assertEquals("INV", document.getDocumentType());
	// }
	//
	// // @Test
	// // public void
	// //
	// given_supplier_code_is_transient_when_get_detail_then_supplier_code_should_is_null()
	// // throws WrongFormatFileException {
	// // // Arrage
	// // String[] fixedLengthContent = new String[4];
	// // fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// // fixedLengthContent[1] = "DMAK MK001 1122033
	// // 20160812201611010000000100000000 ";
	// // fixedLengthContent[2] = "T0000010000000100000000 ";
	// // InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// // StringUtils.join(fixedLengthContent,
	// System.lineSeparator()).getBytes());
	// //
	// //// isTransient = true;
	// //
	// // FileLayoutConfig fileLayoutConfig =
	// // createDrawdownAdviceFixedLengthFileLayout();
	// // FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// // FixedLengthFileConverter<SponsorDocument>(
	// // fileLayoutConfig, SponsorDocument.class);
	// // fileConverter.checkFileFormat(fixedlengthFileContent);
	// // // Actual
	// // DetailResult<SponsorDocument> actualResult =
	// fileConverter.getDetail();
	// //
	// // // Assert
	// // assertTrue(actualResult.isSuccess());
	// // SponsorDocument document = (SponsorDocument)
	// // actualResult.getObjectValue();
	// // assertNull(document.getSupplierCode());
	// // }
	//
	// @Test
	// public void
	// given_document_amount_flag_is_0_when_get_detail_then_document_amount_should_is_negative_value()
	// throws WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[4];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122033
	// 20160812201611010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout();
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	// // Assert
	// assertTrue(actualResult.isSuccess());
	// SponsorDocument document = (SponsorDocument)
	// actualResult.getObjectValue();
	// assertEquals("-100000.00", document.getDocumentAmount().toString());
	// }
	//
	// @Test
	// public void should_map_supplier_code_to_supplier_id() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[4];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK002 1122033
	// 20160812201611010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// DefaultFileLayoutConfigItem supplierCode = new
	// DefaultFileLayoutConfigItem();
	// supplierCode.setDocFieldName("supplierCode");
	// supplierCode.setStartIndex(7);
	// supplierCode.setLength(20);
	// supplierCode.setRequired(true);
	// supplierCode.setRecordType(RecordType.DETAIL);
	// supplierCode.setTransient(false);
	// supplierCode.setDisplayValue("Supplier Code");
	// supplierCode.setValidationType(ValidationType.IN_CUSTOMER_CODE_GROUP);
	// supplierCode.setExpectedValue("1");
	//
	// FieldValidatorFactory fieldValidatorFactory = spy(new
	// FieldValidatorFactoryTest());
	//
	// FieldValidator supplierIdSetter = new
	// ExpectedCustomerCodeValidatorStub();
	// doReturn(supplierIdSetter).when(fieldValidatorFactory).create(eq(supplierCode));
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout(supplierCode);
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	//
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	// // Assert
	// assertTrue(actualResult.isSuccess());
	// SponsorDocument document = (SponsorDocument)
	// actualResult.getObjectValue();
	// assertEquals("002", document.getSupplierId());
	// }
	//
	// @Test
	// public void should_throw_when_customer_code_inactive() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[4];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK001 1122034
	// 20160812201611010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// DefaultFileLayoutConfigItem supplierCode = new
	// DefaultFileLayoutConfigItem();
	// supplierCode.setDocFieldName("supplierCode");
	// supplierCode.setStartIndex(7);
	// supplierCode.setLength(20);
	// supplierCode.setRequired(true);
	// supplierCode.setRecordType(RecordType.DETAIL);
	// supplierCode.setTransient(false);
	// supplierCode.setDisplayValue("Supplier Code");
	// supplierCode.setValidationType(ValidationType.IN_CUSTOMER_CODE_GROUP);
	// supplierCode.setExpectedValue("1");
	//
	// FieldValidatorFactory fieldValidatorFactory = spy(new
	// FieldValidatorFactoryTest());
	//
	// FieldValidator supplierIdSetter = new
	// ExpectedCustomerCodeValidatorStub();
	// doReturn(supplierIdSetter).when(fieldValidatorFactory).create(eq(supplierCode));
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout(supplierCode);
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	//
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLine = actualResult.getErrorLineDetails().get(0);
	// assertEquals("Supplier code (MK001) is inactive Supplier code",
	// errorLine.getErrorMessage());
	// }
	//
	// @Test
	// public void should_throw_when_customer_code_not_existed() throws
	// WrongFormatFileException {
	// // Arrage
	// String[] fixedLengthContent = new String[4];
	// fixedLengthContent[0] = "H20160927120000Siam Makro Plc. MAK 004 ";
	// fixedLengthContent[1] = "DMAK MK004 1122034
	// 20160812201611010000000100000000 ";
	// fixedLengthContent[2] = "T0000010000000100000000 ";
	// InputStream fixedlengthFileContent = new ByteArrayInputStream(
	// StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
	//
	// DefaultFileLayoutConfigItem supplierCodeConfig = new
	// DefaultFileLayoutConfigItem();
	// supplierCodeConfig.setDocFieldName("supplierCode");
	// supplierCodeConfig.setStartIndex(7);
	// supplierCodeConfig.setLength(20);
	// supplierCodeConfig.setRequired(true);
	// supplierCodeConfig.setRecordType(RecordType.DETAIL);
	// supplierCodeConfig.setTransient(false);
	// supplierCodeConfig.setDisplayValue("Supplier Code");
	// supplierCodeConfig.setValidationType(ValidationType.IN_CUSTOMER_CODE_GROUP);
	// supplierCodeConfig.setExpectedValue("1");
	//
	// FieldValidatorFactory fieldValidatorFactory = spy(new
	// FieldValidatorFactoryTest());
	//
	// FieldValidator supplierIdSetter = new
	// ExpectedCustomerCodeValidatorStub();
	// doReturn(supplierIdSetter).when(fieldValidatorFactory).create(eq(supplierCodeConfig));
	//
	// FileLayoutConfig fileLayoutConfig =
	// createDrawdownAdviceFixedLengthFileLayout(supplierCodeConfig);
	// FixedLengthFileConverter<SponsorDocument> fileConverter = new
	// FixedLengthFileConverter<SponsorDocument>(
	// fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory);
	// fileConverter.checkFileFormat(fixedlengthFileContent);
	//
	// // Actual
	// DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
	//
	// // Assert
	// assertFalse(actualResult.isSuccess());
	// ErrorLineDetail errorLine = actualResult.getErrorLineDetails().get(0);
	// assertEquals("Supplier code (MK004) is not exist Supplier code",
	// errorLine.getErrorMessage());
	// }

	protected FileLayoutConfig createDrawdownAdviceFixedLengthFileLayout() {
		return createDrawdownAdviceFixedLengthFileLayout(null);
	}

	protected FileLayoutConfig createDrawdownAdviceFixedLengthFileLayout(DefaultFileLayoutConfigItem otherConfig) {

		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.FIXED_LENGTH);
		fileLayout.setHeaderFlag("HDR");
		fileLayout.setDetailFlag("DTL");
		fileLayout.setFooterFlag("TLR");

		List<FileLayoutConfigItem> configItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem headerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		headerRecordTypeConfig.setDocumentFieldName("recordId");
		headerRecordTypeConfig.setStartIndex(1);
		headerRecordTypeConfig.setLength(3);
		headerRecordTypeConfig.setExpectedValue("HDR");
		headerRecordTypeConfig.setDisplayValue("Record Id");
		headerRecordTypeConfig.setRecordType(RecordType.HEADER);

		configItems.add(headerRecordTypeConfig);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setDocumentFieldName("recordId");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(3);
		detailRecordTypeConfig.setExpectedValue("DTL");
		detailRecordTypeConfig.setDisplayValue("Record Id");
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
		detailRecordTypeConfig.setTransient(true);

		configItems.add(detailRecordTypeConfig);

		if (otherConfig == null) {
			DefaultFileLayoutConfigItem transactionNo = new DefaultFileLayoutConfigItem();
			transactionNo.setDocumentFieldName("transactionNo");
			transactionNo.setStartIndex(4);
			transactionNo.setLength(15);
			transactionNo.setRequired(true);
			transactionNo.setExpectedValue(null);
			transactionNo.setRecordType(RecordType.DETAIL);
			transactionNo.setDisplayValue("GEC Transaction No");
			transactionNo.setTransient(false);
			transactionNo.setValidationType(ValidationType.FOUND_TRANSACTION_NO);
			configItems.add(transactionNo);

			DefaultFileLayoutConfigItem bankTransactionNo = new DefaultFileLayoutConfigItem();
			bankTransactionNo.setDocumentFieldName("bankTransactionNo");
			bankTransactionNo.setStartIndex(19);
			bankTransactionNo.setLength(20);
			bankTransactionNo.setRequired(true);
			bankTransactionNo.setExpectedValue(null);
			bankTransactionNo.setDisplayValue("Loan Transaction No");
			bankTransactionNo.setRecordType(RecordType.DETAIL);
			bankTransactionNo.setTransient(false);
			bankTransactionNo.setValidationType(null);
			configItems.add(bankTransactionNo);

			DefaultFileLayoutConfigItem bankTransactionTime = new DefaultFileLayoutConfigItem();
			bankTransactionTime.setDocumentFieldName("bankTransactionTime");
			bankTransactionTime.setStartIndex(39);
			bankTransactionTime.setLength(14);
			bankTransactionTime.setRequired(true);
			bankTransactionTime.setExpectedValue(null);
			bankTransactionTime.setDisplayValue("Transaction TimeStamp");
			bankTransactionTime.setRecordType(RecordType.DETAIL);
			bankTransactionTime.setTransient(false);
			bankTransactionTime.setValidationType(null);
			bankTransactionTime.setCalendarEra("A.D.");
			bankTransactionTime.setDatetimeFormat("yyyyMMddHHmmss");
			configItems.add(bankTransactionTime);

			DefaultFileLayoutConfigItem sponsorRef = new DefaultFileLayoutConfigItem();
			sponsorRef.setDocumentFieldName("sponsorRef");
			sponsorRef.setStartIndex(53);
			sponsorRef.setLength(20);
			sponsorRef.setRequired(true);
			sponsorRef.setExpectedValue(null);
			sponsorRef.setDisplayValue("Sponsor Ref");
			sponsorRef.setRecordType(RecordType.DETAIL);
			sponsorRef.setTransient(false);
			sponsorRef.setValidationType(ValidationType.FOUND_ORGANIZE);
			configItems.add(sponsorRef);

			DefaultFileLayoutConfigItem buyerSellerRef = new DefaultFileLayoutConfigItem();
			buyerSellerRef.setDocumentFieldName("buyerSellerRef");
			buyerSellerRef.setStartIndex(73);
			buyerSellerRef.setLength(20);
			buyerSellerRef.setRequired(true);
			buyerSellerRef.setExpectedValue(null);
			buyerSellerRef.setDisplayValue("Buyer/Seller Ref");
			buyerSellerRef.setRecordType(RecordType.DETAIL);
			buyerSellerRef.setTransient(false);
			buyerSellerRef.setValidationType(ValidationType.FOUND_ORGANIZE);
			configItems.add(buyerSellerRef);

			DefaultFileLayoutConfigItem drawdownAmount = new DefaultFileLayoutConfigItem();
			drawdownAmount.setDocumentFieldName("drawdownAmount");
			drawdownAmount.setStartIndex(93);
			drawdownAmount.setLength(12);
			drawdownAmount.setRequired(true);
			drawdownAmount.setExpectedValue(null);
			drawdownAmount.setDisplayValue("Drawdown Amount");
			drawdownAmount.setPaddingCharacter("");
			drawdownAmount.setPaddingType(null);
			drawdownAmount.setDecimalPlace(2);
			drawdownAmount.setHasDecimalPlace(false);
			drawdownAmount.setRecordType(RecordType.DETAIL);
			drawdownAmount.setTransient(false);
			configItems.add(drawdownAmount);

			DefaultFileLayoutConfigItem interestAmount = new DefaultFileLayoutConfigItem();
			interestAmount.setDocumentFieldName("interestAmount");
			interestAmount.setStartIndex(105);
			interestAmount.setLength(12);
			interestAmount.setRequired(true);
			interestAmount.setExpectedValue(null);
			interestAmount.setDisplayValue("Interest Amount");
			interestAmount.setPaddingCharacter("");
			interestAmount.setPaddingType(null);
			interestAmount.setDecimalPlace(2);
			interestAmount.setHasDecimalPlace(false);
			interestAmount.setRecordType(RecordType.DETAIL);
			interestAmount.setTransient(false);
			configItems.add(interestAmount);

			DefaultFileLayoutConfigItem returnStatus = new DefaultFileLayoutConfigItem();
			returnStatus.setDocumentFieldName("returnStatus");
			returnStatus.setStartIndex(136);
			returnStatus.setLength(2);
			returnStatus.setRequired(true);
			returnStatus.setExpectedValue(null);
			returnStatus.setDisplayValue("return Status");
			returnStatus.setRecordType(RecordType.DETAIL);
			returnStatus.setTransient(false);
			returnStatus.setValidationType(null);
			configItems.add(returnStatus);

			DefaultFileLayoutConfigItem interestFlag = new DefaultFileLayoutConfigItem();
			interestFlag.setDocumentFieldName(null);
			interestFlag.setStartIndex(241);
			interestFlag.setLength(1);
			interestFlag.setRequired(true);
			interestFlag.setExpectedValue(null);
			interestFlag.setDisplayValue("Interest Flag");
			interestFlag.setRecordType(RecordType.DETAIL);
			interestFlag.setTransient(true);
			interestFlag.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_FLAG);
			configItems.add(interestFlag);

			DefaultFileLayoutConfigItem interestBasis = new DefaultFileLayoutConfigItem();
			interestBasis.setDocumentFieldName(null);
			interestBasis.setStartIndex(252);
			interestBasis.setLength(19);
			interestBasis.setRequired(true);
			interestBasis.setExpectedValue(null);
			interestBasis.setDisplayValue("Interest Basis");
			interestBasis.setPaddingCharacter("");
			interestBasis.setPaddingType(null);
			interestBasis.setDecimalPlace(8);
			interestBasis.setHasDecimalPlace(false);
			interestBasis.setRecordType(RecordType.DETAIL);
			interestBasis.setTransient(true);
			interestBasis.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_BASIS);
			configItems.add(interestBasis);

			DefaultFileLayoutConfigItem interestSpread = new DefaultFileLayoutConfigItem();
			interestSpread.setDocumentFieldName(null);
			interestSpread.setStartIndex(252);
			interestSpread.setLength(19);
			interestSpread.setRequired(true);
			interestSpread.setExpectedValue(null);
			interestSpread.setDisplayValue("Interest Spread");
			interestSpread.setPaddingCharacter("");
			interestSpread.setPaddingType(null);
			interestSpread.setDecimalPlace(8);
			interestSpread.setHasDecimalPlace(false);
			interestSpread.setRecordType(RecordType.DETAIL);
			interestSpread.setTransient(true);
			interestSpread.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_SPREAD);
			configItems.add(interestSpread);

		} else {
			configItems.add(otherConfig);
		}

		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		footerRecordTypeConfig.setDocFieldName("recordId");
		footerRecordTypeConfig.setExpectedValue("TLR");
		footerRecordTypeConfig.setDisplayValue("Record Id");
		footerRecordTypeConfig.setStartIndex(1);
		footerRecordTypeConfig.setLength(3);
		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);
		configItems.add(footerRecordTypeConfig);

		DefaultFileLayoutConfigItem footerTotalDocConfig = new DefaultFileLayoutConfigItem();
		footerTotalDocConfig.setDocFieldName(null);
		footerTotalDocConfig.setDisplayValue("Total line");
		footerTotalDocConfig.setStartIndex(4);
		footerTotalDocConfig.setLength(7);
		footerTotalDocConfig.setRecordType(RecordType.FOOTER);
		footerTotalDocConfig.setPaddingCharacter("0");
		footerTotalDocConfig.setPaddingType(PaddingType.LEFT);
		footerTotalDocConfig.setValidationType(ValidationType.COUNT_OF_DOCUMENT_DETAIL);
		configItems.add(footerTotalDocConfig);

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

	private final class ExpectedCustomerCodeValidatorStub implements FieldValidator, FieldValueSetter {

		private static final String CASE_NOT_EXISTING = "{0} ({1}) is not exist {0}";

		private static final String CASE_INACTIVE = "{0} ({1}) is inactive {0}";

		@Override
		public void setValue(Object target, Object value) {
			try {
				PropertyUtils.setProperty(target, "supplierId", "002");
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void validate(Object dataValidate) throws WrongFormatDetailException {
			String customerCodeData = String.valueOf(dataValidate).trim();
			if ("MK004".equals(customerCodeData)) {
				throw new WrongFormatDetailException(MessageFormat.format(CASE_NOT_EXISTING, "Supplier code", "MK004"));
			} else if ("MK001".equals(customerCodeData)) {
				throw new WrongFormatDetailException(MessageFormat.format(CASE_INACTIVE, "Supplier code", "MK001"));
			}

		}
	}
}
