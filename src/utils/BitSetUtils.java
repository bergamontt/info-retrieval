package utils;

import java.util.BitSet;

public class BitSetUtils {

    public static BitSet toBitSet(String s) {
        int len = s.length();
        BitSet bs = new BitSet();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '1')
                bs.set(i);
        }
        return bs;
    }

}