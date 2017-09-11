package gec.scf.file.validation;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.converter.CovertErrorConstant;
import gec.scf.file.converter.DataObserver;
import gec.scf.file.converter.FieldValidator;
import gec.scf.file.converter.FieldValueSetter;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;

public class CloneHeaderData implements FieldValidator, DataObserver<String>, FieldValueSetter {

	private FileLayoutConfigItem configItem;

	private String value;

	private FileLayoutConfigItem matchingFieldConfig;

	public CloneHeaderData(FileLayoutConfigItem configItem) {
		this.configItem = configItem;
		this.matchingFieldConfig = configItem.getValidationRecordFieldConfig();
	}

	@Override
	public void validate(Object data) throws WrongFormatFileException {

	}

	@Override
	public RecordType getObserveSection() {
		return RecordType.HEADER;
	}

	@Override
	public void observe(Object data) {
		value = String.valueOf(data);
		if (matchingFieldConfig.isRequired() && StringUtils.isBlank(value)) {
			throw new WrongFormatDetailException(MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
					matchingFieldConfig.getDisplayValue()));
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public FileLayoutConfigItem getObserveFieldConfig() {
		return matchingFieldConfig;
	}

	@Override
	public void setValue(Object target, Object value) {
		try {
			// TODO refactor when bank use gecscf document field
			String docFieldName = null;
			if (StringUtils.isNotBlank(configItem.getDocumentFieldName())) {
				docFieldName = configItem.getDocumentFieldName();
			} else {
				docFieldName = configItem.getDocFieldName();
			}
			
			PropertyUtils.setProperty(target, docFieldName, value);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}