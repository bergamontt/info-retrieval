package dictionary;

import dictionary.structure.DictionaryDataStructure;
import dictionary.structure.InvertedIndex;
import dictionary.termIndexer.KgramIndexer;
import dictionary.termIndexer.PermutermIndexer;
import dictionary.termIndexer.TrieTermIndexer;
import dictionary.termIndexer.TermIndexer;
import query.QueryHandler;
import pattern.factory.DataStructureFactory;
import parser.*;
import utils.*;

import java.io.*;
import java.util.*;

public class Dictionary implements Serializable {

    private DictionaryDataStructure dataStructure = new InvertedIndex();
    private TermIndexer termIndexer = new KgramIndexer();
    private Indexer indexer = new Indexer();

    public Dictionary() {}

    public Dictionary(DictionaryDataStructure dataStructure) {
        this.dataStructure = dataStructure;
    }

    public void addFilesFromFolder(String folderName) {
        File folder = FileLoader.loadFolder(folderName);
        for (File file : Objects.requireNonNull(folder.listFiles()))
            addTermsFromFile(file);
    }

    public void addFile(String fileName) {
        File file = FileLoader.loadFile(fileName);
        addTermsFromFile(file);
    }

    public List<String> documentsWithTerm(String term) {
        String normalizedTerm = StemmerUtils.stem(term);
        List<String> documents = new ArrayList<>();
        for (int docID : dataStructure.getDocIDsWithTerm(normalizedTerm)) {
            File document = indexer.getDocumentByID(docID);
            documents.add(document.getName());
        }
        return documents;
    }

    public List<String> documentsFromQuery(String query) {
        List<String> documents = new ArrayList<>();
        List<Integer> docIDs = getDocIDs(query);
        for (int docID : docIDs) {
            File document = indexer.getDocumentByID(docID);
            documents.add(document.getName());
        }
        return documents;
    }

    public void serialize(String directory) {
        try { serializeDirectory(directory);
        } catch (IOException e) {
            System.out.println("Something went wrong during serialization");
        }
    }

    public static Dictionary deserialize(String directory) {
        try { return deserializeDictionary(directory);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Something went wrong during deserialization"); }
        return null;
    }

    public void writeToFile(String directory) {
        try { writeDictionaryToFile(directory);
        } catch (IOException e) { System.out.println("Something went wrong during writing file"); }
    }

    public static Dictionary loadFromFile(String directory) {
        try { return loadDictionaryFromFile(directory);
        } catch (IOException e) { System.out.println("Something went wrong during loading from file"); }
        return null;
    }

    private void addTermsFromFile(File file) {
        int docID = indexer.addFile(file);
        TxtParser parser = new TxtParser(file);
        Normalizer normalizer = new Normalizer(parser.getTerms());
        List<String> normalizedTerms = normalizer.getNormalizedWords();
        termIndexer.addTerms(normalizedTerms);
        dataStructure.addDocumentTerms(normalizedTerms, docID);
    }

    private void serializeDirectory(String directory) throws IOException {
        FileOutputStream fos = new FileOutputStream(directory);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

    private static Dictionary deserializeDictionary(String directory) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(directory);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Dictionary dictionary = (dictionary.Dictionary) ois.readObject();
        ois.close();
        return dictionary;
    }

    private void writeDictionaryToFile(String directory) throws IOException {
        FileWriter fileWriter = new FileWriter(directory);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        indexer.writeToFile(bufferedWriter);
        dataStructure.writeToFile(bufferedWriter);
        bufferedWriter.close();
    }

    private static Dictionary loadDictionaryFromFile(String directory) throws IOException {
        Dictionary dictionary = new Dictionary();
        FileReader fileReader = new FileReader(directory);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        dictionary.indexer = Indexer.loadFromFile(bufferedReader);
        loadDataStructureFromFile(dictionary, bufferedReader);
        bufferedReader.close();
        return dictionary;
    }

    private static void loadDataStructureFromFile(Dictionary dictionary, BufferedReader bufferedReader) throws IOException {
        String dsType = bufferedReader.readLine();
        DataStructureFactory factory = new DataStructureFactory();
        dictionary.dataStructure = factory.createDataStructure(dsType, bufferedReader);
    }

    private List<Integer> getDocIDs(String query) {
        try { QueryHandler queryHandler = new QueryHandler(dataStructure, termIndexer, indexer);
            return queryHandler.getDocIDs(query);
        } catch (NoSuchMethodException e) { throw new RuntimeException(e); }
    }

}