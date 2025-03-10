package posting;

import java.io.Serializable;

public class Position implements Comparable<Position>, Serializable {
    private int start;
    private int end;

    public Position(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Position(int start) {
        this.start = start;
        this.end = start;
    }

    public int subtract(Position p) {
        int compare = this.compareTo(p);
        if (compare > 0)
            return end - p.start;
        return p.end - start;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        if (start < end) {
            this.start = start;
        } else {
            this.start = end;
            this.end = start;
        }
    }

    public void setEnd(int end) {
        if (end < start) {
            this.end = start;
            this.start = end;
        } else {
            this.end = end;
        }
    }

    @Override
    public int compareTo(Position that) {
        return this.start - that.start;
    }

    public String toString() {
        if (start == end) return start + "";
        return start + "-" + end;
    }
}