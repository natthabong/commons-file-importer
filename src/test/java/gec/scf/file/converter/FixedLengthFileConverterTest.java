package gec.scf.file.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;

public class FixedLengthFileConverterTest {

	private FileConverter<SponsorDocument> fixLengthFileConverter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout();

		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void given_a_valid_format_file_when_check_file_format_should_valid()
			throws WrongFormatFileException {

		// Arrange
		InputStream documentFile = getFixedLengthFileContent();

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_a_file_which_has_no_hader_flag_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                              ";
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (H) not found on first row");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_a_file_which_has_a_small_case_header_flag_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "h20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                              ";
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (h) mismatch");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_a_file_which_has_a_space_case_header_flag_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = " 20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000010000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (H) not found on first row");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_a_file_which_has_a_document_date_wrong_format_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20161311120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Send Date (20161311) invalid format");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_a_header_line_which_coporate_code_mismatched_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20161211120000Siam Makro P c.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Corporate Name (Siam Makro P c.) mismatch");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_header_total_length_300_when_check_file_format_found_301_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[1];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       X";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Data length (301) must have 300 characters");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_detail_length_config_is_300_when_check_file_format_found_length_301_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fixedLengthContent[2] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Data length (300) must have 301 characters");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_a_file_which_found_line_space_after_footer_when_check_file_format_should_be_valid()
			throws WrongFormatFileException {
		// Arrang
		String[] fileContent = new String[5];
		fileContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fileContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fileContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fileContent[3] = "T0000020000000101000000                                                                                                                                                                                                                                                                                     ";
		fileContent[4] = "                  ";

		InputStream drawdownAdviceFile = new ByteArrayInputStream(
				StringUtils.join(fileContent, System.lineSeparator()).getBytes());

		// Actual
		fixLengthFileConverter.checkFileFormat(drawdownAdviceFile);

	}

	@Test
	public void given_a_file_which_found_three_blank_line_after_footer_when_check_file_format_should_validn()
			throws WrongFormatFileException {
		// Arrang
		String[] fileContent = new String[7];
		fileContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fileContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fileContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fileContent[3] = "T0000020000000101000000                                                                                                                                                                                                                                                                                     ";
		fileContent[4] = "";
		fileContent[5] = "";
		fileContent[6] = "";

		InputStream drawdownAdviceFile = new ByteArrayInputStream(
				StringUtils.join(fileContent, System.lineSeparator()).getBytes());
		// Actual
		fixLengthFileConverter.checkFileFormat(drawdownAdviceFile);

	}

	@Test
	public void given_a_file_which_found_detail_line_after_footer_line_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {
		// Arrang
		String[] fileContent = new String[4];
		fileContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fileContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fileContent[2] = "T0000010000000100000000                                                                                                                                                                                                                                                                                     ";
		fileContent[3] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		InputStream drawdownAdviceFile = new ByteArrayInputStream(
				StringUtils.join(fileContent, System.lineSeparator()).getBytes());

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (T) not found on last row");

		// Actual
		fixLengthFileConverter.checkFileFormat(drawdownAdviceFile);
	}

	@Test
	public void given_a_file_which_has_a_small_case_record_type_in_a_detail_when_check_file_format_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {

		// Arrange
		String[] fileContent = new String[3];
		fileContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fileContent[1] = "dMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fileContent[2] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream drawdownAdviceFile = new ByteArrayInputStream(
				StringUtils.join(fileContent, System.lineSeparator()).getBytes());

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (d) mismatch");

		// Actual
		fixLengthFileConverter.checkFileFormat(drawdownAdviceFile);
	}

	@Test
	public void given_a_file_which_has_a_blank_line_detail_when_check_file_format_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {
		// Arrange
		String[] fileContent = new String[4];
		fileContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fileContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                              ";
		fileContent[1] = "";
		fileContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream drawdownAdviceFile = new ByteArrayInputStream(
				StringUtils.join(fileContent, System.lineSeparator()).getBytes());

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("File invalid format");

		// Actual
		fixLengthFileConverter.checkFileFormat(drawdownAdviceFile);
	}

	@Test
	public void given_a_file_which_has_no_footer_line_when_check_format_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {

		// Arrange
		String[] fileContent = new String[2];
		fileContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fileContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";

		InputStream drawdownAdviceFile = new ByteArrayInputStream(
				StringUtils.join(fileContent, System.lineSeparator()).getBytes());

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (T) not found on last row");

		// Actual
		fixLengthFileConverter.checkFileFormat(drawdownAdviceFile);
	}

	@Test
	public void given_a_file_which_has_a_space_case_footer_flag_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = " 0000010000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Record Type (T) not found on last row");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_total_document_amount_in_the_footer_is_not_equal_to_sum_of_document_amount_in_detail_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
				"Total Document Amount (99,000.00) is invalid. Total detail line is 101,000.00");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_total_document_in_the_footer_which_has_wrong_format_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "Ttwelve0000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document No (twelve) invalid format");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_total_document_in_the_footer_which_has_wrong_number_format_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T001,000000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document No (001,00) invalid format");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_total_document_amount_in_the_footer_which_has_wrong_number_format_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T00000200000100,0000001                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document Amount (00000100,000000) invalid format");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_total_document_in_the_footer_is_not_equal_to_total_of_document_in_detail_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000010000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document No (1) is invalid. Total detail line is 2");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	private FileLayoutConfig createFixedLengthFileLayout() {
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

		DefaultFileLayoutConfigItem documentDateConfig = new DefaultFileLayoutConfigItem();
		documentDateConfig.setDocFieldName("documentDate");
		documentDateConfig.setStartIndex(2);
		documentDateConfig.setLength(8);
		documentDateConfig.setDisplayValue("Send Date");
		documentDateConfig.setDatetimeFormat("yyyyMMdd");
		documentDateConfig.setTransient(true);
		documentDateConfig.setRecordType(RecordType.HEADER);

		configItems.add(documentDateConfig);

		DefaultFileLayoutConfigItem corporateNameConfig = new DefaultFileLayoutConfigItem();
		corporateNameConfig.setDocFieldName("corporateName");
		corporateNameConfig.setStartIndex(16);
		corporateNameConfig.setLength(30);
		corporateNameConfig.setExpectedValue("Siam Makro Plc.");
		corporateNameConfig.setDisplayValue("Corporate Name");
		corporateNameConfig.setRecordType(RecordType.HEADER);

		configItems.add(corporateNameConfig);

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

		configItems.add(detailRecordTypeConfig);

		DefaultFileLayoutConfigItem docAmountConfig = new DefaultFileLayoutConfigItem();
		docAmountConfig.setDocFieldName("documentAmount");
		docAmountConfig.setStartIndex(63);
		docAmountConfig.setLength(15);
		docAmountConfig.setPaddingCharacter("0");
		docAmountConfig.setPaddingType(PaddingType.LEFT);
		docAmountConfig.setDecimalPlace(2);
		docAmountConfig.setHasDecimalPlace(true);
		docAmountConfig.setRecordType(RecordType.DETAIL);

		configItems.add(docAmountConfig);

		DefaultFileLayoutConfigItem detailFilterConfig = new DefaultFileLayoutConfigItem();
		detailFilterConfig.setDocFieldName("filter");
		detailFilterConfig.setStartIndex(54);
		detailFilterConfig.setLength(248);
		detailFilterConfig.setRecordType(RecordType.DETAIL);

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
		footerTotalDocConfig.setDisplayValue("Total Document No");
		footerTotalDocConfig.setStartIndex(2);
		footerTotalDocConfig.setLength(6);
		footerTotalDocConfig.setDecimalPlace(0);
		footerTotalDocConfig.setPaddingCharacter("0");
		footerTotalDocConfig.setPaddingType(PaddingType.LEFT);
		footerTotalDocConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerTotalDocConfig);

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
		footerFilterConfig.setStartIndex(54);
		footerFilterConfig.setLength(247);
		footerFilterConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerFilterConfig);

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

	private InputStream getFixedLengthFileContent() {
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000020000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}

	private InputStream getFixedLengthFileContent(String[] fixedLengthContent) {
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}
}
