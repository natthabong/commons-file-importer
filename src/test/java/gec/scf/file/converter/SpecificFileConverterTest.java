package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

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
				SponsorDocument.class, fieldValidatorFactory);

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
			throws WrongFormatFileException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[2] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(cpacFileContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
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
			throws WrongFormatFileException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020000983      98854.88        6467.14           0.00";
		cpacFileContent[2] = "INVINVNO/เพิ่มหนี้ค่าหินทราย  09/59  ภ.ตต               DR59/12/D02";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(cpacFileContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
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
			throws WrongFormatFileException {
		// Arrange
		String[] cpacFileContent = new String[3];
		cpacFileContent[0] = "TXNMC บจ.รจนาพัฒนา                                                                                             110/465 ม.3 ถ.ปิ่นเกล้า-นครชัยศรี  ต.นครชัยศรี อ.นครชัยศรี            จ.นครปฐม                           73120     บ.ผลิตภัณฑ์&วัตถุก่อสร้าง                                             0003509002 4702008897    0040470040    9616790.98 0038001491     16012017                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   0020000983 0020001145 0020001423 0020242951 0020247508 0020247514 00202483    9618437.51     1406581.97        1646.53";
		cpacFileContent[1] = "INVINV0020001423    1229404.31-          0.00           0.00";
		cpacFileContent[2] = "INVINVNO/ได้โอนสิทธิ์ให้ บจก.พีเอส อุตสาหกรรม           โอนสิทธิ์";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(cpacFileContent, System.lineSeparator()).getBytes());

		FileLayoutConfig fileLayoutConfig = createCPACFileLayout();
		SpecificFileConverter<SponsorDocument> fileConverter = new SpecificFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class);
		fileConverter.checkFileFormat(fixedlengthFileContent);

		// Actual
		DetailResult<SponsorDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		SponsorDocument document = (SponsorDocument) actualResult.getObjectValue();
		assertEquals("-1229404.31", document.getOptionNumbericField1().toString());
	}

	protected InputStream getFixedLengthFileContent(String[] fixedLengthContent) {
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}

	private FileLayoutConfig createCPACFileLayout() {
		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.SPECIFIC);
		fileLayout.setCharsetName("UTF-8");
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

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

	private final class CloneCustomerCodeValidatorStub
			implements FieldValidator, FieldValueSetter {

		@Override
		public void setValue(Object target, Object value) {
			try {
				PropertyUtils.setProperty(target, "supplierCode", value);
			}
			catch (IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void validate(Object dataValidate) throws WrongFormatDetailException {

		}
	}
}
