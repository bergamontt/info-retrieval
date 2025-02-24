package dictionary;

import dictionary.structure.DictionaryDataStructure;
import dictionary.structure.IncidenceMatrix;
import dictionary.structure.InvertedIndex;
import parser.TxtParser;
import utils.FileLoader;
import utils.StemmerUtils;

import java.io.*;
import java.util.*;

public class Dictionary implements Serializable {

    private DictionaryDataStructure dataStructure = new InvertedIndex();
    private Indexer indexer = new Indexer();
    private String lastSizeInMB;

    public Dictionary() {}

    public Dictionary(DictionaryDataStructure dataStructure) {
        this.dataStructure = dataStructure;
    }

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

    public static Dictionary loadFromFile(String directory) {
        try { return loadDictionaryFromFile(directory);
        } catch (IOException e) { System.out.println("Something went wrong during loading from file"); }
        return null;
    }

    public String getLastSizeInMB() {
        if (lastSizeInMB == null)
            System.out.println("The Dictionary has not been stored yet");
        return lastSizeInMB;
    }

    private void serializeDirectory(String directory) throws IOException {
        File directoryFile = new File(directory);
        FileOutputStream fos = new FileOutputStream(directoryFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        lastSizeInMB = getFileSizeInMB(directoryFile);
        oos.close();
    }

    private static Dictionary deserializeDictionary(String directory) throws IOException, ClassNotFoundException {
        File directoryFile = new File(directory);
        FileInputStream fis = new FileInputStream(directoryFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Dictionary dictionary = (dictionary.Dictionary) ois.readObject();
        dictionary.lastSizeInMB = getFileSizeInMB(directoryFile);
        ois.close();
        return dictionary;
    }

    private void writeDictionaryToFile(String directory) throws IOException {
        File directoryFile = new File(directory);
        FileWriter fileWriter = new FileWriter(directoryFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        indexer.writeToFile(bufferedWriter);
        dataStructure.writeToFile(bufferedWriter);
        lastSizeInMB = getFileSizeInMB(directoryFile);
        bufferedWriter.close();
    }

    private static Dictionary loadDictionaryFromFile(String directory) throws IOException {
        Dictionary dictionary = new Dictionary();
        File directoryFile = new File(directory);
        FileReader fileReader = new FileReader(directoryFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        dictionary.indexer = Indexer.loadFromFile(bufferedReader);
        loadDataStructureFromFile(dictionary, bufferedReader);
        dictionary.lastSizeInMB = getFileSizeInMB(directoryFile);
        bufferedReader.close();
        return dictionary;
    }

    private static void loadDataStructureFromFile(Dictionary dictionary, BufferedReader bufferedReader) throws IOException {
        String dsType = bufferedReader.readLine();
        if (dsType.equals("matrix"))
            dictionary.dataStructure = IncidenceMatrix.readFromFile(bufferedReader);
        else if (dsType.equals("index"))
            dictionary.dataStructure = InvertedIndex.readFromFile(bufferedReader);
        else throw new RuntimeException("Unknown dictionary type");
    }

    private static String getFileSizeInMB(File file) {
        return (double) file.length() / (1024 * 1024) + " MB";
    }

}