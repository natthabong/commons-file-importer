package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.ItemType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.domain.Channel;

public class SpecificFileConverterTest {

	@InjectMocks
	private AbstractFileConverter<SponsorDocument> cpacFileConverter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FieldValidatorFactory fieldValidatorFactory;

	@Before
	public void setup() {

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();

		cpacFileConverter = new SpecificFileConverter<SponsorDocument>(fileLayoutConfig,
				SponsorDocument.class, fieldValidatorFactory , Channel.WEB);

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void given_valid_file_should_be_success() throws WrongFormatFileException {

		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[2] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";

		InputStream documentFile = getFixedLengthFileContent(cpacFileContent);

		// Actual
		cpacFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_no_header_file_should_be_throw_wrong_file_fomat()
			throws WrongFormatFileException {

		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "";
		cpacFileContent[1] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[2] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";

		InputStream documentFile = getFixedLengthFileContent(cpacFileContent);

		// Assert
		thrown.expect(WrongFormatFileException.class);
		thrown.expectMessage("TXN is required");

		// Actual
		cpacFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_blank_in_first_row_file_should_be_success()
			throws WrongFormatFileException {

		// Arrange
		String[] cpacFileContent = new String[4];
		cpacFileContent[1] = "   ";
		cpacFileContent[1] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[2] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[3] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";

		InputStream documentFile = getFixedLengthFileContent(cpacFileContent);

		// Actual
		cpacFileConverter.checkFileFormat(documentFile);
	}

	@Test
	public void given_detail_valid_format_should_set_invoice_no_correctly()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[2] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("0020000983", document.getOptionVarcharField1());
	}

	@Test
	public void given_detail_valid_format_should_set_invoice_amonnt_correctly()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[2] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("98854.88", document.getOptionNumbericField1().toString());
	}

	@Test
	public void given_detail_valid_format_should_set_negative_invoice_amount_correctly()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           0.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("-1229404.31", document.getOptionNumbericField1().toString());
	}

	@Test
	public void should_clone_matching_reference_in_header()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("0038001491", document.getMatchingRef());
	}

	@Test
	public void should_clone_due_date()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("16/01/2017", new SimpleDateFormat("dd/MM/yyyy")
				.format(document.getOptionDateField1()));
	}

	@Test
	public void should_read_multiple_invoice()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("204433.14", document.getOptionNumbericField1().toString());
	}

	@Test
	public void should_read_multiple_txn()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[6];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           0.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		cpacFileContent[3] = "TXNMC บจ.อังเคิล อ๊อด                                                                                          110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4701033529    0040470040    1966867.26 0038001492     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000053 0020000084 0020000151 0020000155 0020000157 0020000158 00202392    2101934.19      315156.21      135066.93";
		cpacFileContent[4] = "INVINV0020000053    1490602.00-          0.00           0.00";
		cpacFileContent[5] = "INVINVno-โอนสิทธิ์ค่าขนส่งคอนกรีตให้ ลิสซิ่งกสิกรW43/16 โอนสิทธิ์W46/16";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("-1490602.00", document.getOptionNumbericField1().toString());
	}

	@Test
	public void should_calculate_outstanding_amount()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FieldValidatorFactory fieldValidatorFactory = spy(
				new FieldValidatorFactoryTest());

		DefaultFileLayoutConfigItem outstandingAmountConfig = new DefaultFileLayoutConfigItem();
		outstandingAmountConfig.setDocFieldName("outstandingAmount");
		outstandingAmountConfig.setRecordType(RecordType.DETAIL);
		outstandingAmountConfig.setItemType(ItemType.DATA);
		outstandingAmountConfig.setTransient(false);
		outstandingAmountConfig.setDisplayValue("Outstanding Amount");
		outstandingAmountConfig
				.setValidationType(ValidationType.CALCULATE_CPAC_OUTSTANDING);

		FieldValidator outstandingValidator = new CalculateCPACOutstanding();
		doReturn(outstandingValidator).when(fieldValidatorFactory)
				.create(eq(outstandingAmountConfig) , eq(Channel.WEB));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout(outstandingAmountConfig);
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("-1229408.31", document.getOutstandingAmount().toString());
	}

	@Test
	public void should_set_document_type_positive_value_to_RC()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31           0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FieldValidatorFactory fieldValidatorFactory = spy(
				new FieldValidatorFactoryTest());

		DefaultFileLayoutConfigItem doctypeConfig = new DefaultFileLayoutConfigItem();
		doctypeConfig.setDocFieldName("documentType");
		doctypeConfig.setDisplayValue("Document Type");
		doctypeConfig.setRecordType(RecordType.DETAIL);
		doctypeConfig.setItemType(ItemType.DATA);
		doctypeConfig.setValidationType(ValidationType.CPAC_DOC_TYPE);

		FieldValidator docTypeValidator = new CPACDocumentType();
		doReturn(docTypeValidator).when(fieldValidatorFactory).create(eq(doctypeConfig) , eq(Channel.WEB));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout(doctypeConfig);
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("RI", document.getDocumentType());
	}

	@Test
	public void should_set_document_type_negtive_value_to_RC()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FieldValidatorFactory fieldValidatorFactory = spy(
				new FieldValidatorFactoryTest());

		DefaultFileLayoutConfigItem doctypeConfig = new DefaultFileLayoutConfigItem();
		doctypeConfig.setDocFieldName("documentType");
		doctypeConfig.setDisplayValue("Document Type");
		doctypeConfig.setRecordType(RecordType.DETAIL);
		doctypeConfig.setItemType(ItemType.DATA);
		doctypeConfig.setValidationType(ValidationType.CPAC_DOC_TYPE);

		FieldValidator docTypeValidator = new CPACDocumentType();
		doReturn(docTypeValidator).when(fieldValidatorFactory).create(eq(doctypeConfig) , eq(Channel.WEB));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout(doctypeConfig);
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("RC", document.getDocumentType());
	}

	@Test
	public void given_txn_wrong_format_date_when_get_detail_should_throw_exception()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     30022017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertFalse(actualResult.isSuccess());
		assertEquals("Value Date (30022017) invalid format",
				actualResult.getErrorLineDetails().get(0).getErrorMessage());

	}

	@Test
	public void given_first_txn_error_should_skip_to_next_txn()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[6];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509001 4702008897    0040470040    9616790.98 0038001491     30022017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		cpacFileContent[3] = "TXNMC บจ.อังเคิล อ๊อด                                                                                          110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4701033529    0040470040    1966867.26 0038001492     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000053 0020000084 0020000151 0020000155 0020000157 0020000158 00202392    2101934.19      315156.21      135066.93";
		cpacFileContent[4] = "INVINV0020000053    1490602.00-          0.00           0.00";
		cpacFileContent[5] = "INVINVno-โอนสิทธิ์ค่าขนส่งคอนกรีตให้ ลิสซิ่งกสิกรW43/16 โอนสิทธิ์W46/16";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("0038001492", document.getMatchingRef());

	}

	@Test
	public void given_first_txn_error_should_skip_to_next_available_txn()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[9];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509001 4702008897    0040470040    9616790.98 0038001491     30022017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		cpacFileContent[3] = "TXNMC บจ.อังเคิล อ๊อด                                                                                          110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4701033529    0040470040    1966867.26 0038001492     1313017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000053 0020000084 0020000151 0020000155 0020000157 0020000158 00202392    2101934.19      315156.21      135066.93";
		cpacFileContent[4] = "INVINV0020000053    1490602.00-          0.00           0.00";
		cpacFileContent[5] = "INVINVno-โอนสิทธิ์ค่าขนส่งคอนกรีตให้ ลิสซิ่งกสิกรW43/16 โอนสิทธิ์W46/16";
		cpacFileContent[6] = "TXNMC บจ.อังเคิล อ๊อด                                                                                          110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509003 4701033529    0040470040    1966867.26 0038001493     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000053 0020000084 0020000151 0020000155 0020000157 0020000158 00202392    2101934.19      315156.21      135066.93";
		cpacFileContent[7] = "INVINV0020000053    1490602.00-          0.00           0.00";
		cpacFileContent[8] = "INVINVno-โอนสิทธิ์ค่าขนส่งคอนกรีตให้ ลิสซิ่งกสิกรW43/16 โอนสิทธิ์W46/16";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		fileConverter.getDetail();

		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("0038001493", document.getMatchingRef());

	}

	@Test
	public void given_txn_wrong_format_date_when_get_detail_multiple_should_throw_exception()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC_002.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertFalse(actualResult.isSuccess());
		assertEquals("Your Reference is required",
				actualResult.getErrorLineDetails().get(0).getErrorMessage());
		assertEquals("1",
				actualResult.getErrorLineDetails().get(0).getErrorLineNo().toString());
	}

	@Test
	public void given_txn_blank_value_date_when_get_detail_should_error()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC_002.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertFalse(actualResult.isSuccess());
		assertEquals("Value Date is required",
				actualResult.getErrorLineDetails().get(0).getErrorMessage());
		assertEquals("4",
				actualResult.getErrorLineDetails().get(0).getErrorLineNo().toString());
	}

	@Test
	public void given_txn_and_not_has_INV_when_get_detail_should_error()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC_002.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertFalse(actualResult.isSuccess());
		assertEquals("INVINV is required",
				actualResult.getErrorLineDetails().get(0).getErrorMessage());
		assertEquals("10",
				actualResult.getErrorLineDetails().get(0).getErrorLineNo().toString());
	}

	@Test
	public void given_INV_wrong_amount_format_when_get_detail_should_sjip_to_next_INV()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC_002.txt");

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		fileConverter.getDetail();
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertFalse(actualResult.isSuccess());
		assertEquals("Document Amount (        3,5000) invalid format",
				actualResult.getErrorLineDetails().get(0).getErrorMessage());
		assertEquals("13",
				actualResult.getErrorLineDetails().get(0).getErrorLineNo().toString());
	}

	@Test
	public void given_INV_wrong_vat_amount_flag_when_get_detail_should_sjip_to_next_INV()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		InputStream fixedlengthFileContent = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("gec/scf/file/converter/CPAC_002.txt");

		
		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		fileConverter.getDetail();
		fileConverter.getDetail();
		fileConverter.getDetail();
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertFalse(actualResult.isSuccess());
		assertEquals("VAT Amount Flag (A) invalid format",
				actualResult.getErrorLineDetails().get(0).getErrorMessage());
		assertEquals("17",
				actualResult.getErrorLineDetails().get(0).getErrorLineNo().toString());
	}

	@Test
	public void given_txn_blank_value_date_when_get_detail_should_skip_to_next_txn()
			throws WrongFormatFileException, UnsupportedEncodingException {
		// Arrange
		String[] cpacFileContent = new String[6];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           4.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		cpacFileContent[3] = "TXNMC บจ.สานนท์ ขนส่ง                                                                                          206 หมู่ที่ 3 ถ.พหลโยธิน           ต.หน้าพระลาน อ.เฉลิมพระเกียรติ     จ.สระบุรี                               18240บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002     21110281010040211040     65000.00      0038018293 01052017                                                                                                         RM               E-MAI          sanon@gmail.com                                                                   ";
		cpacFileContent[4] = "INVINV0020001443    1229404.31-          0.00           4.00";
		cpacFileContent[5] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(StringUtils
				.join(cpacFileContent, System.lineSeparator()).getBytes("TIS620"));

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, new FieldValidatorFactoryTest() , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		fileConverter.getDetail();
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();
		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("0038018293", document.getMatchingRef());
	}

	protected InputStream getFixedLengthFileContent(String[] fixedLengthContent) {
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}

	private FileLayoutConfig createCPACFileLayout() {
		return createCPACFileLayout(null);
	}

	private FileLayoutConfig createCPACFileLayout(
			DefaultFileLayoutConfigItem additional) {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.SPECIFIC);
		fileLayout.setCharsetName("TIS620");
		fileLayout.setHeaderFlag("TXN");
		fileLayout.setDetailFlag("INVINV");

		List<FileLayoutConfigItem> configItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem headerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		headerRecordTypeConfig.setDocFieldName("recordId");
		headerRecordTypeConfig.setStartIndex(1);
		headerRecordTypeConfig.setLength(3);
		headerRecordTypeConfig.setDisplayValue("Record Identifier");
		headerRecordTypeConfig.setRecordType(RecordType.HEADER);
		headerRecordTypeConfig.setTransient(true);
		configItems.add(headerRecordTypeConfig);

		DefaultFileLayoutConfigItem receivingAccountConfig = new DefaultFileLayoutConfigItem();
		receivingAccountConfig.setStartIndex(308);
		receivingAccountConfig.setLength(14);
		receivingAccountConfig.setDisplayValue("Receiving Account");
		receivingAccountConfig.setRecordType(RecordType.HEADER);
		receivingAccountConfig.setTransient(true);
		receivingAccountConfig.setRequired(true);
		configItems.add(receivingAccountConfig);

		DefaultFileLayoutConfigItem yourReferenceConfig = new DefaultFileLayoutConfigItem();
		yourReferenceConfig.setStartIndex(347);
		yourReferenceConfig.setLength(15);
		yourReferenceConfig.setDisplayValue("Your Reference");
		yourReferenceConfig.setRecordType(RecordType.HEADER);
		yourReferenceConfig.setTransient(true);
		yourReferenceConfig.setRequired(true);
		configItems.add(yourReferenceConfig);

		DefaultFileLayoutConfigItem valueDateConfig = new DefaultFileLayoutConfigItem();
		valueDateConfig.setStartIndex(362);
		valueDateConfig.setLength(8);
		valueDateConfig.setDisplayValue("Value Date");
		valueDateConfig.setRecordType(RecordType.HEADER);
		valueDateConfig.setDatetimeFormat("ddMMyyyy");
		valueDateConfig.setTransient(true);
		valueDateConfig.setRequired(true);
		configItems.add(valueDateConfig);

		DefaultFileLayoutConfigItem matchingRefConfig = new DefaultFileLayoutConfigItem();
		matchingRefConfig.setDocFieldName("matchingRef");
		matchingRefConfig.setDisplayValue("Matching Reference No.");
		matchingRefConfig.setRecordType(RecordType.DETAIL);
		matchingRefConfig.setItemType(ItemType.DATA);
		matchingRefConfig.setRequired(true);
		matchingRefConfig.setValidationRecordFieldConfig(yourReferenceConfig);
		matchingRefConfig.setValidationType(ValidationType.CLONE_VALUE);
		configItems.add(matchingRefConfig);

		DefaultFileLayoutConfigItem dueDateConfig = new DefaultFileLayoutConfigItem();
		dueDateConfig.setDocFieldName("optionDateField1");
		dueDateConfig.setDisplayValue("Due Date");
		dueDateConfig.setRecordType(RecordType.DETAIL);
		dueDateConfig.setItemType(ItemType.DATA);
		dueDateConfig.setDatetimeFormat("ddMMyyyy");
		dueDateConfig.setValidationRecordFieldConfig(valueDateConfig);
		dueDateConfig.setValidationType(ValidationType.CLONE_VALUE);
		configItems.add(dueDateConfig);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setDocFieldName("recordId");
		detailRecordTypeConfig.setDisplayValue("Record Identifier");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(6);
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
		detailRecordTypeConfig.setTransient(true);
		configItems.add(detailRecordTypeConfig);

		DefaultFileLayoutConfigItem invoiceNoConfig = new DefaultFileLayoutConfigItem();
		invoiceNoConfig.setDocFieldName("optionVarcharField1");
		invoiceNoConfig.setDisplayValue("Invoice No.");
		invoiceNoConfig.setStartIndex(7);
		invoiceNoConfig.setLength(10);
		invoiceNoConfig.setRecordType(RecordType.DETAIL);
		configItems.add(invoiceNoConfig);

		DefaultFileLayoutConfigItem docAmountFlagConfig = new DefaultFileLayoutConfigItem();
		docAmountFlagConfig.setStartIndex(31);
		docAmountFlagConfig.setLength(1);
		docAmountFlagConfig.setRecordType(RecordType.DETAIL);
		docAmountFlagConfig.setTransient(true);
		docAmountFlagConfig.setPlusSymbol(" ");
		docAmountFlagConfig.setMinusSymbol("-");
		docAmountFlagConfig.setDisplayValue("Document Amount Flag");
		docAmountFlagConfig.setPaddingCharacter(null);

		configItems.add(docAmountFlagConfig);

		DefaultFileLayoutConfigItem docAmountConfig = new DefaultFileLayoutConfigItem();
		docAmountConfig.setSignFlagConfig(docAmountFlagConfig);
		docAmountConfig.setDocFieldName("optionNumbericField1");
		docAmountConfig.setStartIndex(17);
		docAmountConfig.setLength(14);
		docAmountConfig.setPaddingCharacter(" ");
		docAmountConfig.setPaddingType(PaddingType.LEFT);
		docAmountConfig.setDecimalPlace(2);
		docAmountConfig.setHasDecimalPlace(true);
		docAmountConfig.setRecordType(RecordType.DETAIL);
		docAmountConfig.setTransient(false);
		docAmountConfig.setRequired(true);
		docAmountConfig.setDisplayValue("Document Amount");
		configItems.add(docAmountConfig);

		DefaultFileLayoutConfigItem vatAmountFlagConfig = new DefaultFileLayoutConfigItem();
		vatAmountFlagConfig.setStartIndex(46);
		vatAmountFlagConfig.setLength(1);
		vatAmountFlagConfig.setRecordType(RecordType.DETAIL);
		vatAmountFlagConfig.setTransient(true);
		vatAmountFlagConfig.setPlusSymbol(" ");
		vatAmountFlagConfig.setMinusSymbol("-");
		vatAmountFlagConfig.setDisplayValue("VAT Amount Flag");
		vatAmountFlagConfig.setPaddingCharacter(null);
		configItems.add(vatAmountFlagConfig);

		DefaultFileLayoutConfigItem vatAmountConfig = new DefaultFileLayoutConfigItem();
		vatAmountConfig.setSignFlagConfig(vatAmountFlagConfig);
		vatAmountConfig.setDocFieldName("optionNumbericField2");
		vatAmountConfig.setStartIndex(32);
		vatAmountConfig.setLength(14);
		vatAmountConfig.setPaddingCharacter(" ");
		vatAmountConfig.setPaddingType(PaddingType.LEFT);
		vatAmountConfig.setDecimalPlace(2);
		vatAmountConfig.setHasDecimalPlace(true);
		vatAmountConfig.setRecordType(RecordType.DETAIL);
		vatAmountConfig.setTransient(false);
		vatAmountConfig.setDisplayValue("VAT Amount");
		configItems.add(vatAmountConfig);

		DefaultFileLayoutConfigItem whtAmountFlagConfig = new DefaultFileLayoutConfigItem();
		whtAmountFlagConfig.setStartIndex(61);
		whtAmountFlagConfig.setLength(1);
		whtAmountFlagConfig.setRecordType(RecordType.DETAIL);
		whtAmountFlagConfig.setTransient(true);
		whtAmountFlagConfig.setPlusSymbol(" ");
		whtAmountFlagConfig.setMinusSymbol("-");
		whtAmountFlagConfig.setDisplayValue("WHT Amount Flag");
		whtAmountFlagConfig.setPaddingCharacter(null);
		configItems.add(whtAmountFlagConfig);

		DefaultFileLayoutConfigItem whtAmountConfig = new DefaultFileLayoutConfigItem();
		whtAmountConfig.setSignFlagConfig(whtAmountFlagConfig);
		whtAmountConfig.setDocFieldName("optionNumbericField3");
		whtAmountConfig.setStartIndex(47);
		whtAmountConfig.setLength(14);
		whtAmountConfig.setPaddingCharacter(" ");
		whtAmountConfig.setPaddingType(PaddingType.LEFT);
		whtAmountConfig.setDecimalPlace(2);
		whtAmountConfig.setHasDecimalPlace(true);
		whtAmountConfig.setRecordType(RecordType.DETAIL);
		whtAmountConfig.setTransient(false);
		whtAmountConfig.setDisplayValue("WHT Amount");
		configItems.add(whtAmountConfig);

		if (additional != null) {
			configItems.add(additional);
		}

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

	private final class CloneCustomerCodeValidatorStub
			implements FieldValidator, FieldValueSetter, DataObserver<String> {

		private static final String CASE_NOT_EXISTING = "{0} ({1}) is not exist {0}";

		private static final String CASE_INACTIVE = "{0} ({1}) is inactive {0}";

		private String value;

		@Override
		public void setValue(Object target, Object customerCodeData) {

			try {
				if (value != null) {
					value = value.trim();
					PropertyUtils.setProperty(target, "supllierCOde", value);
				}
			}
			catch (IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				e.printStackTrace();
			}

		}

		@Override
		public RecordType getObserveSection() {
			return RecordType.HEADER;
		}

		@Override
		public void observe(Object data) {
			value = String.valueOf(data);

		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public FileLayoutConfigItem getObserveFieldConfig() {
			return null;
		}

		@Override
		public void validate(Object dataValidate) throws WrongFormatFileException {
			String customerCodeData = String.valueOf(dataValidate).trim();
			if ("000000".equals(customerCodeData)) {
				throw new WrongFormatDetailException(MessageFormat
						.format(CASE_NOT_EXISTING, "Supplier code", "000000"));
			}
			else if ("000001".equals(customerCodeData)) {
				throw new WrongFormatDetailException(
						MessageFormat.format(CASE_INACTIVE, "Supplier code", "0000001"));
			}

		}
	}

	private final class CalculateCPACOutstanding
			implements FieldValidator, FieldValueSetter {
		@Override
		public void validate(Object dataValidate) throws WrongFormatFileException {

		}

		@Override
		public void setValue(Object target, Object value) {
			try {
				BigDecimal imvoiceAmount = (BigDecimal) PropertyUtils.getProperty(target,
						"optionNumbericField1");
				Object wht = PropertyUtils.getProperty(target, "optionNumbericField3");
				if (wht != null) {
					imvoiceAmount = imvoiceAmount.subtract((BigDecimal) wht);
				}
				PropertyUtils.setProperty(target, "outstandingAmount", imvoiceAmount);

			}
			catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private final class CPACDocumentType implements FieldValidator, FieldValueSetter {

		@Override
		public void validate(Object dataValidate) throws WrongFormatFileException {

		}

		@Override
		public void setValue(Object target, Object value) {
			try {
				BigDecimal imvoiceAmount = (BigDecimal) PropertyUtils.getProperty(target,
						"optionNumbericField1");
				String documentType = "RI";
				if (imvoiceAmount.compareTo(new BigDecimal("0.0")) < 0) {
					documentType = "RC";
				}
				PropertyUtils.setProperty(target, "documentType", documentType);

			}
			catch (Exception e) {
				// TODO: handle exception
			}

		}
	}
}
