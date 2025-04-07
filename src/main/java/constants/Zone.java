package constants;

public enum Zone {

    TITLE(0.3, 0),
    AUTHOR(0.2, 1),
    BODY(0.5, 2);

    private final double weight;
    private final int ID;

    Zone(double weight, int ID) {
        this.weight = weight;
        this.ID = ID;
    }

    public static Zone fromID(int ID) {
        for (Zone zone : Zone.values())
            if (zone.ID == ID) return zone;
        throw new IllegalArgumentException("Invalid ID: " + ID);
    }

    public double getWeight() {
        return weight;
    }

    public int getID() {
        return ID;
    }

}