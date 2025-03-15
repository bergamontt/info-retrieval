package dictionary.docID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiskIndexer {

    private final Map<Integer, String> docIDs = new HashMap<>();

    public static DiskIndexer load(BufferedReader br) throws IOException {
        DiskIndexer indexer = new DiskIndexer();
        int docIDCount = Integer.parseInt(br.readLine());
        for (int i = 0; i < docIDCount; ++i) {
            String[] tokens = br.readLine().split(" ");
            indexer.docIDs.put(Integer.parseInt(tokens[0]), tokens[1]);
        }
        return indexer;
    }

    public int index(File file) {
        docIDs.put(docIDs.size(), file.getAbsolutePath());
        return docIDs.size() - 1;
    }

    public String getDocByID(int docID) {
        return docIDs.get(docID);
    }

    public void writeToFile(BufferedWriter bw) throws IOException {
        bw.write(docIDs.size() + "\n");
        for (Map.Entry<Integer, String> entry : docIDs.entrySet())
            bw.write(entry.getKey() + " " + entry.getValue() + "\n");
    }

}