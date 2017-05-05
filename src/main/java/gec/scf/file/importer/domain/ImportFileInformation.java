package gec.scf.file.importer.domain;

import java.io.Serializable;

public class ImportFileInformation implements Serializable {

	private static final long serialVersionUID = 632480040501666187L;

	private String processNo;

	private String sourceFileName;

	private Channel channel;

	private String backupFilePath;

	public ImportFileInformation() {
	}

	public ImportFileInformation(String processNo, String sourceFileName,
			Channel channel) {
		super();
		this.processNo = processNo;
		this.sourceFileName = sourceFileName;
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getProcessNo() {
		return processNo;
	}

	public void setProcessNo(String processNo) {
		this.processNo = processNo;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public void setBackupFilePath(String backupFilePath) {
		this.backupFilePath = backupFilePath;

	}

	public String getBackupFilePath() {
		return backupFilePath;
	}

}
