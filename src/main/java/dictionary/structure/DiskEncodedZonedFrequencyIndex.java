package dictionary.structure;

import operators.BooleanOperators;
import operators.BooleanRetrieval;
import operators.ZonedPostingBooleanOperators;
import posting.PositionPosting;
import posting.ZonedPosting;
import query.QueryEngine;
import query.phraseTranslator.DefaultPhraseTranslator;
import utils.VBE;

import java.io.*;
import java.util.*;

public class DiskEncodedZonedFrequencyIndex implements ZonedDictionaryDataStructure, BooleanRetrieval<List<ZonedPosting>> {

    private static int MAX_BLOCK_SIZE = 4;
    private final StringBuilder terms = new StringBuilder();
    private int[][] postings;
    private int pointer;

    private final String postingPath;

    public DiskEncodedZonedFrequencyIndex(String postingPath) {
        this.postingPath = postingPath;
    }

    public static DiskEncodedZonedFrequencyIndex load(DataInputStream reader) throws IOException {
        int pp = reader.readInt();
        char[] pathBuffer = new char[pp];
        for (int i = 0; i < pp; i++)
            pathBuffer[i] = reader.readChar();
        DiskEncodedZonedFrequencyIndex index = new DiskEncodedZonedFrequencyIndex(new String(pathBuffer));

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
    public void addTerm(String term, long position) {
        throw new UnsupportedOperationException();
    }

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
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ZonedPosting> getDocIDsWithTerm(String term) {
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
    public List<ZonedPosting> getDocIDsFromQuery(String query) throws NoSuchMethodException {
        DefaultPhraseTranslator translator = new DefaultPhraseTranslator();
        String translatedQuery = translator.translate(query);
        QueryEngine<List<ZonedPosting> > queryEngine = new QueryEngine<>(this, null);
        return queryEngine.getDocIDsFromQuery(translatedQuery);
    }

    @Override
    public int uniqueWords() {
        throw new UnsupportedOperationException();
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

    private List<ZonedPosting> getPostings(long position) {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(postingPath)))) {
            dis.skipBytes((int)position);
            int postingSize = dis.readInt();
            byte[] buffer = new byte[postingSize];
            dis.readFully(buffer);
            List<ZonedPosting> result = VBE.decodeZoned(buffer);
            toDocIDs(result);
            return result;
        } catch (IOException ignored) {}
        return null;
    }

    private void toDocIDs(List<ZonedPosting> posting) {
        if (posting.isEmpty()) return;
        ZonedPosting prev = posting.get(0);
        for (int i = 1; i < posting.size(); ++i) {
            ZonedPosting curr = posting.get(i);
            posting.set(i, new ZonedPosting(prev.getDocID() + curr.getDocID(), curr.getZones()));
            prev = posting.get(i);
        }

    }

    @Override
    public BooleanOperators<List<ZonedPosting>> getBooleanOperators() {
        return new ZonedPostingBooleanOperators(0);
    }

    @Override
    public List<ZonedPosting> getTermRawDocIDs(String token) {
        return getDocIDsWithTerm(token);
    }

}