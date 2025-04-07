package dictionary.structure;

import constants.Zone;
import parser.Normalizer;
import posting.ZonedPosting;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class ZonedFrequencyIndex {

    private final Map<String, List<ZonedPosting>> index = new TreeMap<>();

    public void addDocumentTerms(List<String> terms, int docID, Zone zone) {
        Normalizer normalizer = new Normalizer(terms);
        for (String term : normalizer.getNormalizedTerms()) {
            List<ZonedPosting> docIDs = index.getOrDefault(term, new ArrayList<>());
            if (!updatePostingIfPresent(docIDs, docID, zone, term)) {
                docIDs.add(new ZonedPosting(docID, zone));
                index.put(term, docIDs);
            }
        }
    }

    private boolean updatePostingIfPresent(List<ZonedPosting> docIDs, int docID, Zone zone, String term) {
        int position = termPosition(docIDs, docID);
        if (position >= 0) {
            boolean[] zones = docIDs.get(position).getZones();
            zones[zone.getID()] = true;
            docIDs.get(position).setZones(zones);
            index.put(term, docIDs);
            return true;
        }
        return false;
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        for (String term : index.keySet()) {
            writer.writeInt(term.length());
            writer.writeChars(term);
            writer.writeInt(index.get(term).size());
            for (ZonedPosting posting : index.get(term)) {
                writer.writeInt(posting.getDocID());
                for (boolean zones : posting.getZones())
                    writer.writeBoolean(zones);
            }
        }
    }

    public void clear() {
        index.clear();
    }

    private int termPosition(List<ZonedPosting> docIDs, int docID) {
        return Collections.binarySearch(docIDs, new ZonedPosting(docID));
    }

}