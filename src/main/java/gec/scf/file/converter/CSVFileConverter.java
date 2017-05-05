package gec.scf.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.domain.ErrorLineDetail;

public class CSVFileConverter<T> extends AbstractFileConverter<T> {

	private List<String[]> csvRecords;

	private int currentLine;

	private int totalColumns;

	private int offset;

	public CSVFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz,
			FieldValidatorFactory fieldValidatorFactory) {

		super(fileLayoutConfig, clazz, fieldValidatorFactory);

		List<FileLayoutConfigItem> detailConfigs = getFileLayoutMappingFor(
				RecordType.DETAIL);

		if (detailConfigs != null) {

			for (FileLayoutConfigItem item : detailConfigs) {
				if (item.getStartIndex() == null)
					continue;
				if (totalColumns < item.getStartIndex()) {
					totalColumns = item.getStartIndex();
				}
			}
		}
	}

	public CSVFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this(fileLayoutConfig, clazz, null);
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		if (getFileLayoutConfig().getOffsetRowNo() != null) {
			offset = getFileLayoutConfig().getOffsetRowNo().intValue();
		}

		if (offset < 1) {
			offset = 1;
		}

		int cuurentRow = offset;
		try {
			List<FileLayoutConfigItem> detailConfigs = getFileLayoutMappingFor(
					RecordType.DETAIL);

			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new InputStreamReader(fileContent,
					getFileLayoutConfig().getCharsetName()), ',', '"', offset);

			csvRecords = reader.readAll();
			for (String[] record : csvRecords) {
				cuurentRow++;
				validateLineDataLength(record, detailConfigs);

			}
		}
		catch (WrongFormatFileException e) {
			e.setErrorLineNo(cuurentRow);
			throw e;
		}
		catch (IOException e) {
			throw new WrongFormatFileException(e.getMessage(), cuurentRow);
		}

	}

	protected void validateLineDataLength(Object currentLine,
			List<FileLayoutConfigItem> layoutConfigItems)
			throws WrongFormatFileException {
		String[] lineData = (String[]) currentLine;
		int recordColumns = lineData.length;
		if (recordColumns != totalColumns) {
			WrongFormatFileException error = new WrongFormatFileException();
			error.setErrorMessage(
					MessageFormat.format(CovertErrorConstant.DATA_LENGTH_OF_FIELD_OVER,
							recordColumns, totalColumns));
			throw error;
		}
	}

	@Override
	public DetailResult<T> getDetail() {

		DetailResult<T> result = new DetailResult<T>();

		try {
			List<FileLayoutConfigItem> detailConfigs = getFileLayoutMappingFor(
					RecordType.DETAIL);
			String[] csvRecord = csvRecords.get(currentLine++);

			T detail = convertDetail(csvRecord, detailConfigs);

			result.setObjectValue(detail);
			result.setSuccess(true);
			result.setLineNo(offset + currentLine);
		}
		catch (WrongFormatDetailException e) {
			result.setErrorLineDetails(e.getErrorLineDetails());
			result.setSuccess(false);
			result.setLineNo(offset + currentLine);
		}
		catch (IndexOutOfBoundsException e) {
			result = null;
		}

		return result;
	}

	private T convertDetail(String[] csvRecord,
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
				applyObjectValue(document, itemConf, csvRecord);
			}
			catch (WrongFormatDetailException e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(offset + currentLine);
				errorLineDetail.setErrorMessage(e.getErrorMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			}
			catch (Exception e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(offset + currentLine);
				errorLineDetail.setErrorMessage(e.getMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			}
		}

		if (isError) {
			throw new WrongFormatDetailException(errorLineDetails);
		}
		return document;
	}

	@Override
	String getCuttedData(FileLayoutConfigItem itemConf, Object currentLine) {
		String[] columns = (String[]) currentLine;
		return columns[itemConf.getStartIndex() - 1];
	}

}
