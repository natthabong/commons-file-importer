package gec.scf.file.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;

public class CSVFileConverterGetDetailFileTest {

	@Test
	public void given_detail_valid_format_when_get_detail_should_success() throws WrongFormatFileException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// Arrange
		String[] csvValidFileContent = new String[3];
		csvValidFileContent[0] = "No,Payer Code,Deposit Branch,Payer,Bank Code,Bank,Cheque Branch,Cheque No,Cheque Due Date,Good Fund Date,Deposit Date,Cheque Amount,Clearing Type";
		csvValidFileContent[1] = "1,5572692,หาดใหญ่ใน,โรงโม่หินศิลามหานคร,4,KBANK,สามแยกอ่างศิลา,100093214,9/22/2016,30/9/2016,24/10/2014,560000,BCOB";

		InputStream csvFileContent = new ByteArrayInputStream(
				StringUtils.join(csvValidFileContent, System.lineSeparator()).getBytes());

		SponsorFileLayoutConfig fileLayout = getLayoutConfig();

		CSVFileConverter<Document> csvFileConverter = new CSVFileConverter<Document>(Document.class);
		csvFileConverter.setFileLayoutConfig(fileLayout);
		csvFileConverter.checkFileFormat(csvFileContent);

		// Actual
		DetailResult actualResult = csvFileConverter.getDetail();
		Document docResult = (Document) actualResult.getObjectValue();

		// Assert
		assertTrue(actualResult.isSuccess());
		assertEquals("5572692", docResult.getSupplierId());
	}

	private SponsorFileLayoutConfig getLayoutConfig() {
		SponsorFileLayoutConfig fileLayout = new SponsorFileLayoutConfig();
		List<FileLayoutConfigItem> layoutItems = new ArrayList<FileLayoutConfigItem>();

		SponsorFileLayoutConfigItem fileItem = new SponsorFileLayoutConfigItem();
		fileItem.setStartIndex(2);
		fileItem.setLength(5);
		fileItem.setFieldName("supplierId");

		layoutItems.add(fileItem);
		fileLayout.setConfigItems(layoutItems);
		return fileLayout;
	}
}
