package utils;

import posting.ZonedPosting;

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

    public static byte[] encodeZoned(List<ZonedPosting> postings) {
        List<Byte> encodedBytes = new ArrayList<>();
        for (ZonedPosting zp : postings) {
            int number = zp.getDocID();
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
            byte zoneByte = 0;
            for (int i = 0; i < 3; i++) {
                if (zp.getZones()[i]) {
                    zoneByte |= (1 << i); // Set bit i
                }
            }
            encodedBytes.add(zoneByte);
        }
        byte[] result = new byte[encodedBytes.size()];
        for (int i = 0; i < encodedBytes.size(); i++)
            result[i] = encodedBytes.get(i);
        return result;
    }

    public static List<ZonedPosting> decodeZoned(byte[] encodedBytes) {
        List<ZonedPosting> postings = new ArrayList<>();
        int i = 0;
        while (i < encodedBytes.length) {
            int number = 0;
            int shift = 0;
            while (true) {
                byte b = encodedBytes[i++];
                number |= (b & 0x7F) << shift;
                if ((b & 0x80) != 0) break;
                shift += 7;
            }
            byte zoneByte = encodedBytes[i++];
            boolean[] zones = new boolean[3];
            for (int j = 0; j < 3; j++) {
                zones[j] = (zoneByte & (1 << j)) != 0;
            }
            postings.add(new ZonedPosting(number, zones));
        }
        return postings;
    }

}