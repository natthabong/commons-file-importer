package gec.scf.file.configuration;

public class DefaultFileLayoutConfigItem implements FileLayoutConfigItem {

	private Integer startIndex;

	private Integer length;

	private RecordType recordType;

	private String docFieldName;

	private String displayValue;

	private String expectedValue;

	private boolean isRequired;

	private String datetimeFormat;

	private String paddingCharacter;

	private PaddingType paddingType;

	private String plusSymbol;

	private String minusSymbol;

	private Integer decimalPlace;

	private boolean isTransient;

	private Boolean hasDecimalPlace;

	private Boolean has1000Separator;

	private String defaultValue;

	private FileLayoutConfigItem signFlagConfig;

	private ValidationType validationType;

	private FileLayoutConfigItem validationRecordFieldConfig;

	private ItemType itemType;

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	@Override
	public String getDocFieldName() {
		return docFieldName;
	}

	public RecordType getRecordType() {
		return recordType;
	}

	@Override
	public RecordType getRecordTypeData() {
		return recordType;
	}

	@Override
	public Integer getStartIndex() {
		return startIndex;
	}

	@Override
	public Integer getLenght() {
		return length;
	}

	@Override
	public String getDatetimeFormat() {
		return datetimeFormat;
	}

	@Override
	public String getPositiveFlag() {
		return plusSymbol;
	}

	@Override
	public String getNegativeFlag() {
		return minusSymbol;
	}

	@Override
	public Integer getDecimalPlace() {
		return decimalPlace;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public void setLength(Integer length) {
		this.length = length;

	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;

	}

	public void setDocFieldName(String docFieldName) {
		this.docFieldName = docFieldName;
	}

	@Override
	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public String getExpectedValue() {
		return expectedValue;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public boolean isRequired() {
		return isRequired;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;

	}

	public void setDatetimeFormat(String datetimeFormat) {
		this.datetimeFormat = datetimeFormat;
	}

	public int getLength() {
		return length;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;

	}

	public void setDecimalPlace(Integer decimalPlace) {
		this.decimalPlace = decimalPlace;
	}

	public void setPaddingCharacter(String paddingCharacter) {
		this.paddingCharacter = paddingCharacter;

	}

	public String getPaddingCharacter() {
		return paddingCharacter;
	}

	public void setPaddingType(PaddingType paddingType) {
		this.paddingType = paddingType;
	}

	public PaddingType getPaddingType() {
		return paddingType;
	}

	public void setDecimalPlace(int decimalPlace) {
		this.decimalPlace = decimalPlace;
	}

	public void setMinusSymbol(String minusSymbol) {
		this.minusSymbol = minusSymbol;
	}

	@Override
	public boolean isTransient() {
		return isTransient;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Boolean has1000Separator() {
		return has1000Separator;
	}

	@Override
	public Boolean hasDecimalPlace() {
		return hasDecimalPlace;
	}

	public void setHasDecimalPlace(Boolean hasDecimalPlace) {
		this.hasDecimalPlace = hasDecimalPlace;
	}

	public void setHas1000Separator(Boolean has1000Separator) {
		this.has1000Separator = has1000Separator;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public FileLayoutConfigItem getSignFlagConfig() {
		return signFlagConfig;
	}

	public void setPlusSymbol(String plusSymbol) {
		this.plusSymbol = plusSymbol;

	}

	public void setSignFlagConfig(FileLayoutConfigItem signFlagConfig) {
		this.signFlagConfig = signFlagConfig;
	}

	public void setValidationType(ValidationType validationType) {
		this.validationType = validationType;
	}

	@Override
	public ValidationType getValidationType() {
		return validationType;
	}

	@Override
	public FileLayoutConfigItem getValidationRecordFieldConfig() {
		return validationRecordFieldConfig;
	}

	public void setValidationRecordFieldConfig(
			FileLayoutConfigItem validationRecordFieldConfig) {
		this.validationRecordFieldConfig = validationRecordFieldConfig;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datetimeFormat == null) ? 0 : datetimeFormat.hashCode());
		result = prime * result + ((decimalPlace == null) ? 0 : decimalPlace.hashCode());
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((displayValue == null) ? 0 : displayValue.hashCode());
		result = prime * result + ((docFieldName == null) ? 0 : docFieldName.hashCode());
		result = prime * result
				+ ((expectedValue == null) ? 0 : expectedValue.hashCode());
		result = prime * result
				+ ((has1000Separator == null) ? 0 : has1000Separator.hashCode());
		result = prime * result
				+ ((hasDecimalPlace == null) ? 0 : hasDecimalPlace.hashCode());
		result = prime * result + (isRequired ? 1231 : 1237);
		result = prime * result + (isTransient ? 1231 : 1237);
		result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((minusSymbol == null) ? 0 : minusSymbol.hashCode());
		result = prime * result
				+ ((paddingCharacter == null) ? 0 : paddingCharacter.hashCode());
		result = prime * result + ((paddingType == null) ? 0 : paddingType.hashCode());
		result = prime * result + ((plusSymbol == null) ? 0 : plusSymbol.hashCode());
		result = prime * result + ((recordType == null) ? 0 : recordType.hashCode());
		result = prime * result + ((startIndex == null) ? 0 : startIndex.hashCode());
		result = prime * result
				+ ((validationType == null) ? 0 : validationType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultFileLayoutConfigItem other = (DefaultFileLayoutConfigItem) obj;
		if (datetimeFormat == null) {
			if (other.datetimeFormat != null)
				return false;
		}
		else if (!datetimeFormat.equals(other.datetimeFormat))
			return false;
		if (decimalPlace == null) {
			if (other.decimalPlace != null)
				return false;
		}
		else if (!decimalPlace.equals(other.decimalPlace))
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		}
		else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (displayValue == null) {
			if (other.displayValue != null)
				return false;
		}
		else if (!displayValue.equals(other.displayValue))
			return false;
		if (docFieldName == null) {
			if (other.docFieldName != null)
				return false;
		}
		else if (!docFieldName.equals(other.docFieldName))
			return false;
		if (expectedValue == null) {
			if (other.expectedValue != null)
				return false;
		}
		else if (!expectedValue.equals(other.expectedValue))
			return false;
		if (has1000Separator == null) {
			if (other.has1000Separator != null)
				return false;
		}
		else if (!has1000Separator.equals(other.has1000Separator))
			return false;
		if (hasDecimalPlace == null) {
			if (other.hasDecimalPlace != null)
				return false;
		}
		else if (!hasDecimalPlace.equals(other.hasDecimalPlace))
			return false;
		if (isRequired != other.isRequired)
			return false;
		if (isTransient != other.isTransient)
			return false;
		if (itemType != other.itemType)
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		}
		else if (!length.equals(other.length))
			return false;
		if (minusSymbol == null) {
			if (other.minusSymbol != null)
				return false;
		}
		else if (!minusSymbol.equals(other.minusSymbol))
			return false;
		if (paddingCharacter == null) {
			if (other.paddingCharacter != null)
				return false;
		}
		else if (!paddingCharacter.equals(other.paddingCharacter))
			return false;
		if (paddingType != other.paddingType)
			return false;
		if (plusSymbol == null) {
			if (other.plusSymbol != null)
				return false;
		}
		else if (!plusSymbol.equals(other.plusSymbol))
			return false;
		if (recordType != other.recordType)
			return false;
		if (startIndex == null) {
			if (other.startIndex != null)
				return false;
		}
		else if (!startIndex.equals(other.startIndex))
			return false;
		if (validationType != other.validationType)
			return false;
		return true;
	}

	@Override
	public ItemType getItemType() {
		return itemType;
	}

	@Override
	public String getItemDataType() {
		// TODO Auto-generated method stub
		return null;
	}

}
