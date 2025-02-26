package dictionary.structure;

import dictionary.structure.query.*;
import dictionary.structure.query.operators.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Biword implements DictionaryDataStructure, Serializable, BooleanRetrieval<List<Integer>> {

    private final Map<String, List<Integer>> biword = new HashMap<>();
    private int fileCount;

    @Override
    public void addDocumentTerms(List<String> terms, int docID) {
        for (int i = 1; i < terms.size(); ++i) {
            String currentTerm = terms.get(i);
            addDocumentToTerm(docID, currentTerm);
            String lastTerm = terms.get(i - 1);
            String biwordTerm = lastTerm + "$" + currentTerm;
            addDocumentToTerm(docID, biwordTerm);
        }
        ++fileCount;
    }

    @Override
    public Iterable<Integer> getDocIDsWithTerm(String term) {
        List<Integer> docIds = biword.get(term);
        return docIds == null ? Collections.emptyList() : docIds;
    }

    @Override
    public Iterable<Integer> getDocIDsFromQuery(String query) {
        if (isPhraseQuery(query))
            query = translatePhraseQuery(query);
        QueryEngine<List<Integer>> queryEngine = new QueryEngine<>(this);
        return queryEngine.getDocIDsFromQuery(query);
    }

    @Override
    public void writeToFile(BufferedWriter bufferedWriter) throws IOException {

    }

    @Override
    public BooleanOperators<List<Integer>> getBooleanOperators() {
        return new ListIntegerBooleanOperators(fileCount);
    }

    @Override
    public List<Integer> getTermRawDocIDs(String token) {
        return biword.get(token);
    }

    @Override
    public List<Integer> removeSmallestInSize(Stack<List<Integer>> operands) {
        return operands.pop();
    }

    @Override
    public boolean contains(String term) {
        return biword.containsKey(term);
    }

    private void addDocumentToTerm(int docID, String term) {
        List<Integer> currentTermDocuments = biword.getOrDefault(term, new ArrayList<>());
        if (!documentsHasDocument(currentTermDocuments, docID))
            currentTermDocuments.add(docID);
        biword.put(term, currentTermDocuments);
    }

    private boolean documentsHasDocument(List<Integer> documents, int docID) {
        int index = Collections.binarySearch(documents, docID);
        return index >= 0;
    }

    private String translatePhraseQuery(String query) {
        String[] words = query.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < words.length; ++i) {
            result.append(words[i - 1]).append("$").append(words[i]);
            if (i != words.length - 1) result.append(" & ");
        }
        return result.toString();
    }

    private boolean isPhraseQuery(String query) {
        return (!query.contains("|") && !query.contains("!") && !query.contains("&"));
    }

}