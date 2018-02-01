package gec.scf.file.common.domain;

import java.io.Serializable;

public class FileInformation implements Comparable<FileInformation>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileType;

	private String fileName;

	private Long fileSize;

	private String reference;

	private String filePath;

	public FileInformation() {

	}

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

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;

	}

	public String getFilePath() {
		return filePath;
	}

	@Override
	public int compareTo(FileInformation o) {
		return this.getFileName().compareToIgnoreCase(o.getFileName());
	}

}
