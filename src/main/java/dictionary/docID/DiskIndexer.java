package dictionary.docID;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiskIndexer {

    private final Map<Integer, String> docIDs = new HashMap<>();

    public static DiskIndexer load(DataInputStream reader) throws IOException {
        DiskIndexer indexer = new DiskIndexer();
        int docIDCount = reader.readInt();
        for (int i = 0; i < docIDCount; i++) {
            int docID = reader.readInt();
            int fileLength = reader.readInt();
            char[] buffer = new char[fileLength];
            for (int j = 0; j < fileLength; j++)
                buffer[j] = reader.readChar();
            indexer.docIDs.put(docID, new String(buffer));
        }
        return indexer;
    }

    public int index(File file) {
        docIDs.put(docIDs.size(), file.getAbsolutePath());
        return docIDs.size() - 1;
    }

    public void index(File file, int docID) {
        if (docIDs.containsKey(docID))
            System.out.println("hell yeah");
        docIDs.put(docID, file.getAbsolutePath());
    }

    public String getDocByID(int docID) {
        return docIDs.get(docID);
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(docIDs.size());
        for (Map.Entry<Integer, String> entry : docIDs.entrySet()) {
            writer.writeInt(entry.getKey());
            String value = entry.getValue();
            writer.writeInt(value.length());
            writer.writeChars(value);
        }
    }

}