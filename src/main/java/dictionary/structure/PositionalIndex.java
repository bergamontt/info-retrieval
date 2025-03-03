package dictionary.structure;

import dictionary.structure.posting.PositionPosting;
import dictionary.structure.query.BooleanRetrieval;
import dictionary.structure.query.QueryEngine;
import dictionary.structure.query.operators.*;
import utils.QueryUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class PositionalIndex implements DictionaryDataStructure, BooleanRetrieval<List<PositionPosting>> {

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
        if (QueryUtils.isPhraseQuery(query))
            query = translatePhraseQuery(query);
        QueryEngine<List<PositionPosting>> queryEngine = new QueryEngine<>(this);
        List<PositionPosting> result = queryEngine.getDocIDsFromQuery(query);
        return getDocIDsFromPostings(result);
    }

    @Override
    public void writeToFile(BufferedWriter fileWriter) throws IOException {
        fileWriter.write("posindex\n");
        fileWriter.write(fileCount + "\n");
        fileWriter.write(positionPostings.size() + "\n");
        for(String term : positionPostings.keySet()) {
            fileWriter.write(term + " ");
            for(PositionPosting posting : positionPostings.get(term))
                fileWriter.write(posting + " ");
            fileWriter.write("\n");
        }
    }

    public static PositionalIndex readFromFile(BufferedReader fileReader) throws IOException {
        PositionalIndex index = new PositionalIndex();
        index.fileCount = Integer.parseInt(fileReader.readLine());
        int termCount = Integer.parseInt(fileReader.readLine());
        for(int i = 0; i < termCount; ++i) {
            String[] termInfo = fileReader.readLine().split(" ");
            String term = termInfo[0];
            List<PositionPosting> positionPostings = new ArrayList<>();
            for (int j = 1; j < termInfo.length; ++j)
                positionPostings.add(PositionPosting.parsePosition(termInfo[j]));
            index.positionPostings.put(term, positionPostings);
        }
        return index;
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

    private String translatePhraseQuery(String query) {
        String[] tokens = query.split(" ");
        StringBuilder result = new StringBuilder(tokens[0]);
        for (int i = 1; i < tokens.length; i++)
            result.append(" ").append(tokens[i]).append(" /").append(1);
        return result.toString();
    }

}