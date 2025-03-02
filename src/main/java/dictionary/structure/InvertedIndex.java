package dictionary.structure;

import dictionary.structure.query.BooleanRetrieval;
import dictionary.structure.query.QueryEngine;
import dictionary.structure.query.operators.BooleanOperators;
import dictionary.structure.query.operators.ListIntegerBooleanOperators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class InvertedIndex implements Serializable, DictionaryDataStructure, BooleanRetrieval<List<Integer>> {

    Map<String, List<Integer>> invertedIndex = new HashMap<>();
    private int fileCount;

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
        List<Integer> docIds = invertedIndex.get(term);
        return docIds == null ? Collections.emptyList() : docIds;
    }

    @Override
    public Iterable<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException {
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
    public BooleanOperators<List<Integer>> getBooleanOperators() {
        return new ListIntegerBooleanOperators(fileCount);
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

    @Override
    public boolean contains(String term) {
        return invertedIndex.containsKey(term);
    }

    private boolean documentsHasDocument(List<Integer> documents, int docID) {
        int index = Collections.binarySearch(documents, docID);
        return index >= 0;
    }

}