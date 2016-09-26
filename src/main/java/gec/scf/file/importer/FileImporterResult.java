package gec.scf.file.importer;

import java.util.List;

public class FileImporterResult {

	private ResultType status;

	private List<ErrorLineDetail> errorLineDetails;

	private int totalSuccess;

	private Integer totalFail;

	public ResultType getStatus() {
		return status;
	}

	public void setStatus(ResultType status) {
		this.status = status;
	}

	public Integer getTotalFail() {
		return totalFail;
	}

	public int getTotalSuccess() {
		return totalSuccess;
	}

	public List<ErrorLineDetail> getErrorLineDetails() {
		return errorLineDetails;
	}

	public void setErrorLineDetails(List<ErrorLineDetail> errorLineDetails) {
		this.errorLineDetails = errorLineDetails;
	}

	public void setTotalSuccess(int totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public void setTotalFail(Integer totalFail) {
		this.totalFail = totalFail;
	}
}
