package gec.scf.file.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.validation.SummaryFieldValidator;

public class SpecificFileConverter<T> extends FixedLengthFileConverter<T> {

	private File tempFile;

	private BufferedReader tempFileReader;

	private static final Logger log = Logger.getLogger(SpecificFileConverter.class);

	public SpecificFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this(fileLayoutConfig, clazz, null);
	}

	public SpecificFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz,
			FieldValidatorFactory fieldValidatorFactory) {
		super(fileLayoutConfig, clazz, fieldValidatorFactory);
	}

	@Override
	public void checkFileFormat(InputStream fileContent) throws WrongFormatFileException {

		BufferedWriter bufferedWriter = null;
		BufferedReader bufferReader = null;

		currentLineNo = 1;

		try {
			if (fileContent == null) {
				throw new IllegalArgumentException("Invalid file content");
			}
			FileLayoutConfig layoutConfig = getFileLayoutConfig();
			tempFile = File.createTempFile("DOCUMENT_T_", ".temp");
			bufferedWriter = new BufferedWriter(new FileWriter(tempFile));

			InputStream tempFileContent = new FileInputStream(tempFile);

			tempFileReader = new BufferedReader(new InputStreamReader(tempFileContent,
					layoutConfig.getCharsetName()));

			bufferReader = new BufferedReader(
					new InputStreamReader(fileContent, layoutConfig.getCharsetName()));

			RecordTypeExtractor headerRecordTypeExtractor = getExtractor(
					RecordType.HEADER);

			int totalHeaderRecord = 0;

			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {

				// When found a blank line
				if (StringUtils.isBlank(currentLine)) {
					// Found a blank line on the end of file, do nothing
					continue;
				}

				String recordType = headerRecordTypeExtractor.extract(currentLine);
				if (getFileLayoutConfig().getHeaderFlag().equals(recordType)) {
					totalHeaderRecord++;
				}

				try {
					bufferedWriter.write(currentLine);
					bufferedWriter.newLine();
				}
				catch (IOException e) {
					log.error(e.getMessage(), e);
				}

			}

			// If not found TXN
			if (totalHeaderRecord < 1) {
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.ERROR_MESSAGE_IS_REQUIRE,
								getFileLayoutConfig().getHeaderFlag()));
			}
		}
		catch (IllegalArgumentException e) {
			throw new WrongFormatFileException(CovertErrorConstant.FILE_INVALID_FORMAT);
		}
		catch (IOException e) {
			throw new WrongFormatFileException("File read error occurred", null);
		}
		finally {
			currentLineNo = 1;
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
				if (bufferReader != null) {
					bufferReader.close();
				}
			}
			catch (IOException e) {
				log.error(e.getMessage(), e);
			}

		}

	}

	@Override
	public DetailResult<T> getDetail() {

		DetailResult<T> detailResult = new DetailResult<T>();
		detailResult.setLineNo(++currentLineNo);

		try {

			RecordTypeExtractor headerRecordTypeExtractor = getExtractor(
					RecordType.HEADER);
			RecordTypeExtractor detailRecordTypeExtractor = getExtractor(
					RecordType.DETAIL);

			String currentLine = tempFileReader.readLine();
			if (currentLine == null) {
				detailResult = null;
				tempFileReader.close();
				tempFile.delete();
				return detailResult;
			}

			String headerRecordType = headerRecordTypeExtractor.extract(currentLine);
			if (getFileLayoutConfig().getHeaderFlag().equals(headerRecordType)) {

				Set<DataObserver<?>> detailObservers = getObservers(RecordType.HEADER);
				observeLine(currentLine, detailObservers);
				currentLine = tempFileReader.readLine();
			}

			String detailRecordType = detailRecordTypeExtractor.extract(currentLine);
			if (getFileLayoutConfig().getDetailFlag().equals(detailRecordType)) {

				List<FileLayoutConfigItem> fileLayoutConfigs = getFileLayoutMappingFor(
						RecordType.DETAIL);
				if (currentLine != null) {
					T detailObject = convertDetail(currentLine, fileLayoutConfigs);
					detailResult.setSuccess(true);
					detailResult.setObjectValue(detailObject);
				}
			}

		}
		catch (Exception e) {
			// TODO: handle exception
		}

		return detailResult;

	}

	private void observeLine(String currentLine, Set<DataObserver<?>> detailObservers) {
		if (detailObservers != null) {
			final String detailData = currentLine;
			detailObservers.forEach(observer -> {

				if (observer instanceof SummaryFieldValidator) {

					FileLayoutConfigItem aggregationFieldConfig = observer
							.getObserveFieldConfig();
					String data = getCuttedData(aggregationFieldConfig, detailData);
					try {
						BigDecimal docAmount = getBigDecimalValue(aggregationFieldConfig,
								data);
						if (aggregationFieldConfig.getSignFlagConfig() != null) {

							String signFlagData = getCuttedData(
									aggregationFieldConfig.getSignFlagConfig(),
									detailData);
							docAmount = applySignFlag(docAmount, aggregationFieldConfig,
									signFlagData);

						}
						observer.observe(docAmount);
					}
					catch (Exception e) {
						log.warn(e.getMessage(), e);
					}
				}
				else {
					observer.observe(detailData);
				}

			});
		}
	}

	@Override
	protected int getLengthOfLine(
			List<? extends FileLayoutConfigItem> fileLayoutConfigItems) {
		return super.getLengthOfLine(fileLayoutConfigItems);
	}
}
