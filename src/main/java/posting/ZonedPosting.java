package posting;

import constants.Zone;

import java.util.Comparator;

public class ZonedPosting implements Comparable<ZonedPosting> {

    private int docID;
    private boolean[] zones = new boolean[Zone.values().length];

    public ZonedPosting(int docID) {
        this.docID = docID;
    }

    public ZonedPosting(int docID, Zone zone) {
        this.docID = docID;
        this.zones[zone.getID()] = true;
    }

    public ZonedPosting(int docID, boolean[] zones) {
        this.docID = docID;
        this.zones = zones;
    }

    public boolean[] getZones() {
        return zones;
    }

    public double getWeight() {
        double weight = 0;
        for (int i = 0; i < zones.length; i++)
            if (zones[i]) weight += Zone.fromID(i).getWeight();
        return weight;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public void setZones(boolean[] zones) {
        this.zones = zones;
    }

    @Override
    public int compareTo(ZonedPosting o) {
        return Integer.compare(this.docID, o.docID);
    }

    public static Comparator<ZonedPosting> BY_WEIGHT = Comparator.comparingDouble(ZonedPosting::getWeight);

}