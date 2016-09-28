package gec.scf.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class CSVFileConverter<T> implements FileConverter<T> {

	private FileLayoutConfig fileLayoutConfig;

	private List<CSVRecord> csvRecords;

	private int currentLine;

	private Class<T> domainClass;

	public CSVFileConverter(Class<T> clazz) {
		this.domainClass = clazz;
	}

	public CSVFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this.fileLayoutConfig = fileLayoutConfig;
		this.domainClass = clazz;
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		CSVParser csvParser = null;
		try {

//			validateBinaryFile(fileContent);
			
			csvParser = new CSVParser(new InputStreamReader(fileContent, "UTF-8"),
					CSVFormat.EXCEL.withSkipHeaderRecord(true).withDelimiter(fileLayoutConfig.getDelimeter().charAt(0)));
			
			csvRecords = csvParser.getRecords();

			currentLine = 1;
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void validateBinaryFile(InputStream fileContent) throws IOException, WrongFormatFileException {
		int size = fileContent.available();
		if (size > 1024) {
			size = 1024;
		}
		byte[] data = new byte[size];
		fileContent.read(data);
		fileContent.close();
		
		int ascii = 0;
		int other = 0;
		
		for(int i = 0; i < data.length; i++) {
		    byte b = data[i];
		    if( b < 0x09 ){
		    	throw new WrongFormatFileException("Data is binary file");
		    }

		    if( b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D ){
		    	ascii++;
		    }
		    else if( b >= 0x20  &&  b <= 0x7E ){
		    	ascii++;
		    }
		    else{
		    	other++;
		    }
		}
		if(100 * other / (ascii + other) > 95){
			throw new WrongFormatFileException("Data is binary file");
		}
	}

	@Override
	public DetailResult getDetail() {

		DetailResult result = new DetailResult();
		result.setLineNo(currentLine);
		try {
			CSVRecord csvRecord = csvRecords.get(currentLine++);
			Object document = convertCSVToDocument(csvRecord,
					fileLayoutConfig.getConfigItems());
			result.setObjectValue(document);
			result.setSuccess(true);
		}
		catch (IndexOutOfBoundsException e) {
			result = null;
		}catch(WrongFormatDetailException e){
			result.setErrorLineDetails(e.getErrorLineDetails());
			result.setSuccess(false);
		}

		return result;
	}

	private T convertCSVToDocument(CSVRecord csvRecord,
			List<? extends FileLayoutConfigItem> itemConfigs) throws WrongFormatDetailException{
		boolean isError = false;
		List<ErrorLineDetail> errorLineDetails = new ArrayList<ErrorLineDetail>();
		
		T document = null;
		try {
			document = (T) domainClass.newInstance();
		}
		catch (InstantiationException e1) {
			e1.printStackTrace();
		}
		catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		for (FileLayoutConfigItem itemConf : itemConfigs) {
			Field field = null;
			try {
				int startIndex = itemConf.getStartIndex() - 1;
				String recordValue = csvRecord.get(startIndex);
				
				if(StringUtils.isNotBlank(itemConf.getFieldName())){
					field = domainClass.getDeclaredField(itemConf.getFieldName());
					field.setAccessible(true);
					Class<?> classType = field.getType();				
					
					if (classType.isAssignableFrom(Date.class)) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								itemConf.getDatetimeFormat(), Locale.US);
						Date date = sdf.parse(recordValue);
						field.set(document, date);
					}
					else if (classType.isAssignableFrom(BigDecimal.class)) {
						if (StringUtils.isBlank(recordValue)) {
							recordValue = "0";
						}
						recordValue = recordValue.replace(",", "");
						BigDecimal amount = new BigDecimal(recordValue);
						field.set(document, amount);
					}
					else {
						if(itemConf.getIsRequired()){
							if(StringUtils.isBlank(recordValue)){
								throw new WrongFormatDetailException(itemConf.getDisplayOfField() + FixedLengthErrorConstant.ERROR_MESSAGE_IS_REQUIRE);
							}else if(recordValue.length() > itemConf.getLenght()){
								throw new WrongFormatDetailException(MessageFormat.format(FixedLengthErrorConstant.DATA_OVER_MAX_LENGTH, itemConf.getDisplayOfField(), recordValue.length(), itemConf.getLenght()));
							}						
						}
						field.set(document, recordValue);
					}
				}
				
			}catch(WrongFormatDetailException e){
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLine);
				errorLineDetail.setErrorMessage(e.getErrorMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			}
			catch (Exception e) {
				//TODO manage error message
				e.printStackTrace();
			}
		}
		
		if(isError){
			throw new WrongFormatDetailException(errorLineDetails);
		}
		return document;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
	}
}
