package gec.scf.file.configuration;

import java.util.List;

public interface FileLayoutConfig {

	public List<? extends FileLayoutConfigItem> getConfigItems();

	public String getDetailFlag();

	public String getFileType();

}
