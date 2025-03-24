package dictionary.structure;

import utils.VBE;

import java.io.*;
import java.util.*;

public class DiskFrequencyIndex implements DiskDictionaryDataStructure{

    private final Map<String, Long> terms = new HashMap<>();
    private final String postingPath;

    public DiskFrequencyIndex(String postingPath) {
        this.postingPath = postingPath;
    }

    public static DiskFrequencyIndex load(DataInputStream reader) throws IOException {
        int pathLength = reader.readInt();
        char[] pathBuffer = new char[pathLength];
        for (int i = 0; i < pathLength; i++)
            pathBuffer[i] = reader.readChar();
        String path = new String(pathBuffer);
        DiskFrequencyIndex index = new DiskFrequencyIndex(path);
        int termCount = reader.readInt();
        for (int i = 0; i < termCount; i++) {
            int termLength = reader.readInt();
            char[] buffer = new char[termLength];
            for (int j = 0; j < termLength; j++)
                buffer[j] = reader.readChar();
            String term = new String(buffer);
            long termPosition = reader.readLong();
            index.terms.put(term, termPosition);
        }
        return index;
    }

    @Override
    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(postingPath.length());
        writer.writeChars(postingPath);
        writer.writeInt(terms.size());
        for (Map.Entry<String, Long> entry : terms.entrySet()) {
            String term = entry.getKey();
            writer.writeInt(term.length());
            writer.writeChars(term);
            writer.writeLong(entry.getValue());
        }
    }

    public void addTerm(String term, long position) {
        terms.put(term, position);
    }

    @Override
    public void addTerms(Map<String, Integer> terms) {
//        this.terms.putAll(terms);
    }

    @Override
    public List<Integer> getDocIDsWithTerm(String term) {
        long position = terms.get(term);
        List<Integer> postings = getPostings(position);
        return postings == null ? new ArrayList<>() : postings;
    }

    public Set<String> allTerms() {
        return terms.keySet();
    }

    @Override
    public List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException {
        return List.of();
    }

    @Override
    public int uniqueWords() {
        return terms.size();
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