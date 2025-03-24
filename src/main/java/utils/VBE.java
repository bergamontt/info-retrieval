package utils;

import java.util.ArrayList;
import java.util.List;

public class VBE {

    public static byte[] encode(List<Integer> numbers) {
        List<Byte> encodedBytes = new ArrayList<>();
        for (int number : numbers) {
            while (true) {
                byte currentByte = (byte) (number & 0x7F);
                number >>>= 7;
                if (number == 0) {
                    currentByte |= 0x80;
                    encodedBytes.add(currentByte);
                    break;
                }
                encodedBytes.add(currentByte);
            }
        }
        byte[] result = new byte[encodedBytes.size()];
        for (int i = 0; i < encodedBytes.size(); i++)
            result[i] = encodedBytes.get(i);
        return result;
    }

    public static List<Integer> decode(byte[] encodedBytes) {
        List<Integer> numbers = new ArrayList<>();
        int number = 0;
        int shift = 0;
        for (byte b : encodedBytes) {
            if ((b & 0x80) != 0) {
                number |= (b & 0x7F) << shift;
                numbers.add(number);
                number = 0;
                shift = 0;
            } else {
                number |= (b & 0x7F) << shift;
                shift += 7;
            }
        }
        return numbers;
    }


}