package gec.scf.file.configuration;

import java.util.List;

public interface FileLayoutConfig {

	public List<? extends FileLayoutConfigItem> getConfigItems();

	public String getHeaderFlag();

	public String getDetailFlag();

	public String getFooterFlag();

	public FileType getFileType();

	public String getDelimeter();

	public boolean isCheckBinaryFile();

	public String getCharsetName();

	public String getOwnerId();
}
