package dictionary.disk.zoned;

import posting.ZonedPosting;
import utils.FileLoader;

import java.io.*;
import java.util.*;

public class ZonedBlockMerger {
    private final static String blocksPath = "src/main/java/indexed_collection/blocks";
    private final String postingPath;

    public ZonedBlockMerger(String postingPath) {
        this.postingPath = postingPath;
    }

    public Map<String, Integer> mergeBlocks() throws IOException {

        System.out.println("Merging...");

        File blocksDirectory = FileLoader.loadFolder(blocksPath);
        ZonedBlockPQ pq = new ZonedBlockPQ(Objects.requireNonNull(blocksDirectory.listFiles()));
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(postingPath)));
        Map<String, Integer> terms = new HashMap<>();
        int currPosition = 0;

        while (pq.isFull()) {
            String term = pq.peek().getTerm();
            List<ZonedPosting> posting = new ArrayList<>(pq.peek().getPosting());
            pq.next();

            while (pq.isFull() && term.equals(pq.peek().getTerm())) {
                posting.addAll(pq.peek().getPosting());
                pq.next();
            }

            ZonedPostingEncoder encoder = new ZonedPostingEncoder();
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