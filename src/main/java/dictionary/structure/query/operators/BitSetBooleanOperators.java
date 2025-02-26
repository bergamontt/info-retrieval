package dictionary.structure.query.operators;

import java.util.BitSet;

public class BitSetBooleanOperators implements BooleanOperators<BitSet> {

    @Override
    public BitSet negate(BitSet operand) {
        BitSet result = (BitSet) operand.clone();
        result.flip(0, operand.length());
        return result;
    }

    @Override
    public BitSet intersect(BitSet operand1, BitSet operand2) {
        BitSet result = (BitSet) operand1.clone();
        result.and(operand2);
        return result;
    }

    @Override
    public BitSet concatenate(BitSet operand1, BitSet operand2) {
        BitSet result = (BitSet) operand1.clone();
        result.or(operand2);
        return result;
    }

}
