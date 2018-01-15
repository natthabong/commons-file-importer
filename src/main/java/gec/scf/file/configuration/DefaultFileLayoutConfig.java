package gec.scf.file.configuration;

import java.util.List;

import gec.scf.file.importer.domain.ProcessType;

public class DefaultFileLayoutConfig implements FileLayoutConfig {

	private Integer startIndex;

	private FileType fileType;

	private String detailFlag;

	private List<FileLayoutConfigItem> configItems;

	private String headerFlag;

	private String footerFlag;

	private String delimeter;

	private boolean checkBinaryFile;

	private String charsetName;

	private long offsetRowNo;
	
	private ProcessType processType;
	
	public ProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(ProcessType processType) {
		this.processType = processType;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public List<? extends FileLayoutConfigItem> getConfigItems() {
		return configItems;
	}

	@Override
	public String getDetailFlag() {
		return detailFlag;
	}

	@Override
	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fixedLength) {
		this.fileType = fixedLength;

	}

	public void setFooterFlag(String footerFlag) {
		this.footerFlag = footerFlag;

	}

	public void setDetailFlag(String detailFlag) {
		this.detailFlag = detailFlag;

	}

	public void setHeaderFlag(String headerFlag) {
		this.headerFlag = headerFlag;

	}

	public void setFileExtension(String string) {

	}

	public void setPrefixName(String string) {

	}

	public void setConfigItems(List<FileLayoutConfigItem> configItems) {
		this.configItems = configItems;

	}

	@Override
	public String getHeaderFlag() {
		return headerFlag;
	}

	@Override
	public String getFooterFlag() {
		return footerFlag;
	}

	@Override
	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public void setCheckBinaryFile(boolean checkBinaryFile) {
		this.checkBinaryFile = checkBinaryFile;
	}

	@Override
	public boolean isCheckBinaryFile() {
		return checkBinaryFile;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;		
	}

	@Override
	public String getCharsetName() {
		return charsetName;
	}

	@Override
	public String getOwnerId() {
		return null;
	}

	public void setOffsetRowNo(long offsetRowNo) {
		this.offsetRowNo = offsetRowNo;		
	}

	@Override
	public Long getOffsetRowNo() {
		return offsetRowNo;
	}
}
