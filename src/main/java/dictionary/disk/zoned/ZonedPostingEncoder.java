package dictionary.disk.zoned;

import posting.ZonedPosting;
import utils.VBE;

import java.util.Collections;
import java.util.List;

public class ZonedPostingEncoder {

    public byte[] encodePostings(List<ZonedPosting> postings) {
        Collections.sort(postings);
        turnIntoIntervalPostings(postings);
        return VBE.encodeZoned(postings);
    }

    private void turnIntoIntervalPostings(List<ZonedPosting> posting) {
        if (posting.isEmpty()) return;
        ZonedPosting prev = posting.get(0);
        for (int i = 1; i < posting.size(); ++i) {
            ZonedPosting current = posting.get(i);
            posting.set(i, new ZonedPosting(current.getDocID() - prev.getDocID(), current.getZones()));
            prev = current;
        }
    }

}