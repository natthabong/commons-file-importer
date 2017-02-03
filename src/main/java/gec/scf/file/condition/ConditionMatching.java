package gec.scf.file.condition;

import gec.scf.file.condition.exception.ConditionMismatchException;

public interface ConditionMatching<T> {

	void match(T object, DataCondition condition) throws ConditionMismatchException;

	ConditionType getConditionType();

}
