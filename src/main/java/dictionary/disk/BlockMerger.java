package dictionary.disk;

import utils.FileLoader;

import java.io.*;
import java.util.*;

public class BlockMerger {
    private final static String blocksPath = "src/main/java/indexed_collection/blocks";
    private final String postingPath;

    public BlockMerger(String postingPath) {
        this.postingPath = postingPath;
    }

    public Map<String, Integer> mergeBlocks() throws IOException {

        System.out.println("Merging...");

        File blocksDirectory = FileLoader.loadFolder(blocksPath);
        BlockPQ pq = new BlockPQ(Objects.requireNonNull(blocksDirectory.listFiles()));
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(postingPath)));
        Map<String, Integer> terms = new HashMap<>();
        int currPosition = 0;

        while (pq.isFull()) {
            String term = pq.peek().getTerm();
            List<Integer> posting = new ArrayList<>(pq.peek().getPosting());
            pq.next();

            while (pq.isFull() && term.equals(pq.peek().getTerm())) {
                posting.addAll(pq.peek().getPosting());
                pq.next();
            }

            PostingEncoder encoder = new PostingEncoder();
            byte[] encoded = encoder.encodePostings(posting);

            writer.writeInt(encoded.length);
            writer.write(encoded);

            terms.put(term, currPosition);
            currPosition += Integer.BYTES + encoded.length;
        }

        writer.close();
        return terms;
    }

}