package operators;

import java.util.Stack;

public interface BooleanOperators<T> {

    T negate(T operand);
    T intersect(T operand1, T operand2);
    T positionalIntersect(T operand1, T operand2, int k) throws NoSuchMethodException;
    T concatenate(T operand1, T operand2);
    T getSmallest(Stack<T> stack);
}