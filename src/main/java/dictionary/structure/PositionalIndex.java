package dictionary.structure;

import dictionary.termIndexer.*;
import parser.Normalizer;
import posting.PositionPosting;
import operators.BooleanRetrieval;
import query.QueryEngine;
import operators.BooleanOperators;
import operators.PostingBooleanOperators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class PositionalIndex implements DictionaryDataStructure, BooleanRetrieval<List<PositionPosting>> {

    private final Map<String, List<PositionPosting>> positionPostings = new HashMap<>();
    private final TermIndexer termIndexer = new PermutermIndexer();
    private int fileCount;

    @Override
    public void addDocumentTerms(List<String> terms, int docID) {
        termIndexer.addTerms(terms);
        normalizeAndAddTerms(terms, docID);
        ++fileCount;
    }

    @Override
    public List<Integer> getDocIDsWithTerm(String term) {
        List<PositionPosting> positionPostingList = positionPostings.get(term);
        return getDocIDsFromPostings(positionPostingList);
    }

    @Override
    public List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException {
        QueryEngine<List<PositionPosting>> queryEngine = new QueryEngine<>(this, termIndexer);
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
            List<PositionPosting> positionPostings = new ArrayList<>();
            for (int j = 1; j < termInfo.length; ++j)
                positionPostings.add(PositionPosting.parsePosition(termInfo[j]));
            index.positionPostings.put(termInfo[0], positionPostings);
        }
        return index;
    }

    @Override
    public BooleanOperators<List<PositionPosting>> getBooleanOperators() {
        return new PostingBooleanOperators(termIndexer, fileCount);
    }

    @Override
    public List<PositionPosting> getTermRawDocIDs(String token) {
        return positionPostings.getOrDefault(token, new ArrayList<>());
    }

    private void normalizeAndAddTerms(List<String> terms, int docID) {
        Normalizer normalizer = new Normalizer(terms);
        int currentPosition = 0;
        for (String term : normalizer.getNormalizedTerms()) {
            List<PositionPosting> positionPostingList = positionPostings.getOrDefault(term, new ArrayList<>());
            updatePostingList(docID, positionPostingList, ++currentPosition);
            positionPostings.put(term, positionPostingList);
        }
    }

    private List<Integer> getDocIDsFromPostings(List<PositionPosting> positionPostingList) {
        List<Integer> docIDs = new ArrayList<>();
        for (PositionPosting positionPosting : positionPostingList)
            docIDs.add(positionPosting.getDocID());
        return docIDs;
    }

    private void updatePostingList(int docID, List<PositionPosting> positionPostingList, int currentPosition) {
        int postingIndex = findPostingPosition(positionPostingList, docID);
        if (postingIndex < 0) {
            PositionPosting positionPosting = new PositionPosting(docID);
            positionPosting.addPosition(currentPosition);
            positionPostingList.add(positionPosting);
        } else {
            PositionPosting positionPosting = positionPostingList.get(postingIndex);
            positionPosting.addPosition(currentPosition);
        }
    }

    private int findPostingPosition(List<PositionPosting> positionPostingList, int docID) {
        return Collections.binarySearch(positionPostingList, new PositionPosting(docID));
    }

}