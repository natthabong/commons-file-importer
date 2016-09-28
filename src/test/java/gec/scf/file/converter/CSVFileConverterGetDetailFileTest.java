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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class CSVFileConverterGetDetailFileTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void given_detail_valid_format_when_get_detail_should_success() throws WrongFormatFileException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// Arrange
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/2/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();
		SponsorDocument docResult = (SponsorDocument) actualResult.getObjectValue();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertEquals("5572692", docResult.getSupplierCode());
	}

	@Test
	public void given_payer_code_in_detail_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());

		String assertErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer Code" + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE, assertErrMsg);
	}

	@Test
	public void given_payer_code_and_bank_code_is_blank_when_get_detail_should_status_fail()
			throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());

		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		String bankCodeErrMsg = actualResult.getErrorLineDetails().get(1).getErrorMessage();
		assertEquals("Payer Code" + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE, payerErrMsg);
		assertEquals("Bank Code" + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE, bankCodeErrMsg);
	}

	@Test
	public void given_payer_code_length_over_20_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,1234567890abcde123451,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());

		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer Code length (21) is over max length (20)", payerErrMsg);
	}

	@Test
	public void given_deposit_branch_is_blank_when_get_detail_should_status_success() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/2/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertNull(actualResult.getErrorLineDetails());
	}

	@Test
	public void given_payer_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,,,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer" + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE, payerErrMsg);
	}
	
	@Test
	public void given_payer_over_limit_20_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,,1234567890abcde123451,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Payer length (21) is over max length (20)", payerErrMsg);
	}
	
	@Test
	public void given_bank_code_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "7,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,,KBANK,สามแยกอ่างศิลา,100093220,24/10/2014,42663,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank Code" + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE, payerErrMsg);
	}
	
	@Test
	public void given_bank_code_length_over_limit_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "8,5572692,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,1234,KBANK,พระประแดง,100093221,24/10/2014,42663,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank Code length (4) is over max length (3)", payerErrMsg);
	}
	
	@Test
	public void given_bank_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "9,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,,สามแยกอ่างศิลา,100093222,24/10/2014,42663,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank" + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE, payerErrMsg);
	}
	
	@Test
	public void given_bank_length_over_limit_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "10,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,1234567890abcde123451,พระประแดง,100093223,24/10/2014,42663,24/10/2014,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank length (21) is over max length (20)", payerErrMsg);
	}
	
	@Test
	public void given_cheque_branch_is_blank_when_get_detail_should_status_success() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "12,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,,100093223,24/10/2014,24/10/2014,24/10/2014,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertNull(actualResult.getErrorLineDetails());
	}
	
	@Test
	public void given_cheque_No_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "12,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,,24/10/2014,42663,24/10/2014,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque No is required", payerErrMsg);
	}
	
	@Test
	public void given_cheque_No_over_limit_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "13,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,a12345678901234567890123456789012345678901234567890,24/10/2014,42663,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque No length (51) is over max length (50)", payerErrMsg);
	}
	
	@Test
	public void given_cheque_due_date_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "14,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,พระประแดง,100093227,,42663,24/10/2014,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Due Date is required", payerErrMsg);
	}
	
	@Test
	public void given_cheque_due_date_invalid_format_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "15,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,สามแยกอ่างศิลา,100093228,29/02/2015,42663,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Due Date (29/02/2015) invalid format", payerErrMsg);
	}
	
	@Test
	public void given_good_fund_date_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "16,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093229,28/02/2015,,24/10/2014,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Good Fund Date is required", payerErrMsg);
	}
	
	@Test
	public void given_good_fund_date_invalid_format_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "15,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,สามแยกอ่างศิลา,100093228,22/02/2015,29/02/2015,24/10/2014,560000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Good Fund Date (29/02/2015) invalid format", payerErrMsg);
	}
	
	@Test
	public void given_deposit_date_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Deposit Date is required", payerErrMsg);
	}
	
	@Test
	public void given_deposit_date_invalid_format_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/13/2015,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Deposit Date (29/13/2015) invalid format", payerErrMsg);
	}
	
	@Test
	public void given_cheque_amount_is_blank_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/10/2015,,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount is required", payerErrMsg);
	}
	
	@Test
	public void given_cheque_amount_have_plus_symbol_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "18,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,7,SCB,พระประแดง,100093231,22/02/2015,22/02/2015,29/10/2015,+100,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount (+100) invalid format", payerErrMsg);
	}
	
	@Test
	public void given_cheque_amount_have_comma_invalid_format_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,\"1,00\",BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount (1,00) invalid format", payerErrMsg);
	}
	
	@Test
	public void given_cheque_amount_have_dot_invalid_format_when_get_detail_should_status_fail() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000.554,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Cheque Amount digit is over max digit (2)", payerErrMsg);
	}
	
	@Test
	public void given_clearing_type_is_blank_when_get_detail_should_status_success() throws WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[2];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument sponsorDoc = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("5572692", sponsorDoc.getSupplierCode());
		
	}

	private CSVFileConverter<SponsorDocument> mockSponsorFileLayout(String[] csvValidFileContent)
			throws WrongFormatFileException {
		InputStream csvFileContent = new ByteArrayInputStream(
				StringUtils.join(csvValidFileContent, System.lineSeparator()).getBytes());

		DefaultFileLayoutConfig fileLayout = getLayoutConfig();

		CSVFileConverter<SponsorDocument> csvFileConverter = new CSVFileConverter<SponsorDocument>(
				SponsorDocument.class);
		csvFileConverter.setFileLayoutConfig(fileLayout);
		csvFileConverter.checkFileFormat(csvFileContent);
		return csvFileConverter;
	}

	private DefaultFileLayoutConfig getLayoutConfig() {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setDelimeter(",");

		List<FileLayoutConfigItem> layoutItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem supplierCode = new DefaultFileLayoutConfigItem();
		supplierCode.setStartIndex(2);
		supplierCode.setLength(20);
		supplierCode.setFieldName("supplierCode");
		supplierCode.setRequired(true);
		supplierCode.setDisplayValue("Payer Code");

		DefaultFileLayoutConfigItem depositBranch = new DefaultFileLayoutConfigItem();
		depositBranch.setStartIndex(3);
		depositBranch.setLength(20);
		depositBranch.setFieldName(null);
		depositBranch.setRequired(false);
		depositBranch.setDisplayValue("Deposit branch");
		
		DefaultFileLayoutConfigItem payer = new DefaultFileLayoutConfigItem();
		payer.setStartIndex(4);
		payer.setLength(20);
		payer.setFieldName("optionVarcharField1");
		payer.setRequired(true);
		payer.setDisplayValue("Payer");

		DefaultFileLayoutConfigItem bankCode = new DefaultFileLayoutConfigItem();
		bankCode.setStartIndex(5);
		bankCode.setLength(3);
		bankCode.setFieldName("optionVarcharField2");
		bankCode.setRequired(true);
		bankCode.setDisplayValue("Bank Code");
		
		DefaultFileLayoutConfigItem bank = new DefaultFileLayoutConfigItem();
		bank.setStartIndex(6);
		bank.setLength(20);
		bank.setFieldName("optionVarcharField3");
		bank.setRequired(true);
		bank.setDisplayValue("Bank");
		
		DefaultFileLayoutConfigItem chequeBranch = new DefaultFileLayoutConfigItem();
		chequeBranch.setStartIndex(7);
		chequeBranch.setLength(50);
		chequeBranch.setFieldName(null);
		chequeBranch.setRequired(false);
		chequeBranch.setDisplayValue("Cheque Branch");
		
		DefaultFileLayoutConfigItem chequeNo = new DefaultFileLayoutConfigItem();
		chequeNo.setStartIndex(8);
		chequeNo.setLength(50);
		chequeNo.setFieldName("documentNo");
		chequeNo.setRequired(true);
		chequeNo.setDisplayValue("Cheque No");
		
		DefaultFileLayoutConfigItem chequeDueDate = new DefaultFileLayoutConfigItem();
		chequeDueDate.setStartIndex(9);
		chequeDueDate.setLength(10);
		chequeDueDate.setFieldName("documentDate");
		chequeDueDate.setRequired(true);
		chequeDueDate.setDisplayValue("Cheque Due Date");
		chequeDueDate.setDatetimeFormat("dd/MM/yyyy");
		
		DefaultFileLayoutConfigItem goodFundDate = new DefaultFileLayoutConfigItem();
		goodFundDate.setStartIndex(10);
		goodFundDate.setLength(10);
		goodFundDate.setFieldName("sponsorPaymentDate");
		goodFundDate.setRequired(true);
		goodFundDate.setDisplayValue("Good Fund Date");
		goodFundDate.setDatetimeFormat("dd/MM/yyyy");
		
		DefaultFileLayoutConfigItem depositDate = new DefaultFileLayoutConfigItem();
		depositDate.setStartIndex(11);
		depositDate.setLength(10);
		depositDate.setFieldName("optionDateField1");
		depositDate.setRequired(true);
		depositDate.setDisplayValue("Deposit Date");
		depositDate.setDatetimeFormat("dd/MM/yyyy");
		
		DefaultFileLayoutConfigItem chequeAmount = new DefaultFileLayoutConfigItem();
		chequeAmount.setStartIndex(12);
		chequeAmount.setLength(10);
		chequeAmount.setFieldName("documentAmount");
		chequeAmount.setRequired(true);
		chequeAmount.setDisplayValue("Cheque Amount");
		chequeAmount.setDecimalPlace(2);
		
		DefaultFileLayoutConfigItem clearingType = new DefaultFileLayoutConfigItem();
		clearingType.setStartIndex(13);
		clearingType.setLength(10);
		clearingType.setFieldName(null);
		clearingType.setRequired(false);
		clearingType.setDisplayValue("Clearing Type");

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
		layoutItems.add(chequeAmount);
		
		fileLayout.setConfigItems(layoutItems);
		return fileLayout;
	}
}
