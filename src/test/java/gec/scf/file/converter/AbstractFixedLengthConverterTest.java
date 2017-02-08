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

public class AbstractFixedLengthConverterTest {

	protected FileConverter<SponsorDocument> stubToAnswerValidation(
	        FileLayoutConfig fileLayoutConfig) {

		FileConverter<SponsorDocument> fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);
		FieldValidatorFactory fieldValidatorFactory = new FieldValidatorFactory();
		fixLengthFileConverter.setFieldValidatorFactory(fieldValidatorFactory);
		return fixLengthFileConverter;
	}

	protected FileConverter<SponsorDocument> spyToAnswerValidation(
	        FieldValidatorFactory fieldValidatorFactory,
	        FileLayoutConfig fileLayoutConfig) {

		FileConverter<SponsorDocument> fixLengthFileConverter = new FixedLengthFileConverter<SponsorDocument>(
		        fileLayoutConfig, SponsorDocument.class);
		fixLengthFileConverter.setFieldValidatorFactory(fieldValidatorFactory);
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
	
	protected FileLayoutConfig createFixedLengthFileLayout(DefaultFileLayoutConfigItem fileLayoutConfigItem) {
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

		if(fileLayoutConfigItem != null){
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

//	protected FileLayoutConfig createFixedLengthFileLayout(String displayValue,
//	        String dateFormat, ValidationType validationType) {
//		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
//		fileLayout.setFileType(FileType.FIXED_LENGTH);
//		fileLayout.setHeaderFlag("H");
//		fileLayout.setDetailFlag("D");
//		fileLayout.setFooterFlag("F");
//
//		List<FileLayoutConfigItem> configItems = new ArrayList<FileLayoutConfigItem>();
//		DefaultFileLayoutConfigItem headerRecordTypeConfig = new DefaultFileLayoutConfigItem();
//		headerRecordTypeConfig.setDocFieldName("recordId");
//		headerRecordTypeConfig.setStartIndex(1);
//		headerRecordTypeConfig.setLength(1);
//		headerRecordTypeConfig.setDisplayValue("Record Type");
//		headerRecordTypeConfig.setRecordType(RecordType.HEADER);
//		configItems.add(headerRecordTypeConfig);
//
//		DefaultFileLayoutConfigItem filterConfig = new DefaultFileLayoutConfigItem();
//		filterConfig.setDocFieldName("filter");
//		filterConfig.setStartIndex(92);
//		filterConfig.setLength(209);
//		filterConfig.setRecordType(RecordType.HEADER);
//		configItems.add(filterConfig);
//
//		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
//		detailRecordTypeConfig.setDocFieldName("recordId");
//		detailRecordTypeConfig.setDisplayValue("Record Type");
//		detailRecordTypeConfig.setStartIndex(1);
//		detailRecordTypeConfig.setLength(1);
//		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
//		configItems.add(detailRecordTypeConfig);
//
//		DefaultFileLayoutConfigItem detailFilterConfig = new DefaultFileLayoutConfigItem();
//		detailFilterConfig.setDocFieldName("filter");
//		detailFilterConfig.setStartIndex(126);
//		detailFilterConfig.setLength(175);
//		detailFilterConfig.setRecordType(RecordType.DETAIL);
//		configItems.add(detailFilterConfig);
//
//		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
//		footerRecordTypeConfig.setDocFieldName("recordId");
//		footerRecordTypeConfig.setDisplayValue("Record Type");
//		footerRecordTypeConfig.setStartIndex(1);
//		footerRecordTypeConfig.setLength(1);
//		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);
//		configItems.add(footerRecordTypeConfig);
//
////		DefaultFileLayoutConfigItem footerSendDateConfig = new DefaultFileLayoutConfigItem();
////		footerSendDateConfig.setLength(8);
////		footerSendDateConfig.setStartIndex(2);
////		footerSendDateConfig.setRecordType(RecordType.FOOTER);
////		footerSendDateConfig.setDatetimeFormat(dateFormat);
////		footerSendDateConfig.setTransient(true);
////		footerSendDateConfig.setDisplayValue(displayValue);
////		footerSendDateConfig.setValidationType(validationType);
////		configItems.add(footerSendDateConfig);
//
//		DefaultFileLayoutConfigItem footerFilterConfig = new DefaultFileLayoutConfigItem();
//		footerFilterConfig.setDocFieldName("filter");
//		footerFilterConfig.setStartIndex(92);
//		footerFilterConfig.setLength(209);
//		footerFilterConfig.setExpectedValue(" ");
//		footerFilterConfig.setRecordType(RecordType.FOOTER);
//		configItems.add(footerFilterConfig);
//
//		fileLayout.setConfigItems(configItems);
//		return fileLayout;
//	}

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
