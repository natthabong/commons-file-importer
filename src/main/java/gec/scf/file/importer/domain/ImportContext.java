package gec.scf.file.importer.domain;

import java.math.BigDecimal;
import java.util.Map;

import gec.scf.file.configuration.FileLayoutConfig;

public class ImportContext {

	private Channel channel;

	private String processNo;

	private String fileName;

	private Long fileLayoutConfigId;

	private String actor;

	private FileLayoutConfig fileLayoutConfig;

	private String ownerFundingId;

	private BigDecimal totalAmount;

	private Map<String, ImportContext> fundingImportContexts;

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
		if (fundingImportContexts != null && fundingImportContexts.size() > 0) {
			fundingImportContexts.entrySet().stream().forEach(fundingImportContext -> {
				fundingImportContext.getValue().setFileLayoutConfig(fileLayoutConfig);
			});
		}
	}

	public FileLayoutConfig getFileLayoutConfig() {
		return fileLayoutConfig;
	}

	public String getOwnerFundingId() {
		return ownerFundingId;
	}

	public void setOwnerFundingId(String ownerFundingId) {
		this.ownerFundingId = ownerFundingId;
	}

	public Map<String, ImportContext> getFundingImportContexts() {
		return fundingImportContexts;
	}

	public void setFundingImportContexts(
			Map<String, ImportContext> fundingImportContexts) {
		this.fundingImportContexts = fundingImportContexts;
	}

	public void increaseAmount(BigDecimal amount) {
		if (totalAmount == null) {
			totalAmount = new BigDecimal("0.0");
		}

		if (amount != null) {
			totalAmount = totalAmount.add(amount);
		}
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

}
