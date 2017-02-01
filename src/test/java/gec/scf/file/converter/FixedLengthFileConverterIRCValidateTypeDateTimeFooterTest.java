package gec.scf.file.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;

public class FixedLengthFileConverterIRCValidateTypeDateTimeFooterTest {

	private FileConverter<SponsorDocument> fixLengthFileConverter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void setup() {
//		Mockito.when(LocalDate.now()).thenReturn(LocalDate.now());
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_TO_UPLOAD_DATE_and_current_date_2017_02_01_when_upload_file_then_should_not_throw_exception()
	        throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170201155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170201155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_TO_UPLOAD_DATE);

		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Mockito.when(fixLengthFileConverter.getSystemDateTime()).thenReturn(LocalDateTime.of(2017,
		// Month.FEBRUARY, 1, 5, 0));

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_TO_UPLOAD_DATE_and_date_in_file_is_2017_01_30_and_current_date_2017_02_01_when_upload_file_then_should_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170130155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170130155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_TO_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Send date (20170130) not equals current date (20170201)");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_OR_GREATER_THAN_UPLOAD_DATE_and_date_in_file_is_2017_01_30_and_current_date_2017_02_01_when_upload_file_then_should_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170130155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170130155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_OR_GREATER_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
		        "Send date (20170130) not equals or greater than current date (20170201)");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_OR_GREATER_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_01_and_current_date_2017_02_01_when_upload_file_then_should_not_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170201155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170201155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_OR_GREATER_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_OR_GREATER_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_04_and_current_date_2017_02_01_when_upload_file_then_should_not_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170204155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170204155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.GREATER_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_GREATER_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_01_and_current_date_2017_02_01_when_upload_file_then_should_throw_exception()
	        throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170201155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170201155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.GREATER_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
		        "Send date (20170201) not greater than current date (20170201)");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_OR_LESS_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_01_and_current_date_2017_02_01_when_upload_file_then_should_not_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170201155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170201155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_OR_LESS_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_OR_LESS_THAN_UPLOAD_DATE_and_date_in_file_is_2017_01_30_and_current_date_2017_02_01_when_upload_file_then_should_not_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170130155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170130155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_OR_LESS_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_EQUAL_OR_LESS_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_05_and_current_date_2017_02_01_when_upload_file_then_should_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170205155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170205155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.EQUAL_OR_LESS_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
		        "Send date (20170205) not equal or less than current date (20170201)");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_config_footer_validation_type_is_LESS_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_05_and_current_date_2017_02_01_when_upload_file_then_should_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170205155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170205155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);
		
		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.LESS_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
		        "Send date (20170205) not less than current date (20170201)");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}
	
	@Test
	public void given_config_footer_validation_type_is_LESS_THAN_UPLOAD_DATE_and_date_in_file_is_2017_02_01_and_current_date_2017_02_01_when_upload_file_then_should_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170201155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170201155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);
		
		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.LESS_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
		        "Send date (20170201) not less than current date (20170201)");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}
	
	@Test
	public void given_config_footer_validation_type_is_LESS_THAN_UPLOAD_DATE_and_date_in_file_is_2017_01_31_and_current_date_2017_02_01_when_upload_file_then_should_not_throw_exception()
	        throws WrongFormatFileException {
		// Arrange
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20170131155917Inoue Rubber (Thailand) Plc.  IRC  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DIRC  100700              RE 5113010964          2014/001            20140104201403102014031000000000157750110000000015775011                                                                                                                                                                               ";
		fixedLengthContent[2] = "F20170131155917Inoue Rubber (Thailand) Plc.  IRC  00400025100000201316445610000020131644561                                                                                                                                                                                                                 ";

		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);
		
		FileLayoutConfig fileLayoutConfig = createFixedLengthFileLayout("Send date",
		        "yyyyMMdd", ValidationType.LESS_THAN_UPLOAD_DATE);
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	private InputStream getFixedLengthFileContent(String[] fixedLengthContent) {
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
		        StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}

	private FileLayoutConfig createFixedLengthFileLayout(String displayValue,
	        String dateFormat, ValidationType validationType) {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.FIXED_LENGTH);
		fileLayout.setHeaderFlag("H");
		fileLayout.setDetailFlag("D");
		fileLayout.setFooterFlag("F");

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
		filterConfig.setStartIndex(92);
		filterConfig.setLength(209);
		filterConfig.setRecordType(RecordType.HEADER);
		configItems.add(filterConfig);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setDocFieldName("recordId");
		detailRecordTypeConfig.setDisplayValue("Record Type");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(1);
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
		configItems.add(detailRecordTypeConfig);

		DefaultFileLayoutConfigItem detailFilterConfig = new DefaultFileLayoutConfigItem();
		detailFilterConfig.setDocFieldName("filter");
		detailFilterConfig.setStartIndex(126);
		detailFilterConfig.setLength(175);
		detailFilterConfig.setRecordType(RecordType.DETAIL);
		configItems.add(detailFilterConfig);

		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		footerRecordTypeConfig.setDocFieldName("recordId");
		footerRecordTypeConfig.setDisplayValue("Record Type");
		footerRecordTypeConfig.setStartIndex(1);
		footerRecordTypeConfig.setLength(1);
		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);
		configItems.add(footerRecordTypeConfig);

		DefaultFileLayoutConfigItem footerSendDateConfig = new DefaultFileLayoutConfigItem();
		footerSendDateConfig.setLength(8);
		footerSendDateConfig.setStartIndex(2);
		footerSendDateConfig.setRecordType(RecordType.FOOTER);
		footerSendDateConfig.setDatetimeFormat(dateFormat);
		footerSendDateConfig.setTransient(true);
		footerSendDateConfig.setDisplayValue(displayValue);
		footerSendDateConfig.setValidationType(validationType);
		configItems.add(footerSendDateConfig);

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setDocFieldName("filter");
		footerFilterConfig.setStartIndex(92);
		footerFilterConfig.setLength(209);
		footerFilterConfig.setRecordType(RecordType.FOOTER);
		configItems.add(footerFilterConfig);

		fileLayout.setConfigItems(configItems);
		return fileLayout;
	}
}
