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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gec.scf.file.configuration.FileLayoutConfig;
import gec.scf.file.configuration.FileLayoutConfigItem;
import gec.scf.file.configuration.RecordType;
import gec.scf.file.exception.WrongFormatDetailException;
import gec.scf.file.exception.WrongFormatFileException;
import gec.scf.file.importer.DetailResult;
import gec.scf.file.importer.ErrorLineDetail;
import gec.scf.file.validation.SummaryFieldValidator;

public class FixedLengthFileConverter<T> extends AbstractFileConverter<T> {

	private static final Logger log = Logger.getLogger(FixedLengthFileConverter.class);

	protected int currentLineNo;

	private File tempFile;

	private BufferedReader tempFileReader;

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz,
			FieldValidatorFactory fieldValidatorFactory) {
		super(fileLayoutConfig, clazz, fieldValidatorFactory);
	}

	public FixedLengthFileConverter(FileLayoutConfig fileLayoutConfig, Class<T> clazz) {
		this(fileLayoutConfig, clazz, null);
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

			tempFile = File.createTempFile("DOCUMENT_T_", ".temp");
			bufferedWriter = new BufferedWriter(new FileWriter(tempFile));

			InputStream tempFileContent = new FileInputStream(tempFile);

			tempFileReader = new BufferedReader(
					new InputStreamReader(tempFileContent, "UTF-8"));

			bufferReader = new BufferedReader(
					new InputStreamReader(fileContent, "UTF-8"));

			RecordTypeExtractor headerRecordTypeExtractor = getExtractor(
					RecordType.HEADER);
			RecordTypeExtractor footerRecordTypeExtractor = getExtractor(
					RecordType.FOOTER);

			boolean hasCheckedHeader = false;
			boolean hasCheckedFooter = false;

			String currentLine;
			while ((currentLine = bufferReader.readLine()) != null) {

				// When found a blank line
				if (StringUtils.isBlank(currentLine)) {

					// Found a blank line on the end of file, do nothing
					if (hasCheckedHeader && hasCheckedFooter) {
						continue;
					}
					else if (!hasCheckedHeader) {

						// Found a blank line on top of file, throw an error
						FileLayoutConfigItem headerRecordType = headerRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(MessageFormat.format(
								CovertErrorConstant.HEADER_NOT_FIRST_LINE_OF_FILE,
								headerRecordType.getDisplayValue(),
								getFileLayoutConfig().getHeaderFlag()));
					}
					else if (!hasCheckedFooter) {

						// Found a blank line between header and detail, throw
						// an error
						throw new WrongFormatFileException(
								CovertErrorConstant.FILE_INVALID_FORMAT);
					}
				}

				// Enter header
				if (!hasCheckedHeader) {

					String recordType = headerRecordTypeExtractor.extract(currentLine);

					if (StringUtils.isBlank(recordType.trim())) {
						// Found a blank recoredId
						FileLayoutConfigItem headerRecordType = headerRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(MessageFormat.format(
								CovertErrorConstant.HEADER_NOT_FIRST_LINE_OF_FILE,
								headerRecordType.getDisplayValue(),
								getFileLayoutConfig().getHeaderFlag()));
					}
					else if (getFileLayoutConfig().getHeaderFlag().equals(recordType)) {

						List<FileLayoutConfigItem> headerConfigItems = getFileLayoutMappingFor(
								RecordType.HEADER);

						validateHeader(currentLine, headerConfigItems);

						hasCheckedHeader = true;
						continue;
					}
					else {

						FileLayoutConfigItem recordTypeLayoutCofig = headerRecordTypeExtractor
								.getConfig();

						throw new WrongFormatFileException(MessageFormat.format(
								CovertErrorConstant.RECORD_ID_MISS_MATCH,
								recordTypeLayoutCofig.getDisplayValue(), recordType));
					}
				}

				// Enter footer
				if (!hasCheckedFooter) {

					String recordType = footerRecordTypeExtractor.extract(currentLine);
					if (StringUtils.isBlank(recordType.trim())) {
						// Found a blank recoredId
						FileLayoutConfigItem footerRecordType = footerRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(
								MessageFormat.format(
										CovertErrorConstant.FOOTER_NOT_LAST_LINE_OF_FILE,
										footerRecordType.getDisplayValue(),
										getFileLayoutConfig().getFooterFlag()),
								currentLineNo);
					}
					else if (getFileLayoutConfig().getFooterFlag().equals(recordType)) {
						List<FileLayoutConfigItem> footerConfigItems = getFileLayoutMappingFor(
								RecordType.FOOTER);

						validateFooter(currentLine, footerConfigItems);
						hasCheckedFooter = true;
						continue;
					}
				}

				RecordTypeExtractor detailRecordTypeExtractor = getExtractor(
						RecordType.DETAIL);

				String recordType = detailRecordTypeExtractor.extract(currentLine);

				// Validate Detail
				if (getFileLayoutConfig().getDetailFlag().equals(recordType)) {

					if (hasCheckedFooter) {

						FileLayoutConfigItem detialRecordTypeConfig = detailRecordTypeExtractor
								.getConfig();
						throw new WrongFormatFileException(
								MessageFormat.format(
										CovertErrorConstant.FOOTER_NOT_LAST_FILE,
										detialRecordTypeConfig.getDisplayValue(),
										getFileLayoutConfig().getFooterFlag()),
								currentLineNo);
					}

					// Enter detail
					List<FileLayoutConfigItem> detailConfigItems = getFileLayoutMappingFor(
							RecordType.DETAIL);
					//
					validateLineDataLength(currentLine, detailConfigItems);
					try {
						bufferedWriter.write(currentLine);
						bufferedWriter.newLine();
					}
					catch (IOException e) {
						log.error(e.getMessage(), e);
					}

					Set<DataObserver<?>> detailObservers = getObservers(
							RecordType.DETAIL);

					if (detailObservers != null) {
						final String detailData = currentLine;
						detailObservers.forEach(observer -> {

							FileLayoutConfigItem aggregationFieldConfig = observer
									.getObserveFieldConfig();

							String data = detailData;
							if (aggregationFieldConfig != null) {
								try {
									data = getCuttedData(aggregationFieldConfig,
											detailData);
								}
								catch (WrongFormatFileException e) {
									log.error(e.getErrorMessage(), e);
								}
							}

							if (observer instanceof SummaryFieldValidator) {

								try {
									BigDecimal docAmount = getBigDecimalValue(
											aggregationFieldConfig, data);
									if (aggregationFieldConfig
											.getSignFlagConfig() != null) {

										String signFlagData = getCuttedData(
												aggregationFieldConfig
														.getSignFlagConfig(),
												detailData);
										docAmount = applySignFlag(docAmount,
												aggregationFieldConfig, signFlagData);

									}
									observer.observe(docAmount);
								}
								catch (Exception e) {
									log.warn(e.getMessage(), e);
								}
							}
							else {
								observer.observe(data);
							}

						});
					}

				}
				else {
					FileLayoutConfigItem recordTypeLayoutCofig = detailRecordTypeExtractor
							.getConfig();
					throw new WrongFormatFileException(
							MessageFormat.format(CovertErrorConstant.RECORD_ID_MISS_MATCH,
									recordTypeLayoutCofig.getDisplayValue(), recordType));
				}

			}

			if (!hasCheckedFooter) {

				FileLayoutConfigItem recordTypeLayoutCofig = footerRecordTypeExtractor
						.getConfig();
				throw new WrongFormatFileException(
						MessageFormat.format(CovertErrorConstant.FOOTER_NOT_LAST_FILE,
								recordTypeLayoutCofig.getDisplayValue(),
								getFileLayoutConfig().getFooterFlag()));
			}
		}
		catch (IllegalArgumentException e) {
			throw new WrongFormatFileException(CovertErrorConstant.FILE_INVALID_FORMAT);
		}
		catch (WrongFormatFileException e) {
			e.setErrorLineNo(null);
			throw e;
		}
		catch (WrongFormatDetailException e) {
			throw e;
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

	protected void validateHeader(String currentLine,
			List<FileLayoutConfigItem> footerConfigItems)
			throws WrongFormatFileException {
		try {
			validateLineDataFormat(currentLine, footerConfigItems);
		}
		catch (WrongFormatFileException e) {
			e.setErrorLineNo(currentLineNo);
			throw e;
		}
		catch (WrongFormatDetailException e) {
			throw new WrongFormatFileException(e.getErrorMessage(), currentLineNo);
		}

	}

	private void validateFooter(String currentLine,
			List<FileLayoutConfigItem> footerConfigItems)
			throws WrongFormatFileException {
		try {
			validateLineDataFormat(currentLine, footerConfigItems);
		}
		catch (WrongFormatFileException e) {
			e.setErrorLineNo(currentLineNo);
			throw e;
		}
		catch (WrongFormatDetailException e) {
			throw new WrongFormatFileException(e.getErrorMessage(), currentLineNo);
		}

	}

	protected void validateLineDataLength(Object currentLine,
			List<FileLayoutConfigItem> configItems) throws WrongFormatFileException {
		String lineData = String.valueOf(currentLine);
		int lineLength = getLengthOfLine(configItems);
		if (lineLength != StringUtils.length(lineData)) {
			throw new WrongFormatFileException(
					MessageFormat.format(CovertErrorConstant.DATA_LENGTH_OVER,
							StringUtils.length(lineData), lineLength));
		}
	}

	protected int getLengthOfLine(
			List<? extends FileLayoutConfigItem> fileLayoutConfigItems) {
		int result = 0;
		for (FileLayoutConfigItem item : fileLayoutConfigItems) {
			if (item.getStartIndex() == null) {
				continue;
			}
			int length = (item.getStartIndex() - 1) + item.getLenght();
			if (result <= length)
				result = length;
		}
		return result;
	}

	@Override
	public DetailResult<T> getDetail() {

		DetailResult<T> detailResult = new DetailResult<T>();

		detailResult.setLineNo(++currentLineNo);
		try {

			List<FileLayoutConfigItem> fileLayoutConfigs = getFileLayoutMappingFor(
					RecordType.DETAIL);

			String currentLine = tempFileReader.readLine();

			if (currentLine != null) {
				T detailObject = convertDetail(currentLine, fileLayoutConfigs);
				detailResult.setSuccess(true);
				detailResult.setObjectValue(detailObject);
			}
			else {
				detailResult = null;
				tempFileReader.close();
				tempFile.delete();
			}
		}
		catch (WrongFormatDetailException e) {
			detailResult.setErrorLineDetails(e.getErrorLineDetails());
			detailResult.setSuccess(false);
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return detailResult;
	}

	protected T convertDetail(String currentLine,
			List<? extends FileLayoutConfigItem> fileLayoutConfigs) {

		T entity = null;
		try {
			entity = (T) getEntityClass().newInstance();
		}
		catch (InstantiationException e1) {
			e1.printStackTrace();
		}
		catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		List<ErrorLineDetail> errorLineDetails = new ArrayList<ErrorLineDetail>();

		boolean isError = false;
		for (FileLayoutConfigItem config : fileLayoutConfigs) {

			try {
				applyObjectValue(entity, config, currentLine);

			}
			catch (WrongFormatDetailException e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLineNo);
				errorLineDetail.setErrorMessage(e.getErrorMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
			}
			catch (Exception e) {
				ErrorLineDetail errorLineDetail = new ErrorLineDetail();
				errorLineDetail.setErrorLineNo(currentLineNo);
				errorLineDetail.setErrorMessage(e.getMessage());
				errorLineDetails.add(errorLineDetail);
				isError = true;
				e.printStackTrace();
			}
		}
		if (isError) {
			throw new WrongFormatDetailException(errorLineDetails);
		}
		return entity;
	}

	@Override
	String getCuttedData(FileLayoutConfigItem configItem, Object currentLine)
			throws WrongFormatFileException {
		int start = configItem.getStartIndex() - 1;
		int end = (configItem.getStartIndex() + configItem.getLenght()) - 1;
		String result = "";
		try {
			result = String.valueOf(currentLine).substring(start, end);
		}
		catch (IndexOutOfBoundsException e) {
			if (configItem.isRequired()) {
				throw new WrongFormatFileException();
			}
		}
		return result;
	}
}
