package dictionary;

import parser.TxtParser;
import utils.FileLoader;

import java.io.*;
import java.util.*;

public class Dictionary implements Serializable {

    private DictionaryDataStructure dataStructure = new IncidenceMatrix();
    private Indexer indexer = new Indexer();

    public void addFilesFromFolder(String folderName) {
        File folder = FileLoader.loadFolder(folderName);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            int docID = indexer.addFile(file);
            TxtParser parser = new TxtParser(file);
            dataStructure.addDocumentTerms(parser.getTerms(), docID);
        }
    }

    public void addFile(String fileName) {
        File file = FileLoader.loadFile(fileName);
        int docID = indexer.addFile(file);
        TxtParser parser = new TxtParser(file);
        dataStructure.addDocumentTerms(parser.getTerms(), docID);
    }

    public List<String> documentsWithTerm(String term) {
        List<String> documents = new ArrayList<>();
        for (int docID : dataStructure.getDocIDsWithTerm(term)) {
            File document = indexer.getDocumentByID(docID);
            documents.add(document.getName());
        }
        return documents;
    }

    public List<String> documentsFromQuery(String query) {
        List<String> documents = new ArrayList<>();
        for (int docID : dataStructure.getDocIDsFromQuery(query)) {
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

    public static Dictionary loadFromFile(String directory, String DSType) {
        try { return loadDictionaryFromFile(directory, DSType);
        } catch (IOException e) { System.out.println("Something went wrong during loading from file"); }
        return null;
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
        return (dictionary.Dictionary) ois.readObject();
    }

    private void writeDictionaryToFile(String directory) throws IOException {
        FileWriter fileWriter = new FileWriter(directory);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        indexer.writeToFile(bufferedWriter);
        dataStructure.writeToFile(bufferedWriter);
        bufferedWriter.close();
    }

    private static Dictionary loadDictionaryFromFile(String directory, String DSType) throws IOException {
        Dictionary dictionary = new Dictionary();
        FileReader fileReader = new FileReader(directory);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        dictionary.indexer = Indexer.loadFromFile(bufferedReader);
        loadDataStructureFromFile(dictionary, bufferedReader, DSType);
        bufferedReader.close();
        return dictionary;
    }

    private static void loadDataStructureFromFile(
            Dictionary dictionary, BufferedReader bufferedReader, String DSType) throws IOException {
        if (DSType.equals("INDEX"))
            dictionary.dataStructure = InvertedIndex.readFromFile(bufferedReader);
        else if (DSType.equals("MATRIX"))
            dictionary.dataStructure = IncidenceMatrix.readFromFile(bufferedReader);
        else throw new RuntimeException("No such data structure exists");
    }

}