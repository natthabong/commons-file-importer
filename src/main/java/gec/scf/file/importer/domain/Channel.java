package gec.scf.file.importer.domain;

public enum Channel {
	WEB(Values.WEB), FTP(Values.FTP), EMAIL(Values.EMAIL);

	private Channel(String val) {
		// force equality between name of enum instance, and value of constant
		if (!this.name().equals(val))
			throw new IllegalArgumentException("Incorrect use of Channel");
	}

	public static class Values {
		public static final String WEB = "WEB";
		public static final String FTP = "FTP";
		public static final String EMAIL = "EMAIL";
	}
}
