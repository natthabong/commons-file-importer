package gec.scf.file.validator;

import gec.scf.file.validator.exception.ConditionMismatchException;

public interface ConditionMatching<T> {

	void match(T object) throws ConditionMismatchException;

	ConditionType getConditionType();

}
