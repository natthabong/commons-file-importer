package gec.scf.file.importer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileImporterResult implements Serializable {

	private static final long serialVersionUID = 8806352501578003668L;

	private ImportFileInformation information;

	private Integer success;

	private Integer fail;

	private Integer total;

	private List<ErrorLineDetail> errors;

	private BigDecimal totalAmount;

	private Date startTime;

	private Date endTime;

	private ProcessType processType;

	private String ownerOrganizeId;

	private String ownerFundingId;

	private Map<String, FileImporterResult> fileImporterResults;

	public FileImporterResult() {
		success = fail = total = 0;
	}

	public FileImporterResult(String processNo, String sourceFileName, Channel channel) {
		this();
		information = new ImportFileInformation(processNo, sourceFileName, channel);
	}

	public FileImporterResult(String processNo, Channel channel) {
		this(processNo, null, channel);
	}

	public FileImporterResult(ImportContext context) {
		this(context.getProcessNo(), context.getFileName(), context.getChannel());
	}

	public void increaseSuccess() {
		success = Optional.ofNullable(success).orElse(0);
		success++;
		updateTotal();
	}

	public void increaseFail() {
		fail = Optional.ofNullable(fail).orElse(0);
		fail++;
		updateTotal();
	}

	public void decreaseSuccess() {
		success--;
		updateTotal();
	}

	public void decreaseFail() {
		fail--;
		updateTotal();
	}

	public boolean isComplete() {
		if (total != null) {
			return success.compareTo(total) == 0;
		}
		else {
			return false;
		}
	}

	private void updateTotal() {
		total = Optional.ofNullable(total).orElse(0);
		success = Optional.ofNullable(success).orElse(0);
		fail = Optional.ofNullable(fail).orElse(0);
		total = success + fail;
	}

	public Integer getSuccess() {
		return success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}

	public Integer getFail() {
		return fail;
	}

	public void setFail(Integer fail) {
		this.fail = fail;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public void setErrors(List<ErrorLineDetail> errors) {
		this.errors = errors;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void addError(ErrorLineDetail error) {
		if (errors == null) {
			errors = new ArrayList<ErrorLineDetail>();
		}
		errors.add(error);

	}

	public void addErrors(List<ErrorLineDetail> errors) {
		if (this.errors == null) {
			this.errors = new ArrayList<ErrorLineDetail>();
		}
		this.errors.addAll(errors);
	}

	public List<ErrorLineDetail> getErrors() {
		return errors;
	}

	public void increaseAmount(BigDecimal amount) {
		if (totalAmount == null) {
			totalAmount = new BigDecimal("0.0");
		}

		if (amount != null) {
			totalAmount = totalAmount.add(amount);
		}
	}

	public void decreaseAmount(BigDecimal amount) {
		if (totalAmount == null) {
			totalAmount = new BigDecimal("0.0");
		}

		if (amount != null) {
			totalAmount = totalAmount.subtract(amount);
		}
	}

	public ImportFileInformation getInformation() {
		return information;
	}

	public void setInformation(ImportFileInformation information) {
		this.information = information;
	}

	public void reject() {
		success = 0;
		fail = null;
		total = 0;

	}

	public void rejectFile() {
		success = 0;
		totalAmount = new BigDecimal("0.0");
		fail = total = null;

	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;

	}

	public Date getStartTime() {
		return startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
		if (fileImporterResults != null && fileImporterResults.size() > 0) {
			fileImporterResults.forEach(
					(key, fileImporterResult) -> fileImporterResult.setEndTime(endTime));
		}

	}

	public Date getEndTime() {
		return endTime;
	}

	public long getDurationTime() {
		if (startTime != null && endTime != null) {
			return endTime.getTime() - startTime.getTime();
		}
		return 0L;
	}

	public void setProcessType(ProcessType processType) {
		this.processType = processType;
	}

	public ProcessType getProcessType() {
		return processType;
	}

	public String getOwnerOrganizeId() {
		return ownerOrganizeId;
	}

	public void setOwnerOrganizeId(String ownerOrganizeId) {
		this.ownerOrganizeId = ownerOrganizeId;
	}

	public String getOwnerFundingId() {
		return ownerFundingId;
	}

	public void setOwnerFundingId(String ownerFundingId) {
		this.ownerFundingId = ownerFundingId;
	}

	public Map<String, FileImporterResult> getFileImporterResults() {
		return fileImporterResults;
	}

	public void setFileImporterResults(
			Map<String, FileImporterResult> fileImporterResults) {
		this.fileImporterResults = fileImporterResults;
	}

}