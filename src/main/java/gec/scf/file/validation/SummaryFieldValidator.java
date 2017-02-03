package gec.scf.file.validation;

import java.math.BigDecimal;
import java.text.MessageFormat;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.DataObserver;
import gec.scf.file.exception.WrongFormatFileException;

public class SummaryFieldValidator implements FieldValidator, DataObserver<BigDecimal> {

	private final FileLayoutConfigItem configItem;

	private FileLayoutConfigItem aggregationFieldConfig;

	private BigDecimal value = new BigDecimal("0.0");

	public SummaryFieldValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
		this.aggregationFieldConfig = configItem.getValidationRecordFieldConfig();
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {
		BigDecimal totalDetail = (BigDecimal) getValue();
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
	public RecordType getObserveSection() {
		return RecordType.DETAIL;
	}

	@Override
	public void observe(Object data) {
		value = value.add((BigDecimal) data);
	}

	@Override
	public BigDecimal getValue() {
		return value;
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return aggregationFieldConfig;
	}
}