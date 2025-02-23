package dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class InvertedIndex implements Serializable, DictionaryDataStructure, BooleanRetrieval<List<Integer>> {

    Map<String, List<Integer>> invertedIndex = new HashMap<>();
    private int fileCount = 0;

    public Iterable<String> getTerms() {
        return invertedIndex.keySet();
    }

    public void addDocumentTerms(List<String> terms, int docID) {
        for (String term : terms) {
            List<Integer> documents = invertedIndex.getOrDefault(term, new ArrayList<>());
            if (documentsHasDocument(documents, docID)) continue;
            documents.add(docID);
            invertedIndex.put(term, documents);
        }
        ++fileCount;
    }

    public Iterable<Integer> getDocIDsWithTerm(String term) {
        return invertedIndex.get(term);
    }

    @Override
    public Iterable<Integer> getDocIDsFromQuery(String query) {
        QueryEngine<List<Integer>> queryEngine = new QueryEngine<>(this);
        return queryEngine.getDocIDsFromQuery(query);
    }

    public void writeToFile(BufferedWriter fileWriter) throws IOException {
        fileWriter.write("index\n");
        fileWriter.write(fileCount + "\n");
        fileWriter.write(invertedIndex.size() + "\n");
        for (String term : invertedIndex.keySet()) {
            fileWriter.write(term + " ");
            List<Integer> documents = invertedIndex.get(term);
            for (int docID : documents)
                fileWriter.write(docID + " ");
            fileWriter.write("\n");
        }
    }

    public static InvertedIndex readFromFile(BufferedReader fileReader) throws IOException {
        InvertedIndex index = new InvertedIndex();
        index.fileCount = Integer.parseInt(fileReader.readLine());
        int termCount = Integer.parseInt(fileReader.readLine());
        for (int i = 0; i < termCount; ++i) {
            String[] termInfo = fileReader.readLine().split(" ");
            List<Integer> docIDs = new ArrayList<>();
            for (int j = 1; j < termInfo.length; ++j)
                docIDs.add(Integer.parseInt(termInfo[j]));
            index.invertedIndex.put(termInfo[0], docIDs);
        }
        return index;
    }

    @Override
    public List<Integer> intersect(List<Integer> p1, List<Integer> p2) {
        List<Integer> result = new ArrayList<>();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            int docID1 = p1.get(i1);
            int docID2 = p2.get(i2);
            if (docID1 == docID2) {
                result.add(docID1);
                ++i1; ++i2;
            } else if (docID1 < docID2) ++i1;
            else ++i2;
        }
        return result;
    }

    @Override
    public List<Integer> negate(List<Integer> p1) {
        List<Integer> result = new ArrayList<>();
        int lastDocID = -1;
        for (int docID : p1) {
            for (int i = lastDocID + 1; i < docID; ++i)
                result.add(i);
            lastDocID = docID;
        }
        for (int i = lastDocID + 1; i < fileCount; ++i)
            result.add(i);
        return result;
    }

    @Override
    public List<Integer> concatenate(List<Integer> p1, List<Integer> p2) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < p1.size() || j < p2.size()) {
            int currentValue;
            if (i < p1.size() && (j >= p2.size() || p1.get(i) < p2.get(j)))
                currentValue = p1.get(i++);
            else currentValue = p2.get(j++);
            if (!documentsHasDocument(result, currentValue))
                result.add(currentValue);
        }
        return result;
    }

    @Override
    public List<Integer> getTermRawDocIDs(String token) {
        return invertedIndex.get(token);
    }

    @Override
    public List<Integer> removeSmallestInSize(Stack<List<Integer>> operands) {
        List<Integer> smallest = operands.peek();
        for (List<Integer> operand : operands)
            if (smallest.size() > operand.size())
                smallest = operand;
        operands.remove(smallest);
        return smallest;
    }

    private boolean documentsHasDocument(List<Integer> documents, int docID) {
        int index = Collections.binarySearch(documents, docID);
        return index >= 0;
    }

}