package gec.scf.file.converter;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import gec.scf.file.exception.WrongFormatFileException;

public class CSVFileConverterCheckFileFormatTest {

	@Test(expected = WrongFormatFileException.class)
	public void given_import_binary_file_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException, FileNotFoundException {
		// Arrange
		CSVFileConverter<Object> csvFileConverter = new CSVFileConverter<Object>(Object.class);

		URL part = this.getClass().getResource("binaryFileConverter.txt");

		File csvFile = new File(part.getFile());
		InputStream csvFileContent = new FileInputStream(csvFile);
		// Actual
		try {
			csvFileConverter.checkFileFormat(csvFileContent);
		}
		catch (WrongFormatFileException e) {
			assertEquals("Data is binary file", e.getErrorMessage());
			throw e;
		}
	}

	@Ignore
	@Test
	public void given_import_csv_file_when_check_file_format_should_not_WrongFormatFileException()
			throws FileNotFoundException, WrongFormatFileException {
		// Arrange
		CSVFileConverter<Object> csvFileConverter = new CSVFileConverter<Object>(Object.class);

		URL part = this.getClass().getResource("bigcsponsor.csv");

		File csvFile = new File(part.getFile());
		InputStream csvFileContent = new FileInputStream(csvFile);
		
		// Actual
		csvFileConverter.checkFileFormat(csvFileContent);
	}
}
