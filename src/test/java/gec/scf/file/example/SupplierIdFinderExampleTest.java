package gec.scf.file.example;

import gec.scf.file.converter.DataFinder;
import gec.scf.file.converter.FinderType;
import gec.scf.file.example.domain.DataFinderType;

public class SupplierIdFinderExampleTest implements DataFinder {

	@Override
	public String find(Object object) {
		return "00031311";
	}

	@Override
	public FinderType getFinderType() {
		return DataFinderType.SUPPILER_ID_FINDER;
	}

	@Override
	public void fixReturnFindResult(String specialVaule) {
		// TODO Auto-generated method stub		
	}

}
