package dictionary.disk;

import utils.VBE;

import java.util.Collections;
import java.util.List;

public class PostingEncoder {

    public byte[] encodePostings(List<Integer> postings) {
        Collections.sort(postings);
        turnIntoIntervalPostings(postings);
        return VBE.encode(postings);
    }

    private void turnIntoIntervalPostings(List<Integer> posting) {
        if (posting.isEmpty()) return;
        int prev = posting.get(0);
        for (int i = 1; i < posting.size(); ++i) {
            int current = posting.get(i);
            posting.set(i, current - prev);
            prev = current;
        }
    }

}