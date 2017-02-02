package gec.scf.file.common;

import java.time.LocalDate;

public class DateTimeImpl implements DateTimeProvider{

	@Override
	public LocalDate getNow() {
		return LocalDate.now();
	}

}
