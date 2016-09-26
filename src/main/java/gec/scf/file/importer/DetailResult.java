package gec.scf.file.importer;

public class DetailResult {

	private boolean success;

	private Integer lineNo;

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getLineNo() {
		return lineNo;
	}

}
