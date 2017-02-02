package gec.scf.file.observer;

import java.text.MessageFormat;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.FileObserver;
import gec.scf.file.exception.WrongFormatFileException;

public class DetailCountingValidator implements FieldValidator {

	/**
	 * 
	 */

	private final FileLayoutConfigItem configItem;

	private FileObserver<?> fileObserver;

	public DetailCountingValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {
		try {
			int footerTotalDocument = Integer.parseInt(String.valueOf(data));
			int totalDetail = (Integer) fileObserver.getValue();

			if (footerTotalDocument != totalDetail) {
				throw new WrongFormatFileException(MessageFormat.format(
						CovertErrorConstant.FOOTER_TOTAL_LINE_INVALIDE_LENGTH_MESSAGE,
						configItem.getDisplayValue(), footerTotalDocument, totalDetail));
			}
		}
		catch (NumberFormatException e) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.INVALIDE_FORMAT,
							configItem.getDisplayValue(), data));
		}

	}

	@Override
	public void setObserver(FileObserver<?> fileObserver) {
		this.fileObserver = fileObserver;

	}

}