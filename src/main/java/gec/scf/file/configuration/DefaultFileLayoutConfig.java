package gec.scf.file.configuration;

import java.util.List;

public class DefaultFileLayoutConfig implements FileLayoutConfig {

	private Integer startIndex;
	
	private FileType fileType;

	private String detailFlag;

	private List<FileLayoutConfigItem> configItems;

	private String headerFlag;

	private String footerFlag;

	private String delimeter;

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
		// TODO Auto-generated method stub

	}

	public void setPrefixName(String string) {
		// TODO Auto-generated method stub

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

}