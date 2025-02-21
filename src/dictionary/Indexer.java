package dictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Indexer {

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

}