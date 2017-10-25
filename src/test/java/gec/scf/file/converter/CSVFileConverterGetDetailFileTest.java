package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class CSVFileConverterGetDetailFileTest {

	private CSVFileConverter<SponsorDocument> csvFileConverter;

	@Test
	public void given_detail_valid_format_when_get_detail_should_success()
	        throws WrongFormatFileException, NoSuchFieldException, SecurityException,
	        IllegalArgumentException, IllegalAccessException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/2/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();
		SponsorDocument docResult = (SponsorDocument) actualResult.getObjectValue();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertEquals("5572692", docResult.getSupplierCode());
		assertEquals("KBANK", docResult.getOptionVarcharField3());
	}

	@Test
	public void given_payer_code_in_detail_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());

		String assertErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer Code is required", assertErrMsg);
	}

	@Test
	public void given_payer_code_and_bank_code_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());

		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		String bankCodeErrMsg = actualResult.getErrorLineDetails().get(1)
		        .getErrorMessage();
		assertEquals("Payer Code is required", payerErrMsg);
		assertEquals("Bank Code is required", bankCodeErrMsg);
	}

	@Test
	public void given_payer_code_length_over_20_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,1234567890abcde123451,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());

		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer Code length (21) is over max length (20)", payerErrMsg);
	}

	@Test
	public void given_deposit_branch_is_blank_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/2/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertNull(actualResult.getErrorLineDetails());
	}

	@Test
	public void given_payer_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,,,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer is required", payerErrMsg);
	}

	@Test
	public void given_payer_over_limit_20_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,,1234567890abcde123451,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer length (21) is over max length (20)", payerErrMsg);
	}

	@Test
	public void given_bank_code_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "7,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,,KBANK,สามแยกอ่างศิลา,100093220,24/10/2014,42663,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank Code is required", payerErrMsg);
	}

	@Test
	public void given_bank_code_length_over_limit_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "8,5572692,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,1234,KBANK,พระประแดง,100093221,24/10/2014,42663,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank Code length (4) is over max length (3)", payerErrMsg);
	}

	@Test
	public void given_bank_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "9,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,,สามแยกอ่างศิลา,100093222,24/10/2014,42663,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank is required", payerErrMsg);
	}

	@Test
	public void given_bank_length_over_limit_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "10,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,1234567890abcde123451,พระประแดง,100093223,24/10/2014,42663,24/10/2014,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank length (21) is over max length (20)", payerErrMsg);
	}

	@Test
	public void given_cheque_branch_is_blank_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "12,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,,100093223,24/10/2014,24/10/2014,24/10/2014,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertNull(actualResult.getErrorLineDetails());
	}

	@Test
	public void given_cheque_No_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "12,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,,24/10/2014,42663,24/10/2014,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque No is required", payerErrMsg);
	}

	@Test
	public void given_cheque_No_over_limit_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "13,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,a12345678901234567890123456789012345678901234567890,24/10/2014,42663,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque No length (51) is over max length (50)", payerErrMsg);
	}

	@Test
	public void given_cheque_due_date_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "14,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,พระประแดง,100093227,,42663,24/10/2014,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Due Date is required", payerErrMsg);
	}

	@Test
	public void given_cheque_due_date_invalid_format_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "15,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,สามแยกอ่างศิลา,100093228,29/02/2015,42663,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Due Date (29/02/2015) invalid format", payerErrMsg);
	}

	@Test
	public void given_good_fund_date_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "16,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093229,28/02/2015,,24/10/2014,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Good Fund Date is required", payerErrMsg);
	}

	@Test
	public void given_good_fund_date_invalid_format_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "15,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,สามแยกอ่างศิลา,100093228,22/02/2015,29/02/2015,24/10/2014,560000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Good Fund Date (29/02/2015) invalid format", payerErrMsg);
	}

	@Test
	public void given_deposit_date_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Deposit Date is required", payerErrMsg);
	}

	@Test
	public void given_deposit_date_invalid_format_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/13/2015,250000,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Deposit Date (29/13/2015) invalid format", payerErrMsg);
	}

	@Test
	public void given_cheque_amount_is_blank_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/10/2015,,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount is required", payerErrMsg);
	}

	@Test
	public void given_cheque_amount_have_plus_symbol_but_has_no_config_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/10/2015,+100,BCOB";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(2);
		chequeAmount.setHas1000Separator(null);
		chequeAmount.setHasDecimalPlace(null);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol(null);

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
	}

	@Test
	public void given_cheque_amount_have_plus_symbol_but_in_config_is_0_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/10/2015,+100,BCOB";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(2);
		chequeAmount.setHas1000Separator(true);
		chequeAmount.setHasDecimalPlace(true);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol("0");

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount (+100) invalid format", payerErrMsg);
	}

	@Test
	public void given_cheque_amount_have_plus_symbol_and_has_config_plus_symbol_when_get_detail_should_success()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/10/2015,+100,BCOB";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(2);
		chequeAmount.setHas1000Separator(true);
		chequeAmount.setHasDecimalPlace(true);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol("+");

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
	}

	@Test
	public void given_cheque_amount_have_invalid_comma_format_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"1,00\",BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount (1,00) invalid format", payerErrMsg);
	}

	@Test
	public void given_cheque_amount_have_dot_invalid_format_when_get_detail_should_status_fail()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000.554,BCOB";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount digit is over max digit (2)", payerErrMsg);
	}

	@Test
	public void given_clearing_type_is_blank_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("5572692", sponsorDoc.getSupplierCode());

	}

	@Test
	public void given_amount_have_commar_and_set_use_1000_separator_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"100,554.00\",";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("5572692", sponsorDoc.getSupplierCode());
	}

	@Test
	public void given_set_config_use_decimal_place_is_true_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"100,554.00\",";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("100554.00", sponsorDoc.getDocumentAmount().toString());
	}

	@Test
	public void given_No_is_blank_when_get_detail_should_status_success()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = ",5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("5572692", sponsorDoc.getSupplierCode());

	}

	@Test
	public void given_config_default_value_documentType_CHQ_when_get_detail_should_set_value_to_documentType()
	        throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = ",5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("CHQ", sponsorDoc.getDocumentType());

	}

	@Test
	public void given_config_has_decimal_is_false_place_and_cheque_amount_is_100554_when_get_detail_cheque_amount_should_is_100554_00()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"100554\",";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(0);
		chequeAmount.setHas1000Separator(true);
		chequeAmount.setHasDecimalPlace(false);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol("+");

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();

		BigDecimal expected = new BigDecimal("100554.00");
		assertTrue("Sponsor document amount should be 100554.00",
		        sponsorDoc.getDocumentAmount().compareTo(expected) == 0);
	}

	@Test
	public void given_config_has_decimal_place_is_true_and_cheque_amount_is_100554_when_get_detail_cheque_amount_should_is_100554()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"100554.00\",";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();

		BigDecimal expected = new BigDecimal("100554.00");
		assertTrue("Sponsor document amount should be 100554.00",
		        sponsorDoc.getDocumentAmount().compareTo(expected) == 0);
	}

	@Test
	public void given_configs_that_has_decimal_place_is_true_and_cheque_amount_is_1005540_no_scale_when_get_detail_cheque_amount_should_is_1005540()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"1005540\",";

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();

		BigDecimal expected = new BigDecimal("1005540.00");
		assertTrue(
		        MessageFormat.format("Sponsor document amount should be {0} but {1}",
		                expected, sponsorDoc.getDocumentAmount()),
		        sponsorDoc.getDocumentAmount().compareTo(expected) == 0);
	}

	@Test
	public void given_configs_that_has_decimal_place_is_true_and_decimal_place_is_3_and_cheque_amount_is_100554_003_when_get_detail_cheque_amount_should_is_100554_003()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"100554.003\",";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(3);
		chequeAmount.setHas1000Separator(null);
		chequeAmount.setHasDecimalPlace(true);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol(null);

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();

		BigDecimal expected = new BigDecimal("100554.003");
		assertTrue(
		        MessageFormat.format("Sponsor document amount should be {0} but {1}",
		                expected, sponsorDoc.getDocumentAmount()),
		        sponsorDoc.getDocumentAmount().compareTo(expected) == 0);
	}

	@Test
	public void given_configs_that_has_decimal_place_is_true_and_decimal_place_is_2_and_cheque_amount_is_100554_2_when_get_detail_cheque_amount_should_is_100554_2()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"100554.2\",";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(2);
		chequeAmount.setHas1000Separator(null);
		chequeAmount.setHasDecimalPlace(true);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol(null);

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();

		BigDecimal expected = new BigDecimal("100554.2");
		assertTrue(
		        MessageFormat.format("Sponsor document amount should be {0} but {1}",
		                expected, sponsorDoc.getDocumentAmount()),
		        sponsorDoc.getDocumentAmount().compareTo(expected) == 0);
	}

	@Test
	public void given_config_is_no_required_and_cheque_amount_is_blank_when_get_detail_cheque_amount_should_is_null()
	        throws WrongFormatFileException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"\",";

		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setRecordType(RecordType.DETAIL);
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setDocFieldName("documentAmount");
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(2);
		chequeAmount.setHas1000Separator(null);
		chequeAmount.setHasDecimalPlace(null);
		chequeAmount.setTransient(false);
		chequeAmount.setPlusSymbol(null);

		// No required
		chequeAmount.setRequired(false);

		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, chequeAmount);

		// Actual
		DetailResult<SponsorDocument> actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();

		assertNull(MessageFormat.format("Sponsor document amount should be null but {0}",
		        sponsorDoc.getDocumentAmount()), sponsorDoc.getDocumentAmount());
	}

	@Test
	public void given_set_offset_row_no_2_and_set_header_has_1_record_and_detail_has_2_record_when_get_detail_should_recieve_detail_1_record()
	        throws WrongFormatFileException {
		// Arrange
		long OFFSET_ROWNO = 2;
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,560000,";
		csvValidFileContent[2] = "23,10278973,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,7,SCB,พระประแดง,480611,15/02/2015,16/03/2015,24/10/2014,250000,";

		DefaultFileLayoutConfig fileLayout = getLayoutConfig();
		fileLayout.setOffsetRowNo(OFFSET_ROWNO);
		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, fileLayout);

		DetailResult<SponsorDocument> actualResult = null;
		int countDetail = 0;
		// Actual
		while ((actualResult = csvFileConverter.getDetail()) != null) {
			countDetail++;
		}

		// Assert
		assertEquals(1, countDetail);
	}

	@Test
	public void given_set_offset_row_no_2_and_set_header_has_1_record_and_detail_has_3_record_when_get_detail_should_recieve_detail_2_record()
	        throws WrongFormatFileException {
		// Arrange
		long OFFSET_ROWNO = 2;
		String[] csvValidFileContent = new String[4];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,560000,";
		csvValidFileContent[2] = "23,10278973,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,7,SCB,พระประแดง,480611,15/02/2015,16/03/2015,24/10/2014,250000,";
		csvValidFileContent[3] = "24,10278974,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,7,SCB,พระประแดง,480611,15/02/2015,16/03/2015,24/10/2014,750000,";

		DefaultFileLayoutConfig fileLayout = getLayoutConfig();
		fileLayout.setOffsetRowNo(OFFSET_ROWNO);
		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, fileLayout);

		DetailResult<SponsorDocument> actualResult = null;
		int countDetail = 0;
		// Actual
		while ((actualResult = csvFileConverter.getDetail()) != null) {
			countDetail++;
		}

		// Assert
		assertEquals(2, countDetail);
	}

	
	@Test
	public void given_set_offset_row_no_1_and_set_header_has_1_record_and_detail_has_2_record_when_get_detail_should_recieve_detail_2_record()
	        throws WrongFormatFileException {
		// Arrange
		long OFFSET_ROWNO = 1;
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,560000,";
		csvValidFileContent[2] = "23,10278973,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,7,SCB,พระประแดง,480611,15/02/2015,16/03/2015,24/10/2014,250000,";

		DefaultFileLayoutConfig fileLayout = getLayoutConfig();
		fileLayout.setOffsetRowNo(OFFSET_ROWNO);
		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, fileLayout);

		DetailResult<SponsorDocument> actualResult = null;
		int countDetail = 0;
		// Actual
		while ((actualResult = csvFileConverter.getDetail()) != null) {
			countDetail++;
		}

		// Assert
		assertEquals(2, countDetail);
	}
	
	@Test
	public void given_set_offset_row_no_0_and_set_header_has_0_record_and_detail_has_2_record_when_get_detail_should_recieve_detail_2_record()
	        throws WrongFormatFileException {
		// Arrange
		long OFFSET_ROWNO = 0;
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,560000,";
		csvValidFileContent[1] = "23,10278973,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,7,SCB,พระประแดง,480611,15/02/2015,16/03/2015,24/10/2014,250000,";

		DefaultFileLayoutConfig fileLayout = getLayoutConfig();
		fileLayout.setOffsetRowNo(OFFSET_ROWNO);
		csvFileConverter = mockSponsorFileLayout(csvValidFileContent, fileLayout);

		DetailResult<SponsorDocument> actualResult = null;
		int countDetail = 0;
		// Actual
		while ((actualResult = csvFileConverter.getDetail()) != null) {
			countDetail++;
		}

		// Assert
		assertEquals(2, countDetail);
	}
	private CSVFileConverter<SponsorDocument> mockSponsorFileLayout(
	        String[] csvValidFileContent, DefaultFileLayoutConfigItem chequeAmount)
	        throws WrongFormatFileException {

		DefaultFileLayoutConfig fileLayout = getLayoutConfig(chequeAmount);
		fileLayout.setOffsetRowNo(1);

		return mockSponsorFileLayout(csvValidFileContent, fileLayout);
	}

	private CSVFileConverter<SponsorDocument> mockSponsorFileLayout(
	        String[] csvValidFileContent) throws WrongFormatFileException {

		DefaultFileLayoutConfig fileLayout = getLayoutConfig();
		fileLayout.setOffsetRowNo(1);

		return mockSponsorFileLayout(csvValidFileContent, fileLayout);
	}

	private CSVFileConverter<SponsorDocument> mockSponsorFileLayout(
	        String[] csvValidFileContent, FileLayoutConfig fileLayout)

	        throws WrongFormatFileException {

		InputStream csvFileContent = new ByteArrayInputStream(
		        StringUtils.join(csvValidFileContent, System.lineSeparator()).getBytes());

		CSVFileConverter<SponsorDocument> csvFileConverter = new CSVFileConverter<SponsorDocument>(
		        fileLayout, SponsorDocument.class);

		csvFileConverter.checkFileFormat(csvFileContent);

		return csvFileConverter;
	}

	private DefaultFileLayoutConfig getLayoutConfig() {

		return getLayoutConfig(null);
	}

	private DefaultFileLayoutConfig getLayoutConfig(
	        DefaultFileLayoutConfigItem otheItem) {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setDelimeter(",");
		fileLayout.setCharsetName("UTF-8");

		List<FileLayoutConfigItem> layoutItems = new ArrayList<FileLayoutConfigItem>();

		if (otheItem != null) {
			layoutItems.add(otheItem);
		}
		else {
			DefaultFileLayoutConfigItem no = new DefaultFileLayoutConfigItem();
			no.setStartIndex(1);
			no.setLength(10);
			no.setDocFieldName(null);
			no.setRequired(false);
			no.setDisplayValue("No");
			no.setRecordType(RecordType.DETAIL);

			DefaultFileLayoutConfigItem supplierCode = new DefaultFileLayoutConfigItem();
			supplierCode.setRecordType(RecordType.DETAIL);
			supplierCode.setStartIndex(2);
			supplierCode.setLength(20);
			supplierCode.setDocFieldName("supplierCode");
			supplierCode.setRequired(true);
			supplierCode.setDisplayValue("Payer Code");
			supplierCode.setTransient(false);

			DefaultFileLayoutConfigItem depositBranch = new DefaultFileLayoutConfigItem();
			depositBranch.setRecordType(RecordType.DETAIL);
			depositBranch.setStartIndex(3);
			depositBranch.setLength(20);
			depositBranch.setDocFieldName(null);
			depositBranch.setRequired(false);
			depositBranch.setDisplayValue("Deposit branch");

			DefaultFileLayoutConfigItem payer = new DefaultFileLayoutConfigItem();
			payer.setRecordType(RecordType.DETAIL);
			payer.setStartIndex(4);
			payer.setLength(20);
			payer.setDocFieldName("optionVarcharField1");
			payer.setRequired(true);
			payer.setDisplayValue("Payer");
			payer.setTransient(false);

			DefaultFileLayoutConfigItem bankCode = new DefaultFileLayoutConfigItem();
			bankCode.setRecordType(RecordType.DETAIL);
			bankCode.setStartIndex(5);
			bankCode.setLength(3);
			bankCode.setDocFieldName("optionVarcharField2");
			bankCode.setRequired(true);
			bankCode.setDisplayValue("Bank Code");
			bankCode.setTransient(false);

			DefaultFileLayoutConfigItem bank = new DefaultFileLayoutConfigItem();
			bank.setRecordType(RecordType.DETAIL);
			bank.setStartIndex(6);
			bank.setLength(20);
			bank.setDocFieldName("optionVarcharField3");
			bank.setRequired(true);
			bank.setDisplayValue("Bank");
			bank.setTransient(false);

			DefaultFileLayoutConfigItem chequeBranch = new DefaultFileLayoutConfigItem();
			chequeBranch.setRecordType(RecordType.DETAIL);
			chequeBranch.setStartIndex(7);
			chequeBranch.setLength(50);
			chequeBranch.setDocFieldName(null);
			chequeBranch.setRequired(false);
			chequeBranch.setDisplayValue("Cheque Branch");

			DefaultFileLayoutConfigItem chequeNo = new DefaultFileLayoutConfigItem();
			chequeNo.setRecordType(RecordType.DETAIL);
			chequeNo.setStartIndex(8);
			chequeNo.setLength(50);
			chequeNo.setDocFieldName("documentNo");
			chequeNo.setRequired(true);
			chequeNo.setDisplayValue("Cheque No");
			chequeNo.setTransient(false);

			DefaultFileLayoutConfigItem chequeDueDate = new DefaultFileLayoutConfigItem();
			chequeDueDate.setRecordType(RecordType.DETAIL);
			chequeDueDate.setStartIndex(9);
			chequeDueDate.setLength(10);
			chequeDueDate.setDocFieldName("documentDate");
			chequeDueDate.setRequired(true);
			chequeDueDate.setDisplayValue("Cheque Due Date");
			chequeDueDate.setDatetimeFormat("dd/MM/yyyy");
			chequeDueDate.setTransient(false);

			DefaultFileLayoutConfigItem goodFundDate = new DefaultFileLayoutConfigItem();
			goodFundDate.setRecordType(RecordType.DETAIL);
			goodFundDate.setStartIndex(10);
			goodFundDate.setLength(10);
			goodFundDate.setDocFieldName("sponsorPaymentDate");
			goodFundDate.setRequired(true);
			goodFundDate.setDisplayValue("Good Fund Date");
			goodFundDate.setDatetimeFormat("dd/MM/yyyy");
			goodFundDate.setTransient(false);

			DefaultFileLayoutConfigItem depositDate = new DefaultFileLayoutConfigItem();
			depositDate.setRecordType(RecordType.DETAIL);
			depositDate.setStartIndex(11);
			depositDate.setLength(10);
			depositDate.setDocFieldName("optionDateField1");
			depositDate.setRequired(true);
			depositDate.setDisplayValue("Deposit Date");
			depositDate.setDatetimeFormat("dd/MM/yyyy");
			depositDate.setTransient(false);

			DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
			chequeAmount.setRecordType(RecordType.DETAIL);
			chequeAmount.setStartIndex(12);
			chequeAmount.setLength(10);
			chequeAmount.setDocFieldName("documentAmount");
			chequeAmount.setRequired(true);
			chequeAmount.setDisplayValue("Cheque Amount");
			chequeAmount.setDecimalPlace(2);
			chequeAmount.setHas1000Separator(null);
			chequeAmount.setHasDecimalPlace(true);
			chequeAmount.setTransient(false);
			chequeAmount.setPlusSymbol(null);
			layoutItems.add(chequeAmount);

			DefaultFileLayoutConfigItem documentType = new DefaultFileLayoutConfigItem();
			documentType.setRecordType(RecordType.DETAIL);
			documentType.setLength(3);
			documentType.setDocFieldName("documentType");
			documentType.setRequired(false);
			documentType.setTransient(false);
			documentType.setDefaultValue("CHQ");
			documentType.setDisplayValue("Document Type");
			layoutItems.add(documentType);

			layoutItems.add(no);
			layoutItems.add(supplierCode);
			layoutItems.add(depositBranch);
			layoutItems.add(bankCode);
			layoutItems.add(payer);
			layoutItems.add(bank);
			layoutItems.add(chequeBranch);
			layoutItems.add(chequeNo);
			layoutItems.add(chequeDueDate);
			layoutItems.add(goodFundDate);
			layoutItems.add(depositDate);

		}

		DefaultFileLayoutConfigItem clearingType = new DefaultFileLayoutConfigItem();
		clearingType.setRecordType(RecordType.DETAIL);
		clearingType.setStartIndex(13);
		clearingType.setLength(10);
		clearingType.setDocFieldName(null);
		clearingType.setRequired(false);
		clearingType.setDisplayValue("Clearing Type");

		layoutItems.add(clearingType);

		fileLayout.setConfigItems(layoutItems);
		return fileLayout;
	}
}
