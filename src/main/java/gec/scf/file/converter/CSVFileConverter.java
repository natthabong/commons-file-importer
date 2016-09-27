package gec.scf.file.converter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

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
	public void checkFileFormat(File fileContent) throws WrongFormatFileException {

		CSVParser csvParser = null;
		try {

//			validateBinaryFile(fileContent);
//			BufferedInputStream bfi = new BufferedInputStream(fileContent);
//			String mimeType = URLConnection.guessContentTypeFromStream(fileContent);
//			if(!mimeType.equals("csv")){
//				throw new WrongFormatFileException("File extenstion (txt) invalid format .csv");
//			}
			csvParser = new CSVParser(new InputStreamReader(new FileInputStream(fileContent), "UTF-8"),
					CSVFormat.EXCEL.withSkipHeaderRecord(true));

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
		}

		return result;
	}

	private T convertCSVToDocument(CSVRecord csvRecord,
			List<? extends FileLayoutConfigItem> itemConfigs) {

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
				field = domainClass.getDeclaredField(itemConf.getFieldName());
				field.setAccessible(true);
				Class<?> classType = field.getType();
				int startIndex = itemConf.getStartIndex() - 1;
				if (classType.isAssignableFrom(Date.class)) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							itemConf.getDatetimeFormat(), Locale.US);
					Date date = sdf.parse(csvRecord.get(startIndex));
					field.set(document, date);
				}
				else if (classType.isAssignableFrom(BigDecimal.class)) {
					String money = csvRecord.get(startIndex);
					if (StringUtils.isBlank(money)) {
						money = "0";
					}
					money = money.replace(",", "");
					BigDecimal amount = new BigDecimal(money);
					field.set(document, amount);
				}
				else {
					field.set(document, csvRecord.get(startIndex));
				}
			}
			catch (Exception e) {
				// TODO: Implement on detail error here
				e.printStackTrace();
			}

		}
		return document;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
	}
}
