package dictionary.structure;

import utils.VBE;

import java.io.*;
import java.util.*;

public class DiskEncodedFrequencyIndex implements DiskDictionaryDataStructure{

    private static int MAX_BLOCK_SIZE = 4;
    private StringBuilder terms = new StringBuilder();
    private int[][] postings;
    private int pointer;

    private final String postingPath;

    public DiskEncodedFrequencyIndex(String postingPath) {
        this.postingPath = postingPath;
    }

    public static DiskEncodedFrequencyIndex load(DataInputStream reader) throws IOException {
//        System.out.println(reader.readInt());

        int pp = reader.readInt();
        char[] pathBuffer = new char[pp];
        for (int i = 0; i < pp; i++)
            pathBuffer[i] = reader.readChar();
        DiskEncodedFrequencyIndex index = new DiskEncodedFrequencyIndex(new String(pathBuffer));

        MAX_BLOCK_SIZE = reader.readInt();
        int postingLength = reader.readInt();

        index.postings = new int[postingLength][MAX_BLOCK_SIZE + 1];
        for (int i = 0; i < postingLength; ++i) {
            for (int j = 0; j < MAX_BLOCK_SIZE + 1; ++j) {
                index.postings[i][j] = reader.readInt();
            }
        }

        int termsLength = reader.readInt();
        for (int i = 0; i < termsLength; ++i) {
            index.terms.append(reader.readChar());
        }
        return index;
    }

    @Override
    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(postingPath.length());
        writer.writeChars(postingPath);
        writer.writeInt(MAX_BLOCK_SIZE);
        writer.writeInt(postings.length);
        System.out.println(postings.length);
        for (int[] block : postings)
            for (int i : block)
                writer.writeInt(i);
        writer.writeInt(terms.length());
        writer.writeChars(terms.toString());
    }

    @Override
    public void addTerm(String term, long position) {}

    @Override
    public void addTerms(Map<String, Integer> terms) {
        ArrayList<String> keys = new ArrayList<>(terms.keySet());
        Collections.sort(keys);

        int tail = terms.size() % MAX_BLOCK_SIZE;
        if (tail > 0)
            for (int i = 0; i < tail; ++i)
                keys.add(keys.get(keys.size() - 1));

        int blockCount = terms.size() / MAX_BLOCK_SIZE;
        postings = new int[blockCount][MAX_BLOCK_SIZE + 1];

        for (int i = 0; i < blockCount; i++) {

            postings[i][0] = this.terms.length();
            int index = i * MAX_BLOCK_SIZE;

            ArrayList<String> block = new ArrayList<>();
            for (int j = 0; j < MAX_BLOCK_SIZE; j++)
                block.add(keys.get(index + j));

            String prefix = longestCommonPrefix(block.get(0), block.get(block.size() - 1));
            this.terms.append((char) prefix.length());
            this.terms.append(prefix);

            for (int j = 0; j < block.size(); j++) {
                String currTerm = block.get(j);
                String suffix = currTerm.substring(prefix.length());
                this.terms.append((char) suffix.length());
                this.terms.append(suffix);
                postings[i][j + 1] = terms.get(currTerm);
            }

        }
    }

    @Override
    public Set<String> allTerms() {
        return null;
    }

    @Override
    public List<Integer> getDocIDsWithTerm(String term) {
        int postingPosition = getPostingPosition(term);
        if (postingPosition == -1) return new ArrayList<>();
        return getPostings(postingPosition);
    }

    private int getPostingPosition(String term) {
        int lo = 0;
        int hi = postings.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = compareTermToBlock(term, postings[mid]);
            if (cmp > 0) {
                hi = mid - 1;
            } else if (cmp < 0) {
                lo = mid + 1;
            } else {
                return getPostingPositionInBlock(term, postings[mid]);
            }
        }
        return -1;
    }

    private int compareTermToBlock(String term, int[] block) {
        pointer = block[0];
        int prefixLength = readCharFromPointer();
        String prefix = readStringFromPointer(prefixLength);
        for (int i = 0; i < MAX_BLOCK_SIZE; ++i) {
            int suffixLength = readCharFromPointer();
            String suffix = readStringFromPointer(suffixLength);
            String currTerm = prefix + suffix;
            if (term.equals(currTerm))
                return 0;
            if (i == MAX_BLOCK_SIZE - 1)
                return currTerm.compareTo(term);
        }
        return -1;
    }

    private int getPostingPositionInBlock(String term, int[] block) {
        pointer = block[0];
        int prefixLength = readCharFromPointer();
        String prefix = readStringFromPointer(prefixLength);
        for (int i = 0; i < MAX_BLOCK_SIZE; ++i) {
            int suffixLength = readCharFromPointer();
            String suffix = readStringFromPointer(suffixLength);
            if (term.equals(prefix + suffix))
                return block[i + 1];
        }
        return -1;
    }

    private char readCharFromPointer() {
        return terms.charAt(pointer++);
    }

    private String readStringFromPointer(int n) {
        int start = pointer;
        pointer += n;
        return terms.substring(start, pointer);
    }

    @Override
    public List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException {
        return List.of();
    }

    @Override
    public int uniqueWords() {
        return 0;
    }

    private String longestCommonPrefix(String term1, String term2) {
        StringBuilder sb = new StringBuilder();
        int length = Math.min(term1.length(), term2.length());
        for (int i = 0; i < length; i++) {
            if (term1.charAt(i) != term2.charAt(i))
                return sb.toString();
            sb.append(term1.charAt(i));
        }
        return sb.toString();
    }

    private List<Integer> getPostings(long position) {
//        File postingFile = new File(postingPath);
//        try (RandomAccessFile raf = new RandomAccessFile(postingFile, "r")) {
//            raf.seek(position);
//            List<Integer> posting = new ArrayList<>();
//            int postingSize = raf.readInt();
//            for (int i = 0; i < postingSize; ++i)
//                posting.add(raf.readInt());
//            return posting;
//        } catch (IOException ignored) {}
//        return null;
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(postingPath)))) {
            dis.skipBytes((int)position);
            int postingSize = dis.readInt();
            byte[] buffer = new byte[postingSize];
            dis.readFully(buffer);
            List<Integer> result = VBE.decode(buffer);
            toDocIDs(result);
            return result;
        } catch (IOException ignored) {}
        return null;
    }

    private void toDocIDs(List<Integer> posting) {
        if (posting.isEmpty()) return;
        int previous = posting.get(0);
        for (int i = 1; i < posting.size(); i++) {
            int current = posting.get(i);
            posting.set(i, previous + current);
            previous = previous + current;
        }
    }

}