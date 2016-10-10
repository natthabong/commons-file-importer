package gec.scf.file.converter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataMerge {

	private List<? extends DataReference> dataReference;

	public DataMerge(List<? extends DataReference> list) {
		setDataReference(list);
	}

	public List<? extends DataReference> getDataReference() {
		return dataReference;
	}

	public void setDataReference(List<? extends DataReference> list) {
		this.dataReference = list;
	}

	public <T extends Object> T merge(T detailObject, Class<? extends Object> classObject)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchFieldException, SecurityException, ParseException {
		for (DataReference dataReference : dataReference) {
			DataFinder dataFinder = dataReference.getDataFinder();
			String result = dataFinder.find(detailObject);

			Field field = classObject.getDeclaredField(dataReference.getFieldName());
			Class<?> classType = field.getType();
			field.setAccessible(true);

			if (classType.isAssignableFrom(Date.class)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
				Date date = sdf.parse(result);
				field.set(detailObject, date);
			}
			else if (classType.isAssignableFrom(BigDecimal.class)) {
				BigDecimal valueAmount = new BigDecimal(result);
				field.set(detailObject, valueAmount);
			}
			else {
				field.set(detailObject, result.trim());
			}

		}
		return detailObject;
	}
}
