package gec.scf.file.importer.domain;

import java.io.Serializable;

import gec.scf.file.common.domain.FileInformation;

public class FileExportResult implements Serializable {

	private static final long serialVersionUID = 8806352501578003668L;

	private String basePath;

	private FileInformation[] files;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public FileInformation[] getFiles() {
		return files;
	}

	public void setFiles(FileInformation[] files) {
		this.files = files;
	}

}
