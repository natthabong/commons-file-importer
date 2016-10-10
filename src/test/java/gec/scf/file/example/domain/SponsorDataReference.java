package gec.scf.file.example.domain;

import java.io.Serializable;

import gec.scf.file.converter.DataFinder;
import gec.scf.file.converter.DataReference;
import gec.scf.file.converter.FinderType;

public class SponsorDataReference implements DataReference, Serializable {

	private static final long serialVersionUID = 6317505874210913496L;

	private String fieldName;

	private DataFinder dataFinder;

	@Override
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public FinderType getFinderType() {
		return DataFinderType.SUPPILER_ID_FINDER;
	}

	@Override
	public DataFinder getDataFinder() {
		return dataFinder;
	}

	public void setDataFinder(DataFinder dataFinder) {
		this.dataFinder = dataFinder;
	}
	
}
