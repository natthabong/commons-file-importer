package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileType;

public class FileConverterFactory {

	public <T> FileConverter<T> getFileConverter(FileLayoutConfig config,
			Class<T> clazz) {
		FileConverter<T> converter = null;
		if (FileType.FIXED_LENGTH.equals(config.getFileType())) {
			converter = new FixedLengthFileConverter<T>(config, clazz);
		}
		else {
			converter = new CSVFileConverter<T>(config, clazz);
		}
		return converter;

	}
}