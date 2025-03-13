package posting;

import java.io.Serializable;

public class Position implements Comparable<Position>, Serializable {
    private int start;
    private int end;

    public Position(int start, int end) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
    }

    public Position(int start) {
        this.start = start;
        this.end = start;
    }

    public int subtract(Position that) {
        int compare = this.compareTo(that);
        if (compare > 0)
            return that.end - this.start;
        return this.end - that.start;
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
        if (this.end > that.start)
            return this.end - that.start;
        return this.start - that.end;
    }

    public String toString() {
        if (start == end) return start + "";
        return start + "-" + end;
    }
}