package gec.scf.file.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutItemConfig;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class CSVFileConverter implements FileConverter {

	private FileLayoutConfig fileLayoutConfig;

	private List<CSVRecord> csvRecords;

	private int currentLine;

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		CSVParser csvParser = null;
		try {
			csvParser = new CSVParser(new InputStreamReader(fileContent, "UTF-8"),
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

	@Override
	public DetailResult getDetail() {

		DetailResult result = new DetailResult();
		result.setLineNo(currentLine);
		try {
			CSVRecord csvRecord = csvRecords.get(currentLine++);
			Object document = convertCSVToDocument(csvRecord, fileLayoutConfig.getItems());
			result.setValue(document);
			result.setSuccess(true);
		}
		catch (IndexOutOfBoundsException e) {
			result = null;
		}

		return result;
	}

	private Object convertCSVToDocument(CSVRecord csvRecord,
			List<FileLayoutItemConfig> itemConfigs) {
		Object document = new Object();
		Class<?> documentClass = document.getClass();
		for (FileLayoutItemConfig itemConf : itemConfigs) {
			Field field = null;
			try {
				field = documentClass.getDeclaredField(itemConf.getFieldName());
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

	public FileLayoutConfig getSponsorConfig() {
		return fileLayoutConfig;
	}

	public void setSponsorConfig(FileLayoutConfig sponsorConfig) {
		this.fileLayoutConfig = sponsorConfig;
	}

	public void setFileLayoutConfig(FileLayoutConfig fileLayoutConfig) {
		this.fileLayoutConfig = fileLayoutConfig;
	}

}
