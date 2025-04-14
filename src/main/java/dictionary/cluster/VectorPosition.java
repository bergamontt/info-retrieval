package dictionary.cluster;

public class VectorPosition {
    private int vectorPosition;
    private int blockID;

    public VectorPosition() {}

    public VectorPosition(int vectorPosition, int blockID) {
        this.vectorPosition = vectorPosition;
        this.blockID = blockID;
    }

    public int getVectorPosition() {
        return vectorPosition;
    }

    public void setVectorPosition(int vectorPosition) {
        this.vectorPosition = vectorPosition;
    }

    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }
}