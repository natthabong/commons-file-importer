package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfig;

public class FileConverterFactory {

	public <T> FileConverter<T> getFileConverter(FileLayoutConfig config, Class<T> clazz) {

		// TODO: Implement converter selection here
		CSVFileConverter<T> converter = new CSVFileConverter<T>();
		converter.setSponsorConfig(config);
		return converter;

	}
}
