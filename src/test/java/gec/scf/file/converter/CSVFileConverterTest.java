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

public class CSVFileConverterTest {

	@Ignore
	@Test(expected = WrongFormatFileException.class)
	public void given_import_binary_file_when_check_file_format_should_throw_WrongFormatFileException()
			throws WrongFormatFileException, FileNotFoundException {
		// Arrange
		CSVFileConverter<Object> csvFileConverter = new CSVFileConverter<Object>(Object.class);

		URL part = this.getClass().getResource("binaryFileConverter.txt");

		File csvFile = new File(part.getFile());

		// Actual
		try {
			csvFileConverter.checkFileFormat(csvFile);
		}
		catch (WrongFormatFileException e) {
			assertEquals("Data is binary file", e.getErrorMessage());
			throw e;
		}
	}

	@Ignore
	@Test(expected = WrongFormatFileException.class)
	public void given_import_file_extenstion_txt_when_check_file_format_should_throw_WrongFormatFileException() throws FileNotFoundException, WrongFormatFileException {
		// Arrange
		CSVFileConverter<Object> csvFileConverter = new CSVFileConverter<Object>(Object.class);

		URL part = this.getClass().getResource("bigcsponsor2.txt");

		File csvFile = new File(part.getFile());

		// Actual
		try {
			csvFileConverter.checkFileFormat(csvFile);
		}
		catch (WrongFormatFileException e) {
			assertEquals("File extenstion (txt) invalid format .csv", e.getErrorMessage());
//			assertEquals("Data is binary file", e.getErrorMessage());
			throw e;
		}
	}
}
