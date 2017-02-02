package gec.scf.file.converter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatFileException;

public class FixedLengthFileConverterIRCValidateTypeDateTimeFooterTest {

	private final class FieldValidatorAnswer implements Answer<FieldValidator> {

		private ValidationType validationType;

		public FieldValidatorAnswer(ValidationType validationType) {
			this.validationType = validationType;
		}

		@Override
		public FieldValidator answer(InvocationOnMock invocation) throws Throwable {

			final FileLayoutConfigItem configItem = (FileLayoutConfigItem) invocation
			        .getArguments()[0];

			FieldValidator fieldValidator = new FieldValidator() {
				private Map<String, Object> dataToValidate;

				@Override
				public void validate(String dataValidate)
				        throws WrongFormatFileException {

					// DateTimeFormatter formatter = DateTimeFormatter
					// .ofPattern(configItem.getDatetimeFormat(), Locale.US);
					// LocalDate documentDateTime = LocalDate.parse(dataValidate,
					// formatter);
					// LocalDate currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);

					switch (validationType) {
					case EQUAL_TO_UPLOAD_DATE:
						DateTimeFormatter formatter = DateTimeFormatter
						        .ofPattern(configItem.getDatetimeFormat(), Locale.US);
						LocalDate documentDateTime = LocalDate.parse(dataValidate,
						        formatter);
						LocalDate currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);

						if (!documentDateTime.isEqual(currentDateTime)) {
							String errorMessage = MessageFormat.format(
							        CovertErrorConstant.EQUAL_TO_UPLOAD_DATE_INVALID,
							        configItem.getDisplayValue(),
							        formatter.format(documentDateTime),
							        formatter.format(currentDateTime));
							throw new WrongFormatFileException(errorMessage);
						}
						break;
					case EQUAL_OR_GREATER_THAN_UPLOAD_DATE:
						formatter = DateTimeFormatter
						        .ofPattern(configItem.getDatetimeFormat(), Locale.US);
						documentDateTime = LocalDate.parse(dataValidate, formatter);
						currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);

						boolean isEqualCurrentUploadDate = false;
						boolean isGreaterThanCurrentDate = false;
						if (documentDateTime.isEqual(currentDateTime)) {
							isEqualCurrentUploadDate = true;
						}

						if (documentDateTime.isAfter(currentDateTime)) {
							isGreaterThanCurrentDate = true;
						}

						if (!isEqualCurrentUploadDate && !isGreaterThanCurrentDate) {
							String errorMessage = MessageFormat.format(
							        CovertErrorConstant.EQUAL_OR_GREATER_THAN_UPLOAD_DATE_INVALID,
							        configItem.getDisplayValue(),
							        formatter.format(documentDateTime),
							        formatter.format(currentDateTime));
							throw new WrongFormatFileException(errorMessage);
						}
						break;
					case GREATER_THAN_UPLOAD_DATE:
						formatter = DateTimeFormatter
						        .ofPattern(configItem.getDatetimeFormat(), Locale.US);
						documentDateTime = LocalDate.parse(dataValidate, formatter);
						currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);

						if (!documentDateTime.isAfter(currentDateTime)) {
							String errorMessage = MessageFormat.format(
							        CovertErrorConstant.GREATER_THAN_UPLOAD_DATE_INVALID,
							        configItem.getDisplayValue(),
							        formatter.format(documentDateTime),
							        formatter.format(currentDateTime));
							throw new WrongFormatFileException(errorMessage);
						}
						break;

					case EQUAL_OR_LESS_THAN_UPLOAD_DATE:
						formatter = DateTimeFormatter
						        .ofPattern(configItem.getDatetimeFormat(), Locale.US);
						documentDateTime = LocalDate.parse(dataValidate, formatter);
						currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);
						isEqualCurrentUploadDate = false;
						boolean isLessThanCurrentDate = false;
						if (documentDateTime.isEqual(currentDateTime)) {
							isEqualCurrentUploadDate = true;
						}

						if (documentDateTime.isBefore(currentDateTime)) {
							isLessThanCurrentDate = true;
						}

						if (!isEqualCurrentUploadDate && !isLessThanCurrentDate) {
							String errorMessage = MessageFormat.format(
							        CovertErrorConstant.EQUAL_OR_LESS_THAN_UPLOAD_DATE_INVALID,
							        configItem.getDisplayValue(),
							        formatter.format(documentDateTime),
							        formatter.format(currentDateTime));
							throw new WrongFormatFileException(errorMessage);
						}
						break;
					case LESS_THAN_UPLOAD_DATE:
						formatter = DateTimeFormatter
						        .ofPattern(configItem.getDatetimeFormat(), Locale.US);
						documentDateTime = LocalDate.parse(dataValidate, formatter);
						currentDateTime = LocalDate.of(2017, Month.FEBRUARY, 1);
						if (!documentDateTime.isBefore(currentDateTime)) {
							String errorMessage = MessageFormat.format(
							        CovertErrorConstant.LESS_THAN_UPLOAD_DATE_INVALID,
							        configItem.getDisplayValue(),
							        formatter.format(documentDateTime),
							        formatter.format(currentDateTime));
							throw new WrongFormatFileException(errorMessage);
						}
						break;
					case EQUAL_TO_HEADER_FIELD:
						/*
						 * TODO: please implements case equal to header
						 */
						break;
					case COUNT_OF_DOCUMENT_DETAIL:
						int footerTotalDocument = Integer.parseInt(dataValidate);
						int totalDetail = (Integer) dataToValidate
						        .get("COUNT_OF_DOCUMENT_DETAIL");
						
						if (footerTotalDocument != totalDetail) {
							throw new WrongFormatFileException(MessageFormat.format(
							        CovertErrorConstant.FOOTER_TOTAL_LINE_INVALIDE_LENGTH_MESSAGE,
							        configItem.getDisplayValue(), footerTotalDocument,
							        totalDetail));
						}

						break;
					}
				}

				@Override
				public void setDataToValidate(Map<String, Object> dataToValidate) {
					this.dataToValidate = dataToValidate;

				}
			};

			return fieldValidator;
		}
	}

	private void stubToAnswerValidationType(ValidationType validationType) {
		when(fieldValidatorFactory.create(any(FileLayoutConfigItem.class)))
		        .thenAnswer(new FieldValidatorAnswer(validationType));
		fixLengthFileConverter.setFieldValidatorFactory(fieldValidatorFactory);
	}

	private FileConverter<SponsorDocument> fixLengthFileConverter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FieldValidatorFactory fieldValidatorFactory;
	
	@Mock
	private FileObserverFactory fileObserverFactory;

	@Before
	public void setup() {
		// LocalDate mockCurrentDate = LocalDate.of(2017, Month.FEBRUARY, 1);
		// Mockito.when(LocalDate.now()).thenReturn(mockCurrentDate);
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

		stubToAnswerValidationType(ValidationType.EQUAL_TO_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_TO_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_OR_GREATER_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_OR_GREATER_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_OR_GREATER_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.GREATER_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_OR_LESS_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_OR_LESS_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.EQUAL_OR_LESS_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.LESS_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.LESS_THAN_UPLOAD_DATE);

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

		stubToAnswerValidationType(ValidationType.LESS_THAN_UPLOAD_DATE);

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
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		stubToAnswerValidationType(ValidationType.COUNT_OF_DOCUMENT_DETAIL);

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
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "T0000020000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream documentFile = getFixedLengthFileContent(fixedLengthContent);

		FileLayoutConfig fileLayoutConfig = createMakroFixedLengthFileLayout();
		fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);

		stubToAnswerValidationType(ValidationType.COUNT_OF_DOCUMENT_DETAIL);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("Total Document No (2) is invalid. Total detail line is 1");

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

	private FileLayoutConfig createMakroFixedLengthFileLayout() {
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
		documentDateConfig.setRequired(true);
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
		docAmountConfig.setHasDecimalPlace(false);
		docAmountConfig.setHas1000Separator(false);
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
		footerTotalDocConfig.setPaddingCharacter("0");
		footerTotalDocConfig.setPaddingType(PaddingType.LEFT);
		footerTotalDocConfig.setRecordType(RecordType.FOOTER);
		footerTotalDocConfig.setValidationType(ValidationType.COUNT_OF_DOCUMENT_DETAIL);
		configItems.add(footerTotalDocConfig);

		DefaultFileLayoutConfigItem footerDocAmountConfig = new DefaultFileLayoutConfigItem();
		footerDocAmountConfig.setDocFieldName("totalDocumentAmount");
		footerDocAmountConfig.setDisplayValue("Total Document Amount");
		footerDocAmountConfig.setStartIndex(8);
		footerDocAmountConfig.setLength(15);
		footerDocAmountConfig.setDecimalPlace(2);
		footerDocAmountConfig.setHasDecimalPlace(false);
		footerDocAmountConfig.setHas1000Separator(false);
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
}
