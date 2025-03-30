package dictionary.docID;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiskIndexer {

    private final Map<Integer, String> docIDs = new HashMap<>();

    public static DiskIndexer load(DataInputStream reader) throws IOException {
        DiskIndexer indexer = new DiskIndexer();
        int docIDCount = reader.readInt();
        int k = 0;
        System.out.println("DOCIDS READ:  " + docIDCount);
        for (int i = 0; i < docIDCount; i++) {
            int docID = reader.readInt();
            int fileLength = reader.readInt();
            char[] buffer = new char[fileLength];
            for (int j = 0; j < fileLength; j++)
                buffer[j] = reader.readChar();
            indexer.docIDs.put(docID, new String(buffer));
            k++;
        }
        System.out.println("DOCIDS SAVED:  " + k);
        return indexer;
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        int i = 0;
        for (Map.Entry<Integer, String> entry : docIDs.entrySet())
            i++;
        System.out.println("DOCIDS: " + i);
        writer.writeInt(i);
        int j = 0;
        for (Map.Entry<Integer, String> entry : docIDs.entrySet()) {
            writer.writeInt(entry.getKey());
            String docPath = entry.getValue();
            writer.writeInt(docPath.length());
            writer.writeChars(docPath);
            j++;
        }
        System.out.println("WRITTEN IDS: " + j);
    }

    public int index(File file) {
        docIDs.put(docIDs.size(), file.getAbsolutePath());
        return docIDs.size() - 1;
    }

    public void index(File file, int docID) {
        docIDs.put(docID, file.getAbsolutePath());
    }

    public String getDocByID(int docID) {
        return docIDs.get(docID);
    }

    public List<String> docsFromDocIDs(List<Integer> docIDs) {
        List<String> docPaths = new ArrayList<>();
        for (Integer docID : docIDs)
            docPaths.add(getDocByID(docID));
        return docPaths;
    }

}