package gec.scf.file.example.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DrawdownDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String processNo;

	private String bankTransactionNo;

	private Date bankTransactionTime;

	private String supplierId;

	private String buyerId;

	private String payerBankAccount;

	private String transactionNo;

	private Date drawdownDate;

	private Date maturityDate;

	private String returnStatus;

	private String returnCode;

	private String returnMessage;

	private BigDecimal interestAmount;

	private BigDecimal drawdownAmount;

	private BigDecimal paymentAmount;

	private BigDecimal debitAmount;

	private BigDecimal debitFee;

	private BigDecimal repaymentFee;

	private BigDecimal repaymentAmount;

	private String transactionStatusCode;

	private boolean transactionRetriable;

	public String getProcessNo() {
		return processNo;
	}

	public void setProcessNo(String processNo) {
		this.processNo = processNo;
	}

	public String getBankTransactionNo() {
		return bankTransactionNo;
	}

	public void setBankTransactionNo(String bankTransactionNo) {
		this.bankTransactionNo = bankTransactionNo;
	}

	public Date getBankTransactionTime() {
		return bankTransactionTime;
	}

	public void setBankTransactionTime(Date bankTransactionTime) {
		this.bankTransactionTime = bankTransactionTime;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getPayerBankAccount() {
		return payerBankAccount;
	}

	public void setPayerBankAccount(String payerBankAccount) {
		this.payerBankAccount = payerBankAccount;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public Date getDrawdownDate() {
		return drawdownDate;
	}

	public void setDrawdownDate(Date drawdownDate) {
		this.drawdownDate = drawdownDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}

	public BigDecimal getDrawdownAmount() {
		return drawdownAmount;
	}

	public void setDrawdownAmount(BigDecimal drawdownAmount) {
		this.drawdownAmount = drawdownAmount;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public BigDecimal getDebitFee() {
		return debitFee;
	}

	public void setDebitFee(BigDecimal debitFee) {
		this.debitFee = debitFee;
	}

	public BigDecimal getRepaymentFee() {
		return repaymentFee;
	}

	public void setRepaymentFee(BigDecimal repaymentFee) {
		this.repaymentFee = repaymentFee;
	}

	public BigDecimal getRepaymentAmount() {
		return repaymentAmount;
	}

	public void setRepaymentAmount(BigDecimal repaymentAmount) {
		this.repaymentAmount = repaymentAmount;
	}

	public String getTransactionStatusCode() {
		return transactionStatusCode;
	}

	public void setTransactionStatusCode(String transactionStatusCode) {
		this.transactionStatusCode = transactionStatusCode;
	}

	public boolean isTransactionRetriable() {
		return transactionRetriable;
	}

	public void setTransactionRetriable(boolean transactionRetriable) {
		this.transactionRetriable = transactionRetriable;
	}

}
