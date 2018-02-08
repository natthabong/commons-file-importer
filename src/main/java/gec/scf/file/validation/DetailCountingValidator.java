package gec.scf.file.validation;

import java.text.MessageFormat;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.DataObserver;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.exception.WrongFormatFileException;

public class DetailCountingValidator implements FieldValidator, DataObserver<Integer> {

	/**
	 * 
	 */

	private final FileLayoutConfigItem configItem;

	private int total;

	public DetailCountingValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {
		try {
			int footerTotalDocument = Integer.parseInt(String.valueOf(data));
			int totalDetail = (Integer) getValue();

			if (footerTotalDocument != totalDetail) {
				throw new WrongFormatFileException(MessageFormat.format(
						CovertErrorConstant.FOOTER_TOTAL_LINE_INVALIDE_LENGTH_MESSAGE,
						configItem.getDisplayValue(), footerTotalDocument, totalDetail));
			}
		}
		catch (NumberFormatException e) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.INVALID_FORMAT,
							configItem.getDisplayValue(), data));
		}

	}

	@Override
	public RecordType getObserveSection() {
		return RecordType.DETAIL;
	}

	@Override
	public void observe(Object currentLine) {
		total++;

	}

	@Override
	public Integer getValue() {
		return Integer.valueOf(total);
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return null;
	}
}