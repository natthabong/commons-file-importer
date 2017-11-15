package gec.scf.file.converter;

public interface CovertErrorConstant {

	String DATA_LENGTH_OVER = "Data length ({0}) must have {1} characters";

	String FOOTER_TOTAL_LINE_INVALIDE_LENGTH_MESSAGE = "{0} ({1}) is invalid. Total detail line is {2}";

	String FOOTER_TOTAL_AMOUNT_INVALIDE_LENGTH_MESSAGE = "{0} ({1,number,#,##0.00}) is invalid. Total detail line is {2,number,#,##0.00}";

	String FOOTER_NOT_LAST_FILE = "{0} ({1}) not found on last row";
	String RECORD_ID_MISS_MATCH = "{0} ({1}) mismatch";

	String HEADER_NOT_FIRST_LINE_OF_FILE = "{0} ({1}) not found on first row";
	String FOOTER_NOT_LAST_LINE_OF_FILE = "{0} ({1}) not found on last row";

	String ERROR_MESSAGE_IS_REQUIRE = "{0} is required";

	String INVALID_FORMAT = "{0} ({1}) invalid format";
	String MISMATCH_FORMAT = "{0} ({1}) mismatch";

	String DOCUMENT_NO_INVALID = " ({0}) is invalid. current Document No is {1}";

	String FILE_INVALID_FORMAT = "File invalid format";

	String DATA_OVER_MAX_LENGTH = "{0} length ({1}) is over max length ({2})";
	String DIGIT_OVER_MAX_DIGIT = "{0} digit is over max digit ({1})";

	String DATA_LENGTH_OF_FIELD_OVER = "data length ({0}) must have {1} field";

	String MISMATCH_WITH_HEADER = "{0} ({1}) in footer is invalid, mismatch in header ({2})";
	
	String EQUAL_TO_UPLOAD_DATE_INVALID = "{0} ({1}) not equals current date ({2})";
	
	String EQUAL_OR_GREATER_THAN_UPLOAD_DATE_INVALID =  "{0} ({1}) not equals or greater than current date ({2})";
	
	String GREATER_THAN_UPLOAD_DATE_INVALID =  "{0} ({1}) not greater than current date ({2})";
	
	String EQUAL_OR_LESS_THAN_UPLOAD_DATE_INVALID =  "{0} ({1}) not equal or less than current date ({2})";
	
	String LESS_THAN_UPLOAD_DATE_INVALID =  "{0} ({1}) not less than current date ({2})";
	
	String TRANSACTION_TYPE_MISMATCH = "{0} ({1}) transaction type mismatch";
}
