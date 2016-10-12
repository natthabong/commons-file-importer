package gec.scf.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.ErrorLineDetail;

public class CSVFileConverter<T> extends AbstractFileConverter<T> {

//	private static final Logger log = Logger.getLogger(CSVFileConverter.class);

	private FileLayoutConfig fileLayoutConfig;

	private List<CSVRecord> csvRecords;

	private int currentLine;

	public CSVFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		super(fileLayoutConfig, clazz);
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		CSVParser csvParser = null;
		try {

			if (fileLayoutConfig.isCheckBinaryFile()) {
				fileContent = validateBinaryFile(fileContent);
			}
			csvParser = new CSVParser(new InputStreamReader(fileContent, "UTF-8"),
					CSVFormat.EXCEL.withSkipHeaderRecord(true)
							.withDelimiter(fileLayoutConfig.getDelimeter().charAt(0)));


			csvRecords = csvParser.getRecords();

			validateDataLength(fileLayoutConfig.getConfigItems());

			currentLine = 1;
		} catch (WrongFormatFileException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void validateDataLength(List<? extends FileLayoutConfigItem> layoutConfigItems)
			throws WrongFormatFileException {
		int layoutItemLength = 0;
		for (FileLayoutConfigItem item : layoutConfigItems) {
			if (item.getStartIndex() == null)
				continue;

			if (layoutItemLength < item.getStartIndex()) {
				layoutItemLength = item.getStartIndex();
			}
		}

		for (CSVRecord record : csvRecords) {
			Iterator<String> iterator = record.iterator();

			int recordLength = 0;
			while (iterator.hasNext()) {
				iterator.next();
				recordLength++;
			}

			if (recordLength != layoutItemLength) {
				WrongFormatFileException error = new WrongFormatFileException();
				error.setErrorLineNo((int) record.getRecordNumber());
				error.setErrorMessage(MessageFormat.format(CovertErrorConstant.DATA_LENGTH_OF_FIELD_OVER, recordLength,
						layoutItemLength));
				throw error;
			}
		}
	}

	@Override
	public DetailResult<T> getDetail() {

		DetailResult<T> result = new DetailResult<T>();

		try {
			CSVRecord csvRecord = csvRecords.get(currentLine++);

			T document = convertCSVToObject(csvRecord, fileLayoutConfig.getConfigItems());

			result.setObjectValue(document);
			result.setSuccess(true);
			result.setLineNo(currentLine);
		} catch (WrongFormatDetailException e) {
			result.setErrorLineDetails(e.getErrorLineDetails());
			result.setSuccess(false);
			result.setLineNo(currentLine);
		} catch (IndexOutOfBoundsException e) {
			result = null;
		}

		return result;
	}

	private T convertCSVToObject(CSVRecord csvRecord, List<? extends FileLayoutConfigItem> itemConfigs)
			throws WrongFormatDetailException {

		boolean isError = false;

		List<ErrorLineDetail> errorLineDetails = new ArrayList<ErrorLineDetail>();

		T document = null;
		try {
			document = (T) getEntityClass().newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		for (FileLayoutConfigItem itemConf : itemConfigs) {
			try {
				String recordValue = "";
				
				if(StringUtils.isNotBlank(itemConf.getDefaultValue())){
					recordValue = itemConf.getDefaultValue();
				}else{
					int startIndex = itemConf.getStartIndex() - 1;
					recordValue = csvRecord.get(startIndex);
				}
				
				if (StringUtils.isNotBlank(itemConf.getDocFieldName())) {
					applyObjectValue(document, itemConf, recordValue);
				}

			} catch (WrongFormatDetailException e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLine);
				errorLineDetail.setErrorMessage(e.getErrorMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			} catch (Exception e) {
				// TODO manage error message
				e.printStackTrace();
			}
		}

		if (isError) {
			throw new WrongFormatDetailException(errorLineDetails);
		}
		return document;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
	}
}
