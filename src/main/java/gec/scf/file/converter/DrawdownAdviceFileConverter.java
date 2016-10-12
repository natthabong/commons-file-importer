package gec.scf.file.converter;

import java.io.InputStream;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.exception.WrongFormatFileException;

public class DrawdownAdviceFileConverter<T> extends FixedLengthFileConverter<T> {

//	private static final Logger log = Logger.getLogger(DrawdownAdviceFileConverter.class);

	private Long lastDocumentNo;

	public DrawdownAdviceFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz, Long lastDocumentNo) {
		super(fileLayoutConfig, clazz);
		this.lastDocumentNo = lastDocumentNo;
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {
		super.setLastDocumentNo(lastDocumentNo);
		super.checkFileFormat(fileContent);
		lastDocumentNo = super.getLastDocumentNo();
	}

	public Long getLastDocumentNo() {
		return lastDocumentNo;
	}
}
