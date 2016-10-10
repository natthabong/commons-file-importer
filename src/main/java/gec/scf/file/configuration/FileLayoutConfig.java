package gec.scf.file.configuration;

import java.util.List;

import gec.scf.file.converter.DataReference;

public interface FileLayoutConfig {

	public List<? extends FileLayoutConfigItem> getConfigItems();

	public String getHeaderFlag();

	public String getDetailFlag();

	public String getFooterFlag();

	public FileType getFileType();

	public String getDelimeter();
	
	public boolean isRequiredFindAndMergeOption();

	public List<? extends DataReference> getDataReferences();

}
