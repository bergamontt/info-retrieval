package dictionary.structure.query.operators;

public interface BooleanOperators<T> {

    T negate(T operand);
    T intersect(T operand1, T operand2);
    T concatenate(T operand1, T operand2);

}