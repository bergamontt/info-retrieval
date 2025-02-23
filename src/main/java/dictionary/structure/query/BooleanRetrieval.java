package dictionary.structure.query;

import java.util.Stack;

public interface BooleanRetrieval<T> {

    T negate(T operand);
    T intersect(T operand1, T operand2);
    T concatenate(T operand1, T operand2);
    T getTermRawDocIDs(String token);
    T removeSmallestInSize(Stack<T> operands);
    boolean contains(String term);
}