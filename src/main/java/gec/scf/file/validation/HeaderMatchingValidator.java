package gec.scf.file.validation;

import java.text.MessageFormat;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.DataObserver;
import gec.scf.file.exception.WrongFormatFileException;

public class HeaderMatchingValidator implements FieldValidator, DataObserver<String> {

	/**
	 * 
	 */

	private final FileLayoutConfigItem configItem;

	private DataObserver<?> fileObserver;

	private String value;

	private FileLayoutConfigItem matchingFieldConfig;

	public HeaderMatchingValidator(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
		this.matchingFieldConfig = configItem.getValidationRecordFieldConfig();
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
	public RecordType getObserveSection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void observe(Object data) {
		value = String.valueOf(data);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return matchingFieldConfig;
	}

}