package structure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Biword extends InvertedIndex {

    public static Biword readFromFile(BufferedReader fileReader) throws IOException {
        Biword biword = new Biword();
        biword.fileCount = Integer.parseInt(fileReader.readLine());
        int termCount = Integer.parseInt(fileReader.readLine());
        for (int i = 0; i < termCount; ++i) {
            String[] termInfo = fileReader.readLine().split(" ");
            List<Integer> docIDs = new ArrayList<>();
            for (int j = 1; j < termInfo.length; ++j)
                docIDs.add(Integer.parseInt(termInfo[j]));
            biword.putTerm(termInfo[0], docIDs);
        }
        return biword;
    }

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
    public void writeToFile(BufferedWriter fileWriter) throws IOException {
        fileWriter.write("bi");
        super.writeToFile(fileWriter);
    }

    private void addDocumentToTerm(int docID, String term) {
        List<Integer> currentTermDocuments = getTermRawDocIDs(term);
        if (!documentsHasDocument(currentTermDocuments, docID))
            currentTermDocuments.add(docID);
        putTerm(term, currentTermDocuments);
    }

    private boolean documentsHasDocument(List<Integer> documents, int docID) {
        int index = Collections.binarySearch(documents, docID);
        return index >= 0;
    }

}