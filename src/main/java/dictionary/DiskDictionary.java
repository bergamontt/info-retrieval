package dictionary;

import dictionary.disk.BlockMerger;
import dictionary.disk.BlockPQ;
import dictionary.disk.IndexingThreadDelegator;
import dictionary.disk.PostingEncoder;
import dictionary.docID.DiskIndexer;
import dictionary.structure.DiskDictionaryDataStructure;
import dictionary.structure.DiskEncodedFrequencyIndex;
import utils.FileLoader;
import utils.StemmerUtils;

import java.io.*;
import java.util.*;

public class DiskDictionary{

    private final static String postingPath = "src/main/java/indexed_collection/posting/posting.txt";

    private DiskIndexer indexer = new DiskIndexer();
    private DiskDictionaryDataStructure dataStructure
            = new DiskEncodedFrequencyIndex(postingPath);

    public static DiskDictionary load(String path) throws IOException {
        DiskDictionary dictionary = new DiskDictionary();
        DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        dictionary.indexer = DiskIndexer.load(reader);
        dictionary.dataStructure = DiskEncodedFrequencyIndex.load(reader);
        reader.close();
        return dictionary;
    }

    public void buildFromDirectory(String directory) throws IOException {
        File directoryFile = FileLoader.loadFolder(directory);
        IndexingThreadDelegator delegator = new IndexingThreadDelegator(indexer);
        delegator.runIndexingThreads(directoryFile);
        BlockMerger merger = new BlockMerger(postingPath);
        dataStructure.addTerms(merger.mergeBlocks());
    }

    public void save(String path) throws IOException {
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
        indexer.writeToFile(writer);
        dataStructure.writeToFile(writer);
        writer.close();
    }

    public List<String> getDocsWithTerm(String term) {
        String normalized = StemmerUtils.stem(term);
        List<Integer> docIDs = dataStructure.getDocIDsWithTerm(normalized);
        return indexer.docsFromDocIDs(docIDs);
    }

    public Set<String> allTerms() {
        return dataStructure.allTerms();
    }

    public int getUniqueWords() {
        return dataStructure.uniqueWords();
    }

}