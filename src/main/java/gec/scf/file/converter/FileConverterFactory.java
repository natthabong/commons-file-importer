package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfig;

public interface FileConverterFactory {

	public <T> FileConverter<T> createConverter(FileLayoutConfig config, Class<T> clazz);

}
