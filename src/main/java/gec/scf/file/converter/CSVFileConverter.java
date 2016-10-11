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
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.ErrorLineDetail;

public class CSVFileConverter<T> extends AbstractFileConverter<T> {
	
	private static final Logger log = Logger.getLogger(CSVFileConverter.class);

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

			// validateBinaryFile(fileContent);

			int csvLengthConfig = fileLayoutConfig.getConfigItems().size();

			csvParser = new CSVParser(new InputStreamReader(fileContent, "UTF-8"),
					CSVFormat.EXCEL.withSkipHeaderRecord(true)
							.withDelimiter(fileLayoutConfig.getDelimeter().charAt(0)));

			csvRecords = csvParser.getRecords();

			validateDataLength(csvLengthConfig);

			currentLine = 1;
		}
		catch (WrongFormatFileException e) {
			throw e;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void validateDataLength(int csvLengthConfig) throws WrongFormatFileException {
		for (CSVRecord record : csvRecords) {
			Iterator<String> iterator = record.iterator();
			int recordLength = 0;
			while (iterator.hasNext()) {
				iterator.next();
				recordLength++;
			}
			if (recordLength != csvLengthConfig) {
				WrongFormatFileException error = new WrongFormatFileException();
				error.setErrorLineNo((int) record.getRecordNumber());
				error.setErrorMessage(MessageFormat.format(
						CovertErrorConstant.DATA_LENGTH_OF_FIELD_OVER, recordLength,
						csvLengthConfig));
				throw error;
			}
		}
	}

	@Override
	public DetailResult<T> getDetail() {

		DetailResult<T> result = new DetailResult<T>();
		result.setLineNo(currentLine);
		try {
			CSVRecord csvRecord = csvRecords.get(currentLine++);

			T document = convertCSVToObject(csvRecord, fileLayoutConfig.getConfigItems());
			if (fileLayoutConfig.isRequiredFindAndMergeOption()) {
				DataMerge dataMerge = new DataMerge(
						fileLayoutConfig.getDataReferences());
				try {
					dataMerge.merge(document, document.getClass());
				}
				catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

			result.setObjectValue(document);
			result.setSuccess(true);
		}
		catch (IndexOutOfBoundsException e) {
			result = null;
		}
		catch (WrongFormatDetailException e) {
			result.setErrorLineDetails(e.getErrorLineDetails());
			result.setSuccess(false);
		}

		return result;
	}

	private T convertCSVToObject(CSVRecord csvRecord,
			List<? extends FileLayoutConfigItem> itemConfigs)
			throws WrongFormatDetailException {

		boolean isError = false;

		List<ErrorLineDetail> errorLineDetails = new ArrayList<ErrorLineDetail>();

		T document = null;
		try {
			document = (T) getEntityClass().newInstance();
		}
		catch (InstantiationException e1) {
			e1.printStackTrace();
		}
		catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		for (FileLayoutConfigItem itemConf : itemConfigs) {
			try {
				int startIndex = itemConf.getStartIndex() - 1;
				String recordValue = csvRecord.get(startIndex);

				if (StringUtils.isNotBlank(itemConf.getFieldName())) {
					applyObjectValue(document, itemConf, recordValue);
				}

			}
			catch (WrongFormatDetailException e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLine);
				errorLineDetail.setErrorMessage(e.getErrorMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			}
			catch (Exception e) {
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
