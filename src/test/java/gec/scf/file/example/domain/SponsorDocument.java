package gec.scf.file.example.domain;

import java.math.BigDecimal;
import java.util.Date;

import gec.scf.file.converter.DocumentStatus;

public class SponsorDocument {
	private String documentId;

	private String documentNo;

	private String documentType;

	private String supplierCode;

	private String sponsorId;

	private String supplierId;

	private Date sponsorPaymentDate;

	private BigDecimal documentAmount;

	private DocumentStatus documentStatus;

	private BigDecimal outstandingAmount;

	private Date documentDate;

	private String optionVarcharField1;

	private String optionVarcharField2;

	private String optionVarcharField3;

	private Date optionDateField1;
	
	private BigDecimal optionNumbericField1;
	
	private String optionVarcharField4;

	private String termCode;
	
	private Date createdTime;

	private Date lastModifiedTime;

	private String createdBy;
	private String lastModifiedBy;

	private Integer version;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSponsorId() {
		return sponsorId;
	}

	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public Date getSponsorPaymentDate() {
		return sponsorPaymentDate;
	}

	public void setSponsorPaymentDate(Date sponsorPaymentDate) {
		this.sponsorPaymentDate = sponsorPaymentDate;
	}

	public BigDecimal getDocumentAmount() {
		return documentAmount;
	}

	public void setDocumentAmount(BigDecimal documentAmount) {
		this.documentAmount = documentAmount;
	}

	public BigDecimal getOutstandingAmount() {
		return outstandingAmount;
	}

	public void setOutstandingAmount(BigDecimal outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}

	public Date getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}

	public String getOptionVarcharField1() {
		return optionVarcharField1;
	}

	public void setOptionVarcharField1(String optionVarcharField1) {
		this.optionVarcharField1 = optionVarcharField1;
	}

	public void setOptionVarcharField2(String optionVarcharField2) {
		this.optionVarcharField2 = optionVarcharField2;
	}

	public String getOptionVarcharField2() {
		return optionVarcharField2;
	}

	public String getOptionVarcharField3() {
		return optionVarcharField3;
	}

	public void setOptionVarcharField3(String optionVarcharField3) {
		this.optionVarcharField3 = optionVarcharField3;
	}

	public Date getOptionDateField1() {
		return optionDateField1;
	}

	public void setOptionDateField1(Date optionDateField1) {
		this.optionDateField1 = optionDateField1;
	}

	public BigDecimal getOptionNumbericField1() {
		return optionNumbericField1;
	}

	public void setOptionNumbericField1(BigDecimal optionNumbericField1) {
		this.optionNumbericField1 = optionNumbericField1;
	}

	public String getOptionVarcharField4() {
		return optionVarcharField4;
	}

	public void setOptionVarcharField4(String optionVarcharField4) {
		this.optionVarcharField4 = optionVarcharField4;
	}

	public void setTermCode(String termCode) {
		this.termCode = termCode;
	}

	public String getTermCode() {
		return termCode == null ? "": termCode;
	}

	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}
	
}
