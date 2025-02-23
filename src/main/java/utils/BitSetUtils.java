package utils;

import java.util.BitSet;

public class BitSetUtils {

    public static BitSet toBitSet(String s) {
        BitSet bitSet = new BitSet();
        for (String index : s.replaceAll("[{}\\s]", "").split(","))
            bitSet.set(Integer.parseInt(index));
        return bitSet;
    }

}