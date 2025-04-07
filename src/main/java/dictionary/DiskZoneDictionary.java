package dictionary;

import dictionary.disk.zoned.ZonedBlockMerger;
import dictionary.disk.zoned.ZonedIndexingThreadDelegator;
import dictionary.docID.DiskIndexer;
import dictionary.structure.DiskDictionaryDataStructure;
import dictionary.structure.DiskEncodedFrequencyIndex;
import dictionary.structure.DiskEncodedZonedFrequencyIndex;
import dictionary.structure.ZonedDictionaryDataStructure;
import posting.ZonedPosting;
import utils.FileLoader;
import utils.StemmerUtils;

import java.io.*;
import java.util.*;

public class DiskZoneDictionary{

    private final static String postingPath = "src/main/java/indexed_collection/posting/posting.txt";
    private final static int MAX_RETURNED_DOCS = 10;

    private DiskIndexer indexer = new DiskIndexer();
    private ZonedDictionaryDataStructure dataStructure
            = new DiskEncodedZonedFrequencyIndex(postingPath);

    public static DiskZoneDictionary load(String path) throws IOException {
        DiskZoneDictionary dictionary = new DiskZoneDictionary();
        DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        dictionary.indexer = DiskIndexer.load(reader);
        dictionary.dataStructure = DiskEncodedZonedFrequencyIndex.load(reader);
        reader.close();
        return dictionary;
    }

    public void buildFromDirectory(String directory) throws IOException {
        File directoryFile = FileLoader.loadFolder(directory);
        ZonedIndexingThreadDelegator delegator = new ZonedIndexingThreadDelegator(indexer);
        delegator.runIndexingThreads(directoryFile);
        ZonedBlockMerger merger = new ZonedBlockMerger(postingPath);
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
        List<ZonedPosting> docIDs = dataStructure.getDocIDsWithTerm(normalized);
        List<Integer> bestDocIDs = getBestDocIDs(docIDs);
        return indexer.docsFromDocIDs(bestDocIDs);
    }

    public Set<String> allTerms() {
        return dataStructure.allTerms();
    }

    public int getUniqueWords() {
        return dataStructure.uniqueWords();
    }

    private List<Integer> getBestDocIDs(List<ZonedPosting> docIDs) {
        docIDs.sort(ZonedPosting.BY_WEIGHT);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < MAX_RETURNED_DOCS; ++i) {
            if (docIDs.isEmpty())
                break;
            int index = docIDs.size() - 1;
            result.add(docIDs.remove(index).getDocID());
        }
        return result;
    }

}