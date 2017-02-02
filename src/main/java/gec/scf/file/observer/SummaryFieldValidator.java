package gec.scf.file.observer;

import java.math.BigDecimal;
import java.text.MessageFormat;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.FileObserver;
import gec.scf.file.exception.WrongFormatFileException;

public class SummaryFieldValidator implements FieldValidator {

	private final FileLayoutConfigItem configItem;

	private FileObserver<?> fileObserver;

	public SummaryFieldValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {
		BigDecimal totalDetail = (BigDecimal) fileObserver.getValue();
		BigDecimal totalFooter = (BigDecimal) data;

		if (totalFooter.compareTo(totalDetail) != 0) {
			throw new WrongFormatFileException(
					MessageFormat.format(
							CovertErrorConstant.FOOTER_TOTAL_AMOUNT_INVALIDE_LENGTH_MESSAGE,
							configItem.getDisplayValue(), totalFooter.doubleValue(),
							totalDetail.doubleValue()),
					null);
		}
	}

	@Override
	public void setObserver(FileObserver<?> fileObserver) {
		this.fileObserver = fileObserver;

	}
}