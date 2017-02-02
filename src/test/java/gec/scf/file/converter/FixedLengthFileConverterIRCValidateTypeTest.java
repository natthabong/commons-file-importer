package gec.scf.file.converter;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;

public class FixedLengthFileConverterIRCValidateTypeTest
		extends AbstractFixedLengthConverterTest {

	private FileConverter<SponsorDocument> fixLengthFileConverter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

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

		FileLayoutConfig fileLayoutConfig = createMakroFixedLengthFileLayout();
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document No (1) is invalid. Total detail line is 2");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_total_document_in_the_footer_have_2_record_is_not_equal_to_total_of_document_in_detail_have_1_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException {

		// Arrange
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000020000000099000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createMakroFixedLengthFileLayout();
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
				"Total Document Amount (99,000.00) is invalid. Total detail line is 101,000.00");

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

		FileLayoutConfig fileLayoutConfig = createMakroFixedLengthFileLayout();
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document Amount (00000100,000000) invalid format");

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

		FileLayoutConfig fileLayoutConfig = createMakroFixedLengthFileLayout();
		fixLengthFileConverter = stubToAnswerValidation(fileLayoutConfig);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage(
				"Total Document Amount (99,000.00) is invalid. Total detail line is 101,000.00");

		// Actual
		fixLengthFileConverter.checkFileFormat(documentFile);
	}


}
