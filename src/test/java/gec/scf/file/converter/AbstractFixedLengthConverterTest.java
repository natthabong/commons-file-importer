package gec.scf.file.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.importer.domain.Channel;

public class AbstractFixedLengthConverterTest {

	protected AbstractFileConverter<SponsorDocument> stubToAnswerValidation(
			FileLayoutConfig fileLayoutConfig) {

		FieldValidatorFactory fieldValidatorFactory = new FieldValidatorFactoryTest();
		AbstractFileConverter<SponsorDocument> fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory , Channel.WEB);

		return fixLengthFileConverter;
	}

	protected AbstractFileConverter<SponsorDocument> spyToAnswerValidation(
			FieldValidatorFactory fieldValidatorFactory,
			FileLayoutConfig fileLayoutConfig) {

		AbstractFileConverter<SponsorDocument> fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
				fileLayoutConfig, SponsorDocument.class, fieldValidatorFactory , Channel.WEB);
		return fixLengthFileConverter;
	}

	protected InputStream getFixedLengthFileContent(String[] fixedLengthContent) {
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}

	protected DefaultFileLayoutConfigItem prepareFileLayoutConfigItem(String displayValue,
			String dateFormat, ValidationType validationType) {
		DefaultFileLayoutConfigItem footerSendDateConfig = new DefaultFileLayoutConfigItem();
		footerSendDateConfig.setLength(8);
		footerSendDateConfig.setStartIndex(2);
		footerSendDateConfig.setRecordType(RecordType.FOOTER);
		footerSendDateConfig.setDatetimeFormat(dateFormat);
		footerSendDateConfig.setTransient(true);
		footerSendDateConfig.setDisplayValue(displayValue);
		footerSendDateConfig.setValidationType(validationType);
		return footerSendDateConfig;
	}

	protected FileLayoutConfig createFixedLengthFileLayout(
			DefaultFileLayoutConfigItem fileLayoutConfigItem) {
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

		if (fileLayoutConfigItem != null) {
			configItems.add(fileLayoutConfigItem);
		}

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setDocFieldName("filter");
		footerFilterConfig.setStartIndex(92);
		footerFilterConfig.setLength(209);
		footerFilterConfig.setExpectedValue(" ");
		footerFilterConfig.setRecordType(RecordType.FOOTER);
		configItems.add(footerFilterConfig);

		fileLayout.setConfigItems(configItems);
		return fileLayout;
	}

	protected FileLayoutConfig createMakroFixedLengthFileLayoutNoValidation() {
		return createMakroFixedLengthFileLayout(null);
	}

	protected FileLayoutConfig createMakroFixedLengthFileLayout(
			DefaultFileLayoutConfigItem otherConfig) {

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
		detailRecordTypeConfig.setTransient(true);

		configItems.add(detailRecordTypeConfig);

		if (otherConfig == null) {
			DefaultFileLayoutConfigItem corporateCode = new DefaultFileLayoutConfigItem();
			corporateCode.setDocFieldName(null);
			corporateCode.setStartIndex(2);
			corporateCode.setLength(5);
			corporateCode.setExpectedValue("MAK");
			corporateCode.setRecordType(RecordType.DETAIL);
			corporateCode.setDisplayValue("Corporate Code");
			corporateCode.setTransient(false);

			configItems.add(corporateCode);

			DefaultFileLayoutConfigItem supplierCode = new DefaultFileLayoutConfigItem();
			supplierCode.setDocFieldName("supplierCode");
			supplierCode.setStartIndex(7);
			supplierCode.setLength(20);
			supplierCode.setRequired(true);
			supplierCode.setRecordType(RecordType.DETAIL);
			supplierCode.setTransient(false);
			supplierCode.setDisplayValue("Supplier Code");

			configItems.add(supplierCode);

			DefaultFileLayoutConfigItem receiptNumber = new DefaultFileLayoutConfigItem();
			receiptNumber.setDocFieldName("documentNo");
			receiptNumber.setStartIndex(27);
			receiptNumber.setLength(20);
			receiptNumber.setRequired(true);
			receiptNumber.setRecordType(RecordType.DETAIL);
			receiptNumber.setTransient(false);
			receiptNumber.setDisplayValue("Receipt Number");

			configItems.add(receiptNumber);

			DefaultFileLayoutConfigItem receiptDate = new DefaultFileLayoutConfigItem();
			receiptDate.setDocFieldName("documentDate");
			receiptDate.setStartIndex(47);
			receiptDate.setLength(8);
			receiptDate.setRequired(true);
			receiptDate.setRecordType(RecordType.DETAIL);
			receiptDate.setTransient(false);
			receiptDate.setDatetimeFormat("yyyyMMdd");
			receiptDate.setDisplayValue("Receipt Date");

			configItems.add(receiptDate);

			DefaultFileLayoutConfigItem documentDueDate = new DefaultFileLayoutConfigItem();
			documentDueDate.setDocFieldName("sponsorPaymentDate");
			documentDueDate.setStartIndex(55);
			documentDueDate.setLength(8);
			documentDueDate.setRequired(true);
			documentDueDate.setRecordType(RecordType.DETAIL);
			documentDueDate.setTransient(false);
			documentDueDate.setDatetimeFormat("yyyyMMdd");
			documentDueDate.setDisplayValue("Document Due Date");

			configItems.add(documentDueDate);

			DefaultFileLayoutConfigItem docAmountFlagConfig = new DefaultFileLayoutConfigItem();
			docAmountFlagConfig.setDocFieldName("optionVarcharField2");
			docAmountFlagConfig.setStartIndex(78);
			docAmountFlagConfig.setLength(1);
			docAmountFlagConfig.setRecordType(RecordType.DETAIL);
			docAmountFlagConfig.setTransient(true);
			docAmountFlagConfig.setPlusSymbol("1");
			docAmountFlagConfig.setMinusSymbol("0");
			docAmountFlagConfig.setDisplayValue("Document Amount Flag");

			configItems.add(docAmountFlagConfig);

			DefaultFileLayoutConfigItem docAmountConfig = new DefaultFileLayoutConfigItem();
			docAmountConfig.setSignFlagConfig(docAmountFlagConfig);
			docAmountConfig.setDocFieldName("documentAmount");
			docAmountConfig.setStartIndex(63);
			docAmountConfig.setLength(15);
			docAmountConfig.setPaddingCharacter("0");
			docAmountConfig.setPaddingType(PaddingType.LEFT);
			docAmountConfig.setDecimalPlace(2);
			docAmountConfig.setHasDecimalPlace(false);
			docAmountConfig.setRecordType(RecordType.DETAIL);
			docAmountConfig.setTransient(false);
			docAmountConfig.setRequired(true);

			docAmountConfig.setDisplayValue("Document Amount");

			configItems.add(docAmountConfig);

			DefaultFileLayoutConfigItem documentType = new DefaultFileLayoutConfigItem();
			documentType.setDocFieldName("documentType");
			documentType.setRecordType(RecordType.DETAIL);
			documentType.setDefaultValue("INV");
			documentType.setTransient(false);

			configItems.add(documentType);
		}
		else {
			configItems.add(otherConfig);
		}

		DefaultFileLayoutConfigItem detailFilterConfig = new DefaultFileLayoutConfigItem();
		detailFilterConfig.setDocFieldName("filter");
		detailFilterConfig.setStartIndex(79);
		detailFilterConfig.setLength(222);
		detailFilterConfig.setRecordType(RecordType.DETAIL);
		detailFilterConfig.setTransient(true);

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
		footerTotalDocConfig.setStartIndex(2);
		footerTotalDocConfig.setLength(6);
		footerTotalDocConfig.setDecimalPlace(0);
		footerTotalDocConfig.setPaddingCharacter("0");
		footerTotalDocConfig.setDecimalPlace(0);
		footerTotalDocConfig.setPaddingType(PaddingType.LEFT);
		footerTotalDocConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerTotalDocConfig);

		DefaultFileLayoutConfigItem footerDocAmountFlagConfig = new DefaultFileLayoutConfigItem();
		footerDocAmountFlagConfig.setDisplayValue("Total Document Amount Flag");
		footerDocAmountFlagConfig.setStartIndex(23);
		footerDocAmountFlagConfig.setLength(1);
		footerDocAmountFlagConfig.setRecordType(RecordType.FOOTER);
		footerDocAmountFlagConfig.setPlusSymbol("1");
		footerDocAmountFlagConfig.setMinusSymbol("0");
		configItems.add(footerDocAmountFlagConfig);

		DefaultFileLayoutConfigItem footerDocAmountConfig = new DefaultFileLayoutConfigItem();
		footerDocAmountConfig.setDocFieldName("totalDocumentAmount");
		footerDocAmountConfig.setDisplayValue("Total Document Amount");
		footerDocAmountConfig.setStartIndex(8);
		footerDocAmountConfig.setLength(15);
		footerDocAmountConfig.setDecimalPlace(2);
		footerDocAmountConfig.setHasDecimalPlace(false);
		footerDocAmountConfig.setPaddingCharacter("0");
		footerDocAmountConfig.setPaddingType(PaddingType.LEFT);
		footerDocAmountConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerDocAmountConfig);

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setDocFieldName("filter");
		footerFilterConfig.setStartIndex(24);
		footerFilterConfig.setLength(277);
		footerFilterConfig.setRecordType(RecordType.FOOTER);

		configItems.add(footerFilterConfig);

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

	protected FileLayoutConfig createMakroFixedLengthFileLayout() {
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
		filterConfig.setExpectedValue(" ");
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
		footerDocAmountConfig.setValidationType(ValidationType.SUMMARY_OF_FIELD);
		footerDocAmountConfig.setValidationRecordFieldConfig(docAmountConfig);
		configItems.add(footerDocAmountConfig);

		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
		footerFilterConfig.setDocFieldName("filter");
		footerFilterConfig.setStartIndex(54);
		footerFilterConfig.setExpectedValue(" ");
		footerFilterConfig.setLength(247);
		footerFilterConfig.setRecordType(RecordType.FOOTER);
		configItems.add(footerFilterConfig);

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

	protected InputStream getFixedLengthFileContent() {
		String[] fixedLengthContent = new String[4];
		fixedLengthContent[0] = "H20160927120000Siam Makro Plc.               MAK  004                                                                                                                                                                                                                                                       ";
		fixedLengthContent[1] = "DMAK  232112              1122031             20160910201609010000000100000000                                                                                                                                                                                                                               ";
		fixedLengthContent[2] = "DMAK  232112              1122031             20160910201609010000000001000001                                                                                                                                                                                                                               ";
		fixedLengthContent[3] = "T0000020000000101000000                                                                                                                                                                                                                                                                                     ";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		return fixedlengthFileContent;
	}
}
