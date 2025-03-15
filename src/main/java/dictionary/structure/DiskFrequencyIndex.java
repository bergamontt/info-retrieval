package dictionary.structure;

import java.io.*;
import java.util.*;

public class DiskFrequencyIndex implements DiskDictionaryDataStructure{

    private final Map<String, Long> terms = new HashMap<>();
    private final String postingPath;

    public DiskFrequencyIndex(String postingPath) {
        this.postingPath = postingPath;
    }

    public static DiskFrequencyIndex load(BufferedReader br) throws IOException {
        DiskFrequencyIndex loadedIndex = new DiskFrequencyIndex(br.readLine());
        int termsCount = Integer.parseInt(br.readLine());
        for (int i = 0; i < termsCount; ++i) {
            String[] tokens = br.readLine().split(" ");
            loadedIndex.addTerm(tokens[0], Long.parseLong(tokens[1]));
        }
        return loadedIndex;
    }

    public void addTerm(String term, long position) {
        terms.put(term, position);
    }

    @Override
    public List<Integer> getDocIDsWithTerm(String term) {
        long position = terms.get(term);
        String postingLine = getPostingLine(position);
        if (postingLine == null) return new ArrayList<>();
        return getPostingList(postingLine);
    }

    @Override
    public List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException {
        return List.of();
    }

    @Override
    public void writeToFile(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(postingPath + "\n");
        bufferedWriter.write(terms.size() + "\n");
        for (Map.Entry<String, Long> entry : terms.entrySet())
            bufferedWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
    }

    private String getPostingLine(long position) {
        File postingFile = new File(postingPath);
        try (RandomAccessFile raf = new RandomAccessFile(postingFile, "r")) {
            raf.seek(position);
            return raf.readLine();
        } catch (IOException ignored) {}
        return null;
    }

    private List<Integer> getPostingList(String postingLine) {
        String[] tokens = postingLine.split(" ");
        List<Integer> postingList = new ArrayList<>();
        for (String token : tokens)
            if (!token.isEmpty())
                postingList.add(Integer.parseInt(token));
        return postingList;
    }

}