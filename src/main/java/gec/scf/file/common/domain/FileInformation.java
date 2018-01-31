package gec.scf.file.common.domain;

import java.io.Serializable;

public class FileInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileType;

	private String fileName;

	private String reference;

	public FileInformation(String fileType, String fileName, String reference) {
		this.fileType = fileType;
		this.fileName = fileName;
		this.reference = reference;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

}
