package dictionary;

import dictionary.docID.DiskIndexer;
import dictionary.structure.DiskDictionaryDataStructure;
import dictionary.structure.DiskEncodedFrequencyIndex;
import dictionary.structure.DiskFrequencyIndex;
import dictionary.structure.FrequencyIndex;
import dictionary.threads.IndexingThread;
import parser.TxtParser;
import utils.FileLoader;
import utils.StemmerUtils;
import utils.StopWatch;
import utils.VBE;

import java.io.*;
import java.util.*;

public class DiskDictionary{

    private final static int INDEXING_THREADS = 16;
    private final static String postingPath = "src/main/java/indexed_collection/posting/posting.txt";
    private final static String blocksPath = "src/main/java/indexed_collection/blocks";

    private DiskIndexer indexer = new DiskIndexer();
    private DiskDictionaryDataStructure dataStructure
            = new DiskEncodedFrequencyIndex(postingPath);
    private long nonUniqueWords;
    private long timeIndexing;

    public static DiskDictionary load(String path) throws IOException {
        DiskDictionary dictionary = new DiskDictionary();
        DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        dictionary.indexer = DiskIndexer.load(reader);
        dictionary.dataStructure = DiskEncodedFrequencyIndex.load(reader);
        reader.close();
        return dictionary;
    }

    public void buildFromDirectory(String directory) throws IOException {
        StopWatch stopWatch = new StopWatch();
        File directoryFile = FileLoader.loadFolder(directory);
        runIndexingThreads(directoryFile);
        mergeBlocks();
        timeIndexing = stopWatch.stop();
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
        return docsFromDocIDs(docIDs);
    }

    public Set<String> allTerms() {
        return dataStructure.allTerms();
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

    private void runIndexingThreads(File directoryFile) {
        Thread[] threads = new Thread[INDEXING_THREADS];
        File[] directoryFilesArr = directoryFile.listFiles();
        if (directoryFilesArr == null) return;
        List<File> directoryFiles = new ArrayList<>(List.of(directoryFilesArr));
        int filesPerThread = directoryFiles.size() / INDEXING_THREADS;
        for (int i = 0; i < INDEXING_THREADS; i++) {
            List<File> threadFiles = new ArrayList<>();
            if (i == INDEXING_THREADS - 1)
                threadFiles.addAll(directoryFiles);
            else {
                for (int j = 0; j < filesPerThread; j++)
                    threadFiles.add(directoryFiles.remove(0));
            }
            threads[i] = new Thread(new IndexingThread(indexer, threadFiles, i, i * (filesPerThread + 1)));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try { thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); }
        }
    }

    private List<String> docsFromDocIDs(List<Integer> docIDs) {
        List<String> docPaths = new ArrayList<>();
        for (Integer docID : docIDs)
            docPaths.add(indexer.getDocByID(docID));
        return docPaths;
    }

    private void mergeBlocks() throws IOException {

        File blocksDirectory = FileLoader.loadFolder(blocksPath);
        BlockPQ pq = new BlockPQ(Objects.requireNonNull(blocksDirectory.listFiles()));
        Map<String, Integer> terms = new HashMap<>();

        System.out.println("Merging...");

        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(postingPath)));
        int currPosition = 0;

        while (pq.isFull()) {
            String term = pq.peek().term;
            List<Integer> posting = new ArrayList<>(pq.peek().posting);
            pq.next();

            while (pq.isFull() && term.equals(pq.peek().term)) {
                posting.addAll(pq.peek().posting);
                pq.next();
            }

            Collections.sort(posting);
            intervalPosting(posting);
            byte[] encoded = VBE.encode(posting);

            writer.writeInt(encoded.length);
            writer.write(encoded);

            terms.put(term, currPosition);
            currPosition += Integer.BYTES + encoded.length;
        }
        dataStructure.addTerms(terms);
        writer.close();
    }

    private void intervalPosting(List<Integer> posting) {
        if (posting.isEmpty()) return;
        int prev = posting.get(0);
        for (int i = 1; i < posting.size(); ++i) {
            int current = posting.get(i);
            posting.set(i, current - prev);
            prev = current;
        }
    }

    private static class Block {
        private DataInputStream reader;
        private String term;
        private List<Integer> posting;

        public Block(File file) {
            try { reader = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            } catch (FileNotFoundException ignored) {}
            nextLine();
        }

        public boolean nextLine() {
            try { return tryNextLine();
            } catch (IOException ignored)
            { return false;}
        }

        private boolean tryNextLine() throws IOException {
            try {
                int termLength = reader.readInt();
                char[] buffer = new char[termLength];
                for (int i = 0; i < termLength; i++)
                    buffer[i] = reader.readChar();
                term = new String(buffer);
                int postingLength = reader.readInt();
                posting = new ArrayList<>(postingLength);
                for (int i = 0; i < postingLength; i++)
                    posting.add(reader.readInt());
                return true;
            } catch (EOFException e) {
                reader.close();
                return false;
            }
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