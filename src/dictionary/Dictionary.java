package dictionary;

import parser.TxtParser;
import utils.FileLoader;

import java.io.*;
import java.util.*;

public class Dictionary implements Serializable {

    private final InvertedIndex matrix = new InvertedIndex();
    private final Indexer indexer = new Indexer();

    public void addFilesFromFolder(String folderName) {
        File folder = FileLoader.loadFolder(folderName);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            int docID = indexer.addFile(file);
            TxtParser parser = new TxtParser(file);
            matrix.addDocumentTerms(parser.getTerms(), docID);
        }
    }

    public void addFile(String fileName) {
        File file = FileLoader.loadFile(fileName);
        int docID = indexer.addFile(file);
        TxtParser parser = new TxtParser(file);
        matrix.addDocumentTerms(parser.getTerms(), docID);
    }

    public List<String> documentsWithTerm(String term) {
        List<String> documents = new ArrayList<>();
        for (int docID : matrix.getDocIDsWithTerm(term)) {
            File document = indexer.getDocumentByID(docID);
            documents.add(document.getName());
        }
        return documents;
    }

    public List<String> documentsFromQuery(String query) {
        List<String> documents = new ArrayList<>();
        for (int docID : matrix.getDocIDsFromQuery(query)) {
            File document = indexer.getDocumentByID(docID);
            documents.add(document.getName());
        }
        return documents;
    }

//    public Iterable<String> getTerms() {
//        return dictionaryStructure.getTerms();
//    }
//
//    public BigInteger getTermCount(String term) {
//        return dictionaryStructure.getTermCount(term);
//    }
//
//    public Iterable<Posting> getTermPostings(String term) {
//        return dictionaryStructure.getTermPostings(term);
//    }
//
//    public BigInteger getAllTermsCount() {
//        return dictionaryStructure.getAllTermsCount();
//    }
//
//    public long getUniqueTermsCount() {
//        return dictionaryStructure.getUniqueTermsCount();
//    }

}