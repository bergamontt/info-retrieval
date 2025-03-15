package dictionary;

import dictionary.docID.DiskIndexer;
import dictionary.structure.DiskDictionaryDataStructure;
import dictionary.structure.DiskFrequencyIndex;
import dictionary.structure.FrequencyIndex;
import parser.TxtParser;
import utils.FileLoader;
import utils.StemmerUtils;

import java.io.*;
import java.util.*;

public class DiskDictionary{

    private final static int MAX_BLOCK_SIZE = 50000000;
    private DiskIndexer indexer = new DiskIndexer();
    private DiskDictionaryDataStructure dataStructure
            = new DiskFrequencyIndex("src/main/java/indexed_collection/posting/posting.txt");

    public static DiskDictionary load(String path) throws IOException {
        DiskDictionary dictionary = new DiskDictionary();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        dictionary.indexer = DiskIndexer.load(reader);
        dictionary.dataStructure = DiskFrequencyIndex.load(reader);
        reader.close();
        return dictionary;
    }

    public void buildFromDirectory(String directory) throws IOException {
        File directoryFile = FileLoader.loadFolder(directory);
        indexDirectory(directoryFile);
    }

    public void save(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        indexer.writeToFile(writer);
        dataStructure.writeToFile(writer);
        writer.close();
    }

    public List<String> getDocsWithTerm(String term) {
        String normalized = StemmerUtils.stem(term);
        List<Integer> docIDs = dataStructure.getDocIDsWithTerm(normalized);
        return docsFromDocIDs(docIDs);
    }

    private List<String> docsFromDocIDs(List<Integer> docIDs) {
        List<String> docPaths = new ArrayList<>();
        for (Integer docID : docIDs)
            docPaths.add(indexer.getDocByID(docID));
        return docPaths;
    }

    private void indexDirectory(File directoryFile) throws IOException {
        int blockCount = invertDirectoryInBlocks(directoryFile);
        mergeBlocks(blockCount);
    }

    private void mergeBlocks(int blockCount) throws IOException {

        PriorityQueue<TermData> pq = new PriorityQueue<>();
        List<BufferedReader> blockReaders = new ArrayList<>(blockCount);
        File blocksDirectory = FileLoader.loadFolder("src/main/java/indexed_collection/blocks");

        for (File file : Objects.requireNonNull(blocksDirectory.listFiles())) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line == null) continue;
            String[] lineTokens = line.split(" ", 2);
            if (lineTokens.length != 2) continue;
            pq.offer(new TermData(lineTokens[0], lineTokens[1], blockReaders.size()));
            blockReaders.add(reader);
        }

        System.out.println("Merging...");

        String lastTerm = null;
        StringBuilder lastData = new StringBuilder();
        RandomAccessFile raf = new RandomAccessFile("src/main/java/indexed_collection/posting/posting.txt", "rw");

        while (!pq.isEmpty()) {

            TermData termData = pq.poll();
            String currTerm = termData.term;
            String currData = termData.data;
            int currIndex = termData.index;

            if (!currTerm.equals(lastTerm)) {
                if (lastTerm == null) {
                    lastTerm = currTerm;
                } else {
                    long currPosition = raf.getFilePointer();
                    raf.writeBytes(lastTerm + " " + lastData.toString() + "\r\n");
                    dataStructure.addTerm(lastTerm, currPosition);
                    lastTerm = currTerm;
                    lastData.setLength(0);
                }
            }

            lastData.append(currData);
            BufferedReader blockReader = blockReaders.get(currIndex);
            String line = blockReader.readLine();
            if (line == null) {
                blockReader.close();
                continue;
            }

            String[] tokens = line.split(" ", 2);
            if (tokens.length == 2)
                pq.add(new TermData(tokens[0], tokens[1], currIndex));
        }

        raf.close();

        for (BufferedReader blockReader : blockReaders)
            blockReader.close();

    }

    private int invertDirectoryInBlocks(File directoryFile) throws IOException {
        int blockCount = 0;
        int currentBlockSize = 0;
        FrequencyIndex frequencyIndex = new FrequencyIndex();
        for (File file : Objects.requireNonNull(directoryFile.listFiles())) {
            TxtParser txtParser = new TxtParser(file);
            List<String> terms = txtParser.getTerms();
            if (blockIsFull(currentBlockSize, terms.size())) {
                writeIndexToFile(frequencyIndex, blockCount++);
                frequencyIndex = new FrequencyIndex();
                currentBlockSize = 0;
            }
            int docId = indexer.index(file);
            frequencyIndex.addDocumentTerms(terms, docId);
            currentBlockSize += terms.size();
        }
        writeIndexToFile(frequencyIndex, blockCount);
        return blockCount;
    }

    private boolean blockIsFull(int currentBlockSize, int termsCount) {
        return currentBlockSize + termsCount > MAX_BLOCK_SIZE;
    }

    private void writeIndexToFile(FrequencyIndex frequencyIndex, int blockNumber) throws IOException {
        BufferedWriter bufferedWriter =
                new BufferedWriter(new FileWriter("src/main/java/indexed_collection/blocks/"+ blockNumber + ".txt"));
        frequencyIndex.writeToFile(bufferedWriter);
        bufferedWriter.close();
    }

    private record TermData(String term, String data, int index) implements Comparable<TermData> {

        @Override
            public int compareTo(TermData o) {
                if (!term.equals(o.term))
                    return term.compareTo(o.term);
                return index - o.index;
            }
        }

}