package gec.scf.file.condition;

public interface ConditionMatchingProvider {

	<T> ConditionMatching<T> create(DataCondition dataCondition, Class<T> objectClass);

}
