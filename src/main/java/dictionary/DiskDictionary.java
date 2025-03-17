package dictionary;

import dictionary.docID.DiskIndexer;
import dictionary.structure.DiskDictionaryDataStructure;
import dictionary.structure.DiskFrequencyIndex;
import dictionary.structure.FrequencyIndex;
import parser.TxtParser;
import utils.FileLoader;
import utils.StemmerUtils;
import utils.StopWatch;

import java.io.*;
import java.util.*;

public class DiskDictionary{

    private final static int MAX_BLOCK_SIZE = 50000000;

    private DiskIndexer indexer = new DiskIndexer();
    private DiskDictionaryDataStructure dataStructure
            = new DiskFrequencyIndex("src/main/java/indexed_collection/posting/posting.txt");
    private long nonUniqueWords;
    private long timeIndexing;

    public static DiskDictionary load(String path) throws IOException {
        DiskDictionary dictionary = new DiskDictionary();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        dictionary.nonUniqueWords = Long.parseLong(reader.readLine());
        dictionary.timeIndexing = Long.parseLong(reader.readLine());
        dictionary.indexer = DiskIndexer.load(reader);
        dictionary.dataStructure = DiskFrequencyIndex.load(reader);
        reader.close();
        return dictionary;
    }

    public void buildFromDirectory(String directory) throws IOException {
        StopWatch stopWatch = new StopWatch();
        File directoryFile = FileLoader.loadFolder(directory);
        //int blockCount = invertDirectoryInBlocks(directoryFile);
        mergeBlocks();
        timeIndexing = stopWatch.stop();
    }

    public void save(String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(nonUniqueWords + "\n");
        writer.write(timeIndexing + "\n");
        indexer.writeToFile(writer);
        dataStructure.writeToFile(writer);
        writer.close();
    }

    public List<String> getDocsWithTerm(String term) {
        String normalized = StemmerUtils.stem(term);
        List<Integer> docIDs = dataStructure.getDocIDsWithTerm(normalized);
        return docsFromDocIDs(docIDs);
    }

    public long getNonUniqueWords() {
        return nonUniqueWords;
    }

    public int getUniqueWords() {
        return dataStructure.uniqueWords();
    }

    public String timeIndexing() {
        return timeIndexing + "ms";
    }

    private List<String> docsFromDocIDs(List<Integer> docIDs) {
        List<String> docPaths = new ArrayList<>();
        for (Integer docID : docIDs)
            docPaths.add(indexer.getDocByID(docID));
        return docPaths;
    }

    private void mergeBlocks() throws IOException {

        File blocksDirectory = FileLoader.loadFolder("src/main/java/indexed_collection/blocks");
        BlockPQ pq = new BlockPQ(Objects.requireNonNull(blocksDirectory.listFiles()));

        System.out.println("Merging...");
        RandomAccessFile raf = new RandomAccessFile("src/main/java/indexed_collection/posting/posting.txt", "rw");

        while (pq.isFull()) {

            String term = pq.peek().term;
            List<Integer> posting = new ArrayList<>(pq.peek().posting);
            pq.next();

            while (pq.isFull() && term.equals(pq.peek().term)) {
                posting.addAll(pq.peek().posting);
                pq.next();
            }

            long currPosition = raf.getFilePointer();
            raf.writeBytes(term);
            Collections.sort(posting);
            for (int docID : posting)
                raf.writeBytes(" " + docID);
            raf.writeBytes("\n");

            dataStructure.addTerm(term, currPosition);

        }

        raf.close();

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
            nonUniqueWords += terms.size();
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

    private static class Block {
        private BufferedReader reader;
        private String term;
        private List<Integer> posting;

        public Block(File file) {
            try { reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException ignored) {}
            nextLine();
        }

        public boolean nextLine() {
            try { return tryNextLine();
            } catch (IOException ignored) {}
            return false;
        }

        private boolean tryNextLine() throws IOException {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                return false;
            }
            String[] tokens = line.split(" ");
            term = tokens[0];
            posting = new ArrayList<>();
            for (int i = 1; i < tokens.length; i++)
                posting.add(Integer.parseInt(tokens[i]));
            return true;
        }
    }

    private static class BlockPQ {
        private final PriorityQueue<Block> blocks = new PriorityQueue<>(
                Comparator.comparing(o -> o.term));

        public BlockPQ(File[] files) {
            for (File file : files) {
                blocks.add(new Block(file));
            }
        }

        public boolean isFull() {
            return !blocks.isEmpty();
        }

        public Block peek() {
            return blocks.peek();
        }

        public void next() {
            Block block = blocks.poll();
            if (block == null) return;
            if (block.nextLine())
                blocks.add(block);
        }
    }

}