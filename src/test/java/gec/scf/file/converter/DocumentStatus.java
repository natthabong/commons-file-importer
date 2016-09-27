package gec.scf.file.converter;

import java.io.Serializable;

public enum DocumentStatus implements Serializable {

	NEW, IN_PROGRESS, USED, BOOKED, WAIT_FOR_BANK_PROCESSING
}
