package gec.scf.file.converter;

import java.util.List;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.FileType;

public class SponsorFileLayoutConfig implements FileLayoutConfig {

	private List<? extends FileLayoutConfigItem> layoutItems;


	@Override
	public List<? extends FileLayoutConfigItem> getConfigItems() {
		return layoutItems;
	}

	@Override
	public String getDetailFlag() {
		return null;
	}

	@Override
	public FileType getFileType() {
		return null;
	}

	
	public void setConfigItems(List<? extends FileLayoutConfigItem> layoutItems) {
		this.layoutItems = layoutItems;
	}

}
