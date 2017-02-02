package gec.scf.file.observer;

import java.text.MessageFormat;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.FileObserver;
import gec.scf.file.exception.WrongFormatFileException;

public class HeaderMatchingValidator implements FieldValidator {

	/**
	 * 
	 */

	private final FileLayoutConfigItem configItem;

	private FileObserver<?> fileObserver;

	public HeaderMatchingValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {
		String footerData = String.valueOf(data);
		String headerData = String.valueOf(fileObserver.getValue());

		if (!footerData.equals(headerData)) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.MISMATCH_WITH_HEADER,
							configItem.getDisplayValue(), footerData, headerData));
		}

	}

	@Override
	public void setObserver(FileObserver<?> fileObserver) {
		this.fileObserver = fileObserver;

	}

}