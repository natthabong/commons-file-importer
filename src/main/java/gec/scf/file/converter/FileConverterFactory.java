package gec.scf.file.converter;

import gec.scf.file.importer.domain.ImportContext;

public interface FileConverterFactory {

	public <T> FileConverter<T> createConverter(ImportContext importContext,
			Class<T> clazz);

}
