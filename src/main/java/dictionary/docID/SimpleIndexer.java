package dictionary.docID;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleIndexer implements Serializable {

    private final List<File> files = new ArrayList<>();
    private final List<Integer> docIDs = new ArrayList<>();

    public int addFile(File file) {
        int docID = docIDs.size();
        docIDs.add(docID);
        files.add(file);
        return docID;
    }

    public File getDocumentByID(int docID) {
        int index = Collections.binarySearch(docIDs, docID);
        return files.get(index);
    }

    public void writeToFile(BufferedWriter fileWriter) throws IOException {
        fileWriter.write(files.size() + "\n");
        for (int docID : docIDs)
            fileWriter.write(getDocumentByID(docID).getPath() + "\n");
    }

    public static SimpleIndexer loadFromFile(BufferedReader fileReader) throws IOException {
        SimpleIndexer simpleIndexer = new SimpleIndexer();
        int filesCount = Integer.parseInt(fileReader.readLine());
        for (int i = 0; i < filesCount; ++i) {
            String filePath = fileReader.readLine();
            simpleIndexer.addFile(new File(filePath));
        }
        return simpleIndexer;
    }
}