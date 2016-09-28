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
	public void given_a_file_which_has_small_case_hader_flag_when_check_file_format_should_throw_WrongFormatFileException()
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
	public void given_header_total_length_300_when_check_file_format_found_301_should_throw_WrongFileFormatException()
			throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[1];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       X";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("data length (301) must have 300 characters");

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
		thrown.expectMessage("data length (300) must have 301 characters");

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
		fileContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
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
		fileContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
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
		fileContent[2] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
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

	private FileLayoutConfig createFixedLengthFileLayout() {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.FIXED_LENGTH);
		fileLayout.setHeaderFlag("H");
		fileLayout.setDetailFlag("D");
		fileLayout.setFooterFlag("T");

		List<FileLayoutConfigItem> configItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem headerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		headerRecordTypeConfig.setFieldName("recordId");
		headerRecordTypeConfig.setStartIndex(1);
		headerRecordTypeConfig.setLength(1);
		headerRecordTypeConfig.setDisplayOfField("Record Type");
		headerRecordTypeConfig.setRecordType(RecordType.HEADER);

		configItems.add(headerRecordTypeConfig);

		DefaultFileLayoutConfigItem filterConfig = new DefaultFileLayoutConfigItem();
		filterConfig.setFieldName("filter");
		filterConfig.setStartIndex(54);
		filterConfig.setLength(247);
		filterConfig.setRecordType(RecordType.HEADER);

		configItems.add(filterConfig);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setFieldName("recordId");
		detailRecordTypeConfig.setDisplayOfField("Record Type");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(1);
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);

		configItems.add(detailRecordTypeConfig);

		DefaultFileLayoutConfigItem detailFilterConfig = new DefaultFileLayoutConfigItem();
		detailFilterConfig.setFieldName("filter");
		detailFilterConfig.setStartIndex(54);
		detailFilterConfig.setLength(248);
		detailFilterConfig.setRecordType(RecordType.DETAIL);

		configItems.add(detailFilterConfig);

		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		footerRecordTypeConfig.setFieldName("recordId");
		footerRecordTypeConfig.setDisplayOfField("Record Type");
		footerRecordTypeConfig.setStartIndex(1);
		footerRecordTypeConfig.setLength(1);
		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerRecordTypeConfig);

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setFieldName("filter");
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
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
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
