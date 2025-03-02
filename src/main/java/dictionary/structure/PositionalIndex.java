package dictionary.structure;

import dictionary.structure.posting.PositionPosting;
import dictionary.structure.query.BooleanRetrieval;
import dictionary.structure.query.QueryEngine;
import dictionary.structure.query.operators.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class PositionalIndex implements Serializable, DictionaryDataStructure, BooleanRetrieval<List<PositionPosting>> {

    private final Map<String, List<PositionPosting>> positionPostings = new HashMap<>();
    private int fileCount;

    @Override
    public void addDocumentTerms(List<String> terms, int docID) {
        int currentPosition = 0;
        for (String term : terms) {
            List<PositionPosting> positionPostingList = positionPostings.getOrDefault(term, new ArrayList<>());
            updatePostingList(docID, positionPostingList, currentPosition);
            positionPostings.put(term, positionPostingList);
            ++currentPosition;
        }
        ++fileCount;
    }

    @Override
    public Iterable<Integer> getDocIDsWithTerm(String term) {
        List<PositionPosting> positionPostingList = positionPostings.get(term);
        return getDocIDsFromPostings(positionPostingList);
    }

    @Override
    public Iterable<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException {
        QueryEngine<List<PositionPosting>> queryEngine = new QueryEngine<>(this);
        List<PositionPosting> result = queryEngine.getDocIDsFromQuery(query);
        return getDocIDsFromPostings(result);
    }

    @Override
    public void writeToFile(BufferedWriter bufferedWriter) throws IOException {

    }

    @Override
    public BooleanOperators<List<PositionPosting>> getBooleanOperators() {
        return new PostingBooleanOperators(fileCount);
    }

    @Override
    public List<PositionPosting> getTermRawDocIDs(String token) {
        return positionPostings.get(token);
    }

    @Override
    public List<PositionPosting> removeSmallestInSize(Stack<List<PositionPosting>> operands) {
        return operands.pop();
    }

    @Override
    public boolean contains(String term) {
        return positionPostings.containsKey(term);
    }

    private Iterable<Integer> getDocIDsFromPostings(Iterable<PositionPosting> positionPostingList) {
        List<Integer> docIDs = new ArrayList<>();
        for (PositionPosting positionPosting : positionPostingList)
            docIDs.add(positionPosting.getDocID());
        return docIDs;
    }

    private void updatePostingList(int docID, List<PositionPosting> positionPostingList, int currentPosition) {
        int postingIndex = findPostingPosition(positionPostingList, docID);
        if (postingIndex < 0) {
            positionPostingList.add(new PositionPosting(docID));
        } else {
            PositionPosting positionPosting = positionPostingList.get(postingIndex);
            positionPosting.addPosition(currentPosition);
        }
    }

    private int findPostingPosition(List<PositionPosting> positionPostingList, int docID) {
        return Collections.binarySearch(positionPostingList, new PositionPosting(docID));
    }
}