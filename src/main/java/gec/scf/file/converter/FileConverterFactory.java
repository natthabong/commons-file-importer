package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfig;

public class FileConverterFactory {

	public FileConverter getFileConverter(FileLayoutConfig config) {

		// TODO: Implement converter selection here
		CSVFileConverter converter = new CSVFileConverter();
		converter.setSponsorConfig(config);
		return converter;

	}
}
