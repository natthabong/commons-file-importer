package gec.scf.file.converter;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import gec.scf.file.configuration.DefaultFileLayoutConfig;
import gec.scf.file.configuration.DefaultFileLayoutConfigItem;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;
import gec.scf.file.configuration.PaddingType;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.configuration.ValidationType;
import gec.scf.file.example.domain.DrawdownDocument;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.domain.Channel;
import gec.scf.file.importer.domain.ErrorLineDetail;

public class DrawdownAdviceFileConverterGetDetailTest extends AbstractFixedLengthConverterTest {
	
	@Test
	public void given_detail_valid_format_should_return_status_success() throws WrongFormatFileException{
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "HDR20171114DRAWDOWNADVICE      14             ";
		fixedLengthContent[1] = "DTLEDS123000000028BF14129349          2016112210221100025408            00031323            0000080000000000000020000001000000010003000B 2  Process successfully                                                                                1MOR       0000740000000000000+00000000000000000007.40000000                    ";
		fixedLengthContent[2] = "TLR0000001";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		
		FileLayoutConfig fileLayoutConfig = createDrawdownAdviceFixedLengthFileLayout();
		FixedLengthFileConverter<DrawdownDocument> fileConverter = new FixedLengthFileConverter<DrawdownDocument>(
				fileLayoutConfig, DrawdownDocument.class , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		
		// Actual
		DetailResult<DrawdownDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertTrue(actualResult.isSuccess());
		DrawdownDocument document = (DrawdownDocument) actualResult.getObjectValue();
		assertEquals("B", document.getReturnStatus());
	}
	
	@Test
	public void given_GEC_transaction_no_is_empty_should_return_status_fail() throws WrongFormatFileException{
		// Arrage
		String[] fixedLengthContent = new String[3];
		fixedLengthContent[0] = "HDR20171114DRAWDOWNADVICE      14             ";
		fixedLengthContent[1] = "DTL               BF14129349          2016112210221100025408            00031323            0000080000000000000020000001000000010003000B 2  Process successfully                                                                                1MOR       0000740000000000000+00000000000000000007.40000000                    ";
		fixedLengthContent[2] = "TLR0000001";
		InputStream fixedlengthFileContent = new ByteArrayInputStream(
				StringUtils.join(fixedLengthContent, System.lineSeparator()).getBytes());
		
		FileLayoutConfig fileLayoutConfig = createDrawdownAdviceFixedLengthFileLayout();
		FixedLengthFileConverter<DrawdownDocument> fileConverter = new FixedLengthFileConverter<DrawdownDocument>(
				fileLayoutConfig, DrawdownDocument.class , Channel.WEB);
		fileConverter.checkFileFormat(fixedlengthFileContent);
		
		// Actual
		DetailResult<DrawdownDocument> actualResult = fileConverter.getDetail();

		// Assert
		assertFalse(actualResult.isSuccess());
		ErrorLineDetail errorLineDetail = actualResult.getErrorLineDetails().get(0);
		assertEquals("GEC Transaction No is required", errorLineDetail.getErrorMessage());
	}

	protected FileLayoutConfig createDrawdownAdviceFixedLengthFileLayout() {
		return createDrawdownAdviceFixedLengthFileLayout(null);
	}

	protected FileLayoutConfig createDrawdownAdviceFixedLengthFileLayout(DefaultFileLayoutConfigItem otherConfig) {

		DefaultFileLayoutConfig fileLayout = new DefaultFileLayoutConfig();
		fileLayout.setFileType(FileType.FIXED_LENGTH);
		fileLayout.setHeaderFlag("HDR");
		fileLayout.setDetailFlag("DTL");
		fileLayout.setFooterFlag("TLR");

		List<FileLayoutConfigItem> configItems = new ArrayList<FileLayoutConfigItem>();

		DefaultFileLayoutConfigItem headerRecordId = new DefaultFileLayoutConfigItem();
		headerRecordId.setDocumentFieldName("recordId");
		headerRecordId.setStartIndex(1);
		headerRecordId.setLength(3);
		headerRecordId.setExpectedValue("HDR");
		headerRecordId.setDisplayValue("Record Id");
		headerRecordId.setRecordType(RecordType.HEADER);
		headerRecordId.setRequired(true);
		headerRecordId.setTransient(true);

		configItems.add(headerRecordId);
		
		DefaultFileLayoutConfigItem headerDocumentDate = new DefaultFileLayoutConfigItem();
		headerDocumentDate.setDocumentFieldName(null);
		headerDocumentDate.setStartIndex(4);
		headerDocumentDate.setLength(8);
		headerDocumentDate.setRecordType(RecordType.HEADER);
		headerDocumentDate.setRequired(true);
		headerDocumentDate.setTransient(true);
		headerDocumentDate.setCalendarEra("A.D.");
		headerDocumentDate.setDatetimeFormat("yyyyMMdd");
		
		configItems.add(headerDocumentDate);
		
		DefaultFileLayoutConfigItem headerRefId = new DefaultFileLayoutConfigItem();
		headerRefId.setDocumentFieldName(null);
		headerRefId.setStartIndex(12);
		headerRefId.setLength(20);
		headerRefId.setRecordType(RecordType.HEADER);
		headerRefId.setRequired(true);
		headerRefId.setTransient(true);
		headerRefId.setExpectedValue("DRAWDOWNADVICE");
		
		configItems.add(headerRefId);
		
		DefaultFileLayoutConfigItem headerDocumentNo = new DefaultFileLayoutConfigItem();
		headerDocumentNo.setDocumentFieldName(null);
		headerDocumentNo.setStartIndex(32);
		headerDocumentNo.setLength(15);
		headerDocumentNo.setRecordType(RecordType.HEADER);
		headerDocumentNo.setRequired(true);
		headerDocumentNo.setTransient(true);
		
		configItems.add(headerDocumentNo);

		DefaultFileLayoutConfigItem detailRecordTypeConfig = new DefaultFileLayoutConfigItem();
		detailRecordTypeConfig.setDocumentFieldName("recordId");
		detailRecordTypeConfig.setStartIndex(1);
		detailRecordTypeConfig.setLength(3);
		detailRecordTypeConfig.setExpectedValue("DTL");
		detailRecordTypeConfig.setDisplayValue("Record Id");
		detailRecordTypeConfig.setRecordType(RecordType.DETAIL);
		detailRecordTypeConfig.setTransient(true);

		configItems.add(detailRecordTypeConfig);

		if (otherConfig == null) {
			DefaultFileLayoutConfigItem transactionNo = new DefaultFileLayoutConfigItem();
			transactionNo.setDocumentFieldName("transactionNo");
			transactionNo.setStartIndex(4);
			transactionNo.setLength(15);
			transactionNo.setRequired(true);
			transactionNo.setExpectedValue(null);
			transactionNo.setRecordType(RecordType.DETAIL);
			transactionNo.setDisplayValue("GEC Transaction No");
			transactionNo.setTransient(false);
			transactionNo.setValidationType(ValidationType.FOUND_DRAWDOWN_TRANSACTION_NO);
			configItems.add(transactionNo);

			DefaultFileLayoutConfigItem bankTransactionNo = new DefaultFileLayoutConfigItem();
			bankTransactionNo.setDocumentFieldName("bankTransactionNo");
			bankTransactionNo.setStartIndex(19);
			bankTransactionNo.setLength(20);
			bankTransactionNo.setRequired(true);
			bankTransactionNo.setExpectedValue(null);
			bankTransactionNo.setDisplayValue("Loan Transaction No");
			bankTransactionNo.setRecordType(RecordType.DETAIL);
			bankTransactionNo.setTransient(false);
			bankTransactionNo.setValidationType(null);
			configItems.add(bankTransactionNo);

			DefaultFileLayoutConfigItem bankTransactionTime = new DefaultFileLayoutConfigItem();
			bankTransactionTime.setDocumentFieldName("bankTransactionTime");
			bankTransactionTime.setStartIndex(39);
			bankTransactionTime.setLength(14);
			bankTransactionTime.setRequired(true);
			bankTransactionTime.setExpectedValue(null);
			bankTransactionTime.setDisplayValue("Transaction TimeStamp");
			bankTransactionTime.setRecordType(RecordType.DETAIL);
			bankTransactionTime.setTransient(false);
			bankTransactionTime.setValidationType(null);
			bankTransactionTime.setCalendarEra("A.D.");
			bankTransactionTime.setDatetimeFormat("yyyyMMddHHmmss");
			configItems.add(bankTransactionTime);

			DefaultFileLayoutConfigItem sponsorRef = new DefaultFileLayoutConfigItem();
			sponsorRef.setDocumentFieldName("buyerId");
			sponsorRef.setStartIndex(53);
			sponsorRef.setLength(20);
			sponsorRef.setRequired(true);
			sponsorRef.setExpectedValue(null);
			sponsorRef.setDisplayValue("Sponsor Ref");
			sponsorRef.setRecordType(RecordType.DETAIL);
			sponsorRef.setTransient(false);
			sponsorRef.setValidationType(ValidationType.FOUND_ORGANIZE);
			configItems.add(sponsorRef);

			DefaultFileLayoutConfigItem buyerSellerRef = new DefaultFileLayoutConfigItem();
			buyerSellerRef.setDocumentFieldName("supplierId");
			buyerSellerRef.setStartIndex(73);
			buyerSellerRef.setLength(20);
			buyerSellerRef.setRequired(true);
			buyerSellerRef.setExpectedValue(null);
			buyerSellerRef.setDisplayValue("Buyer/Seller Ref");
			buyerSellerRef.setRecordType(RecordType.DETAIL);
			buyerSellerRef.setTransient(false);
			buyerSellerRef.setValidationType(ValidationType.FOUND_ORGANIZE);
			configItems.add(buyerSellerRef);

			DefaultFileLayoutConfigItem drawdownAmount = new DefaultFileLayoutConfigItem();
			drawdownAmount.setDocumentFieldName("drawdownAmount");
			drawdownAmount.setStartIndex(93);
			drawdownAmount.setLength(12);
			drawdownAmount.setRequired(true);
			drawdownAmount.setExpectedValue(null);
			drawdownAmount.setDisplayValue("Drawdown Amount");
			drawdownAmount.setPaddingCharacter("");
			drawdownAmount.setPaddingType(null);
			drawdownAmount.setDecimalPlace(2);
			drawdownAmount.setHasDecimalPlace(false);
			drawdownAmount.setRecordType(RecordType.DETAIL);
			drawdownAmount.setTransient(false);
			configItems.add(drawdownAmount);

			DefaultFileLayoutConfigItem interestAmount = new DefaultFileLayoutConfigItem();
			interestAmount.setDocumentFieldName("interestAmount");
			interestAmount.setStartIndex(105);
			interestAmount.setLength(12);
			interestAmount.setRequired(true);
			interestAmount.setExpectedValue(null);
			interestAmount.setDisplayValue("Interest Amount");
			interestAmount.setPaddingCharacter("");
			interestAmount.setPaddingType(null);
			interestAmount.setDecimalPlace(2);
			interestAmount.setHasDecimalPlace(false);
			interestAmount.setRecordType(RecordType.DETAIL);
			interestAmount.setTransient(false);
			configItems.add(interestAmount);
			
			DefaultFileLayoutConfigItem repaymentFee = new DefaultFileLayoutConfigItem();
			repaymentFee.setDocumentFieldName("repaymentFee");
			repaymentFee.setStartIndex(117);
			repaymentFee.setLength(7);
			repaymentFee.setRequired(true);
			repaymentFee.setExpectedValue(null);
			repaymentFee.setDisplayValue("Repayment Fee");
			repaymentFee.setPaddingCharacter("");
			repaymentFee.setPaddingType(null);
			repaymentFee.setDecimalPlace(2);
			repaymentFee.setHasDecimalPlace(false);
			repaymentFee.setRecordType(RecordType.DETAIL);
			repaymentFee.setTransient(false);
			configItems.add(repaymentFee);
			
			DefaultFileLayoutConfigItem repaymentAmount = new DefaultFileLayoutConfigItem();
			repaymentAmount.setDocumentFieldName("repaymentAmount");
			repaymentAmount.setStartIndex(124);
			repaymentAmount.setLength(12);
			repaymentAmount.setRequired(true);
			repaymentAmount.setExpectedValue(null);
			repaymentAmount.setDisplayValue("Repayment Amount");
			repaymentAmount.setPaddingCharacter("");
			repaymentAmount.setPaddingType(null);
			repaymentAmount.setDecimalPlace(2);
			repaymentAmount.setHasDecimalPlace(false);
			repaymentAmount.setRecordType(RecordType.DETAIL);
			repaymentAmount.setTransient(false);
			configItems.add(repaymentAmount);
			

			DefaultFileLayoutConfigItem returnStatus = new DefaultFileLayoutConfigItem();
			returnStatus.setDocumentFieldName("returnStatus");
			returnStatus.setStartIndex(136);
			returnStatus.setLength(2);
			returnStatus.setRequired(true);
			returnStatus.setExpectedValue(null);
			returnStatus.setDisplayValue("return Status");
			returnStatus.setRecordType(RecordType.DETAIL);
			returnStatus.setTransient(false);
			returnStatus.setValidationType(ValidationType.DRAWDOWN_ADVICE_RETURN_STATUS);
			configItems.add(returnStatus);
			
			DefaultFileLayoutConfigItem returnCode = new DefaultFileLayoutConfigItem();
			returnCode.setDocumentFieldName("returnCode");
			returnCode.setStartIndex(138);
			returnCode.setLength(3);
			returnCode.setRequired(false);
			returnCode.setExpectedValue(null);
			returnCode.setDisplayValue("Return code");
			returnCode.setRecordType(RecordType.DETAIL);
			returnCode.setTransient(false);
			returnCode.setValidationType(null);
			configItems.add(returnCode);
			
			DefaultFileLayoutConfigItem returnMessage = new DefaultFileLayoutConfigItem();
			returnMessage.setDocumentFieldName("returnMessage");
			returnMessage.setStartIndex(141);
			returnMessage.setLength(100);
			returnMessage.setRequired(false);
			returnMessage.setExpectedValue(null);
			returnMessage.setDisplayValue("Return message");
			returnMessage.setRecordType(RecordType.DETAIL);
			returnMessage.setTransient(false);
			returnMessage.setValidationType(null);
			configItems.add(returnMessage);

			DefaultFileLayoutConfigItem interestFlag = new DefaultFileLayoutConfigItem();
			interestFlag.setDocumentFieldName(null);
			interestFlag.setStartIndex(241);
			interestFlag.setLength(1);
			interestFlag.setRequired(true);
			interestFlag.setExpectedValue(null);
			interestFlag.setDisplayValue("Interest Flag");
			interestFlag.setRecordType(RecordType.DETAIL);
			interestFlag.setTransient(true);
			interestFlag.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_FLAG);
			configItems.add(interestFlag);
			
			DefaultFileLayoutConfigItem interestCode = new DefaultFileLayoutConfigItem();
			interestCode.setDocumentFieldName(null);
			interestCode.setStartIndex(242);
			interestCode.setLength(10);
			interestCode.setRequired(false);
			interestCode.setExpectedValue(null);
			interestCode.setDisplayValue("Interest Code");
			interestCode.setRecordType(RecordType.DETAIL);
			interestCode.setTransient(true);
			configItems.add(interestCode);

			DefaultFileLayoutConfigItem interestBasis = new DefaultFileLayoutConfigItem();
			interestBasis.setDocumentFieldName(null);
			interestBasis.setStartIndex(252);
			interestBasis.setLength(19);
			interestBasis.setRequired(true);
			interestBasis.setExpectedValue(null);
			interestBasis.setDisplayValue("Interest Basis");
			interestBasis.setPaddingCharacter("");
			interestBasis.setPaddingType(null);
			interestBasis.setDecimalPlace(8);
			interestBasis.setHasDecimalPlace(false);
			interestBasis.setRecordType(RecordType.DETAIL);
			interestBasis.setTransient(true);
			interestBasis.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_BASIS);
			configItems.add(interestBasis);

			DefaultFileLayoutConfigItem interestSpread = new DefaultFileLayoutConfigItem();
			interestSpread.setDocumentFieldName(null);
			interestSpread.setStartIndex(252);
			interestSpread.setLength(19);
			interestSpread.setRequired(true);
			interestSpread.setExpectedValue(null);
			interestSpread.setDisplayValue("Interest Spread");
			interestSpread.setPaddingCharacter("");
			interestSpread.setPaddingType(null);
			interestSpread.setDecimalPlace(8);
			interestSpread.setHasDecimalPlace(false);
			interestSpread.setRecordType(RecordType.DETAIL);
			interestSpread.setTransient(true);
			interestSpread.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_SPREAD);
			configItems.add(interestSpread);
			
			DefaultFileLayoutConfigItem allInterestRate = new DefaultFileLayoutConfigItem();
			allInterestRate.setDocumentFieldName(null);
			allInterestRate.setStartIndex(291);
			allInterestRate.setLength(30);
			allInterestRate.setRequired(true);
			allInterestRate.setExpectedValue(null);
			allInterestRate.setDisplayValue("All Interest Rate");
			allInterestRate.setPaddingCharacter("");
			allInterestRate.setPaddingType(null);
			allInterestRate.setDecimalPlace(8);
			allInterestRate.setHasDecimalPlace(false);
			allInterestRate.setRecordType(RecordType.DETAIL);
			allInterestRate.setTransient(true);
			allInterestRate.setValidationType(ValidationType.DRAWDOWN_ADVICE_INTEREST_SPREAD);
			configItems.add(allInterestRate);

		} else {
			configItems.add(otherConfig);
		}

		DefaultFileLayoutConfigItem footerRecordTypeConfig = new DefaultFileLayoutConfigItem();
		footerRecordTypeConfig.setDocFieldName("recordId");
		footerRecordTypeConfig.setExpectedValue("TLR");
		footerRecordTypeConfig.setDisplayValue("Record Id");
		footerRecordTypeConfig.setStartIndex(1);
		footerRecordTypeConfig.setLength(3);
		footerRecordTypeConfig.setRecordType(RecordType.FOOTER);
		configItems.add(footerRecordTypeConfig);

		DefaultFileLayoutConfigItem footerTotalDocConfig = new DefaultFileLayoutConfigItem();
		footerTotalDocConfig.setDocFieldName(null);
		footerTotalDocConfig.setDisplayValue("Total line");
		footerTotalDocConfig.setStartIndex(4);
		footerTotalDocConfig.setLength(7);
		footerTotalDocConfig.setRecordType(RecordType.FOOTER);
		footerTotalDocConfig.setPaddingCharacter("0");
		footerTotalDocConfig.setPaddingType(PaddingType.LEFT);
		footerTotalDocConfig.setValidationType(ValidationType.COUNT_OF_DOCUMENT_DETAIL);
		configItems.add(footerTotalDocConfig);

		fileLayout.setConfigItems(configItems);

		return fileLayout;
	}

}
