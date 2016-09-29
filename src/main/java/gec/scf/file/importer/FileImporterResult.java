package gec.scf.file.importer;

import java.util.List;

public class FileImporterResult {

	private ResultType status;

	private int totalSuccess;

	private Integer totalFailed;

	private List<ErrorLineDetail> errorLineDetails;
	
	private String processNo;

	public ResultType getStatus() {
		return status;
	}

	public void setStatus(ResultType status) {
		this.status = status;
	}

	public int getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(int totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public Integer getTotalFailed() {
		return totalFailed;
	}

	public void setTotalFailed(Integer totalFailed) {
		this.totalFailed = totalFailed;
	}

	public List<ErrorLineDetail> getErrorLineDetails() {
		return errorLineDetails;
	}

	public void setErrorLineDetails(List<ErrorLineDetail> errorLineDetails) {
		this.errorLineDetails = errorLineDetails;
	}

	public String getProcessNo() {
		return processNo;
	}

	public void setProcessNo(String processNo) {
		this.processNo = processNo;
	}

}
