package gec.scf.file.condition;

@Deprecated
public interface ConditionMatchingProvider {

	<T> ConditionMatching<T> create(DataCondition dataCondition, Class<T> objectClass);

}
