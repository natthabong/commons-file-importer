package gec.scf.file.converter;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfigItem;

import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;

public class CSVFileConverterCheckFileFormatTest {

	private static final boolean NOT_CHECK_BINARY = false;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private CSVFileConverter<SponsorDocument> csvFileConverter = new CSVFileConverter<SponsorDocument>(
			null, SponsorDocument.class);
	
	@Ignore
	@Test
	public void given_import_binary_file_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException, FileNotFoundException {
		// Arrange
		URL part = this.getClass().getResource("binaryFileConverter.txt");

		File csvFile = new File(part.getFile());
		InputStream csvFileContent = new FileInputStream(csvFile);
		
		//Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("is binary file");
		
		// Actual		
		csvFileConverter.checkFileFormat(csvFileContent);		
	}

	@Ignore
	@Test
	public void given_import_csv_file_when_check_file_format_should_not_WrongFormatFileException()
			throws FileNotFoundException, WrongFormatFileException {
		URL part = this.getClass().getResource("bigcsponsor.csv");

		File csvFile = new File(part.getFile());
		InputStream csvFileContent = new FileInputStream(csvFile);

		// Actual
		csvFileConverter.checkFileFormat(csvFileContent);
	}

	@Test(expected = WrongFormatFileException.class)
	public void given_import_csv_detail_length_less_than_config_should_throw_WrongFormatFileException()
			throws FileNotFoundException, WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554";
		csvValidFileContent[2] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent, NOT_CHECK_BINARY);
		InputStream csvFileContent = new ByteArrayInputStream(
				StringUtils.join(csvValidFileContent, System.lineSeparator()).getBytes());
		// Actual
		try {
			csvFileConverter.checkFileFormat(csvFileContent);
		}
		catch (WrongFormatFileException e) {
			assertEquals("data length (12) must have 13 field", e.getErrorMessage());
			assertEquals(2, e.getErrorLineNo().intValue());
			throw e;
		}
	}

	@Test(expected = WrongFormatFileException.class)
	public void given_import_csv_detail_length_over_config_should_throw_WrongFormatFileException()
			throws FileNotFoundException, WrongFormatFileException {
		// Arrage
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,,";
		csvValidFileContent[2] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent, NOT_CHECK_BINARY);
		InputStream csvFileContent = new ByteArrayInputStream(
				StringUtils.join(csvValidFileContent, System.lineSeparator()).getBytes());
		// Actual
		try {
			csvFileConverter.checkFileFormat(csvFileContent);
		}
		catch (WrongFormatFileException e) {
			assertEquals("data length (14) must have 13 field", e.getErrorMessage());
			assertEquals(2, e.getErrorLineNo().intValue());
			throw e;
		}
	}
	
	@Test
	public void given_config_not_check_binary_file_when_import_file_should_not_call_validate_binary_file() throws WrongFormatFileException, IOException{
		// Arrage
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";
		csvValidFileContent[2] = "22,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,6,KTB,พระประแดง,100093235,22/02/2015,22/02/2015,24/10/2014,1000554,";

		CSVFileConverter<SponsorDocument> csvFileConverter = mockSponsorFileLayout(csvValidFileContent, NOT_CHECK_BINARY);
		InputStream csvFileContent = new ByteArrayInputStream(
				StringUtils.join(csvValidFileContent, System.lineSeparator()).getBytes());
		
		// Actual
		csvFileConverter.checkFileFormat(csvFileContent);
		
		// Assert
		Mockito.verify(csvFileConverter, Mockito.never()).validateBinaryFile(csvFileContent);
		
	}

	private CSVFileConverter<SponsorDocument> mockSponsorFileLayout(String[] csvValidFileContent, boolean checkBinaryFile)
			throws WrongFormatFileException {

		DefaultFileLayoutConfig fileLayout = getLayoutConfig(checkBinaryFile);

		CSVFileConverter<SponsorDocument> csvFileConverter = Mockito.spy(new CSVFileConverter<SponsorDocument>(fileLayout, SponsorDocument.class));
		return csvFileConverter;
	}

	private DefaultFileLayoutConfig getLayoutConfig(boolean checkBinaryFile) {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setDelimeter(",");
		fileLayout.setCheckBinaryFile(checkBinaryFile);
		fileLayout.setCharsetName("UTF-8");

		List<FileLayoutConfigItem> layoutItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem no = new DefaultFileLayoutConfigItem();
		no.setStartIndex(1);
		no.setLength(10);
		no.setFieldName(null);
		no.setRequired(false);
		no.setDisplayValue("No");

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
		layoutItems.add(chequeAmount);
		layoutItems.add(clearingType);

		fileLayout.setConfigItems(layoutItems);
		return fileLayout;
	}

}
