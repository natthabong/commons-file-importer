package gec.scf.file.validator;

public interface ConditionMatchingFactory {

	<T> ConditionMatching<T> create(DataCondition dataCondition, Class<T> objectClass);

}
