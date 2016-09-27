package gec.scf.file.importer;

import java.util.List;

public class DetailResult {

	private boolean success;

	private Integer lineNo;

	private List<ErrorLineDetail> errorLineDetails;

	private Object objectValue;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getLineNo() {
		return lineNo;
	}

	public void setErrorLineDetails(List<ErrorLineDetail> errorLineDetails) {
		this.errorLineDetails = errorLineDetails;
	}

	public List<ErrorLineDetail> getErrorLineDetails() {
		return errorLineDetails;
	}

	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	public Object getObjectValue() {
		return objectValue;
	}

	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}

}