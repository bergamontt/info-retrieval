package dictionary.structure.query;

import dictionary.structure.query.operators.BooleanOperators;

import java.util.Stack;

public interface BooleanRetrieval<T> {

    BooleanOperators<T> getBooleanOperators();
    T getTermRawDocIDs(String token);
    T removeSmallestInSize(Stack<T> operands);
    boolean contains(String term);
}