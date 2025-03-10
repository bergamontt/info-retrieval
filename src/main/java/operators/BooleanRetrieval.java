package operators;

public interface BooleanRetrieval<T> {

    BooleanOperators<T> getBooleanOperators();
    T getTermRawDocIDs(String token);
}