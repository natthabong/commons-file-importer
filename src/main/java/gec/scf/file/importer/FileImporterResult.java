package gec.scf.file.importer;

import java.util.List;

public class FileImporterResult {

	private ResultType status;
	
	private List<ErrorLineDetail> errorLineDetails;

	public ResultType getStatus() {
		return status;
	}

	public void setStatus(ResultType status) {
		this.status = status;
	}

	public Integer getTotalFail() {
		return null;
	}

	public int getTotalSuccess() {
		return 0;
	}

	public List<ErrorLineDetail> getErrorLineDetails() {
		return errorLineDetails;
	}

	public void setErrorLineDetails(List<ErrorLineDetail> errorLineDetails) {
		this.errorLineDetails = errorLineDetails;
	}
}
