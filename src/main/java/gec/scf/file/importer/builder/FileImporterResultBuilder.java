package gec.scf.file.importer.builder;

import java.math.BigDecimal;
import java.util.Date;

import gec.scf.file.importer.domain.Channel;
import gec.scf.file.importer.domain.FileImporterResult;
import gec.scf.file.importer.domain.ImportFileInformation;
import gec.scf.file.importer.domain.ProcessType;

public class FileImporterResultBuilder {

	private ImportFileInformation information;

	private Channel channel;

	private String processNo;

	private String fileName;

	private Integer success;

	private Integer fail;

	private Integer total;

	private BigDecimal totalAmount;

	private Date startTime;

	private Date endTime;

	private ProcessType processType;

	private String ownerOrganizeId;

	private String ownerFundingId;

	public FileImporterResultBuilder information(ImportFileInformation information) {
		this.information = information;
		return this;
	}

	public FileImporterResultBuilder channel(Channel channel) {
		this.channel = channel;
		return this;
	}

	public FileImporterResultBuilder processNo(String processNo) {
		this.processNo = processNo;
		return this;
	}

	public FileImporterResultBuilder fileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public FileImporterResultBuilder success(Integer success) {
		this.success = success;
		return this;
	}

	public FileImporterResultBuilder fail(Integer fail) {
		this.fail = fail;
		return this;
	}

	public FileImporterResultBuilder total(Integer total) {
		this.total = total;
		return this;
	}

	public FileImporterResultBuilder totalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}

	public FileImporterResultBuilder startTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}

	public FileImporterResultBuilder endTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}

	public FileImporterResultBuilder processType(ProcessType processType) {
		this.processType = processType;
		return this;
	}

	public FileImporterResultBuilder ownerOrganizeId(String ownerOrganizeId) {
		this.ownerOrganizeId = ownerOrganizeId;
		return this;
	}

	public FileImporterResultBuilder ownerFundingId(String ownerFundingId) {
		this.ownerFundingId = ownerFundingId;
		return this;
	}

	public FileImporterResult build() {
		FileImporterResult fileImporterResult = new FileImporterResult(processNo, fileName, channel);
		fileImporterResult.setInformation(information);
		fileImporterResult.setSuccess(success);
		fileImporterResult.setFail(fail);
		fileImporterResult.setTotal(total);
		fileImporterResult.setTotalAmount(totalAmount);
		fileImporterResult.setStartTime(startTime);
		fileImporterResult.setEndTime(endTime);
		fileImporterResult.setProcessType(processType);
		fileImporterResult.setOwnerOrganizeId(ownerOrganizeId);
		fileImporterResult.setOwnerFundingId(ownerFundingId);
		return fileImporterResult;
	}
}
