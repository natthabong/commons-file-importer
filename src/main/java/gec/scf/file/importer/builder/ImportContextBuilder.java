package gec.scf.file.importer.builder;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.importer.domain.Channel;
import gec.scf.file.importer.domain.ImportContext;

public class ImportContextBuilder {

	private Channel channel;

	private String processNo;

	private String fileName;

	private Long fileLayoutConfigId;

	private FileLayoutConfig fileLayoutConfig;

	private String actor;

	private String ownerFundingId;

	public ImportContextBuilder channel(Channel channel) {
		this.channel = channel;
		return this;
	}

	public ImportContextBuilder processNo(String processNo) {
		this.processNo = processNo;
		return this;
	}

	public ImportContextBuilder fileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public ImportContextBuilder ownerFundingId(String ownerFundingId) {
		this.ownerFundingId = ownerFundingId;
		return this;
	}

	public ImportContextBuilder actor(String actor) {
		this.actor = actor;
		return this;
	}

	public ImportContextBuilder fileLayoutConfigId(Long fileLayoutConfigId) {
		this.fileLayoutConfigId = fileLayoutConfigId;
		return this;
	}

	public ImportContextBuilder fileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
		return this;
	}

	public ImportContext build() {
		ImportContext importContext = new ImportContext();
		importContext.setChannel(channel);
		importContext.setProcessNo(processNo);
		importContext.setFileName(fileName);
		importContext.setOwnerFundingId(ownerFundingId);
		importContext.setActor(actor);
		importContext.setFileLayoutConfigId(fileLayoutConfigId);
		importContext.setFileLayoutConfig(fileLayoutConfig);

		return importContext;
	}

}
