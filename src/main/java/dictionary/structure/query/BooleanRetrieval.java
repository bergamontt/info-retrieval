package dictionary.structure.query;

import dictionary.structure.query.operators.BooleanOperators;

public interface BooleanRetrieval<T> {

    BooleanOperators<T> getBooleanOperators();
    T getTermRawDocIDs(String token);
    boolean contains(String term);
}