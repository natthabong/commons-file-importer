package gec.scf.file.converter;

import static org.junit.Assert.*;

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
import gec.scf.file.exception.WrongFormatDetailException;
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
		csvValidFileContent[1] = "1,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

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
		csvValidFileContent[1] = "1,5572692,,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

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
		csvValidFileContent[1] = "7,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,,KBANK,สามแยกอ่างศิลา,100093220,42635,42663,24/10/2014,560000,BCOB";

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
		csvValidFileContent[1] = "8,5572692,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,1234,KBANK,พระประแดง,100093221,42635,42663,24/10/2014,560000,BCOB";

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
		csvValidFileContent[1] = "9,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,,สามแยกอ่างศิลา,100093222,42635,42663,24/10/2014,560000,BCOB";

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
		csvValidFileContent[1] = "10,5572692,หาดใหญ่ใน,บจก ประจงกิจปาล์มออยล์,6,1234567890abcde123451,พระประแดง,100093223,42635,42663,24/10/2014,250000,BCOB";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		String payerErrMsg = actualResult.getErrorLineDetails().get(0).getErrorMessage();
		assertEquals("Bank length (22) is over max length (20)", payerErrMsg);
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
		supplierCode.setDisplayOfField("Payer Code");

		DefaultFileLayoutConfigItem depositBranch = new DefaultFileLayoutConfigItem();
		depositBranch.setStartIndex(3);
		depositBranch.setLength(20);
		depositBranch.setFieldName(null);
		depositBranch.setRequired(false);
		depositBranch.setDisplayOfField("Deposit branch");
		
		DefaultFileLayoutConfigItem payer = new DefaultFileLayoutConfigItem();
		payer.setStartIndex(4);
		payer.setLength(20);
		payer.setFieldName("optionVarcharField1");
		payer.setRequired(true);
		payer.setDisplayOfField("Payer");

		DefaultFileLayoutConfigItem bankCode = new DefaultFileLayoutConfigItem();
		bankCode.setStartIndex(5);
		bankCode.setLength(3);
		bankCode.setFieldName("optionVarcharField2");
		bankCode.setRequired(true);
		bankCode.setDisplayOfField("Bank Code");
		
		DefaultFileLayoutConfigItem bank = new DefaultFileLayoutConfigItem();
		bank.setStartIndex(6);
		bank.setLength(20);
		bank.setFieldName("optionVarcharField3");
		bank.setRequired(true);
		bank.setDisplayOfField("Bank");

		layoutItems.add(supplierCode);
		layoutItems.add(depositBranch);
		layoutItems.add(bankCode);
		layoutItems.add(payer);
		layoutItems.add(bank);

		fileLayout.setConfigItems(layoutItems);
		return fileLayout;
	}
}
