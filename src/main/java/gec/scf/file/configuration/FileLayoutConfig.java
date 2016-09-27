package gec.scf.file.configuration;

import java.util.List;

import gec.scf.file.domain.FileType;

public interface FileLayoutConfig {

	public List<? extends FileLayoutConfigItem> getConfigItems();

	public String getDetailFlag();

	public FileType getFileType();

}
