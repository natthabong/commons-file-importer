package gec.scf.file.validator;

import gec.scf.file.validator.exception.ConditionMismatchException;

public interface ConditionMatching<T> {

	void match(T object, DataCondition condition) throws ConditionMismatchException;

	ConditionType getConditionType();

}
