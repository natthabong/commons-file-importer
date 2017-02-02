package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.observer.DetailCountingObserver;
import gec.scf.file.observer.SummaryFieldObserver;

public class FileObserverFactory {

	FileObserver<?> create(FileLayoutConfigItem fileLayoutConfigItem) {
		FileObserver<?> result = null;
		if (fileLayoutConfigItem.getValidationType() != null) {
			switch (fileLayoutConfigItem.getValidationType()) {
			case COUNT_OF_DOCUMENT_DETAIL:
				result = new DetailCountingObserver(fileLayoutConfigItem);
				break;
			case SUMMARY_OF_FIELD:
				result = new SummaryFieldObserver(fileLayoutConfigItem);
				break;
			default:
				break;
			}
		}
		return result;
	}

}
