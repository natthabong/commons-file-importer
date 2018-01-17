package gec.scf.file.importer.domain;

import gec.scf.file.configuration.FileLayoutConfig;

public class ImportContext {

	private Channel channel;

	private String processNo;

	private String fileName;

	private Long fileLayoutConfigId;

	private String actor;

	private FileLayoutConfig fileLayoutConfig;

	public String getProcessNo() {
		return processNo;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getFileName() {
		return fileName;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setProcessNo(String processNo) {
		this.processNo = processNo;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileLayoutConfigId() {
		return fileLayoutConfigId;
	}

	public void setActor(String actor) {
		this.actor = actor;

	}

	public String getActor() {
		return actor;
	}

	public void setFileLayoutConfigId(Long fileLayoutConfigId) {
		this.fileLayoutConfigId = fileLayoutConfigId;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;

	}

	public FileLayoutConfig getFileLayoutConfig() {
		return fileLayoutConfig;
	}

}
