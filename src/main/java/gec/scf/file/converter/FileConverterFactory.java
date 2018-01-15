package gec.scf.file.converter;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.importer.domain.Channel;

public interface FileConverterFactory {

	public <T> FileConverter<T> createConverter(FileLayoutConfig config, Class<T> clazz , Channel channel);

}
