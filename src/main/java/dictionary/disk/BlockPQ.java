package dictionary.disk;

import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;

public class BlockPQ {

    private final PriorityQueue<Block> blocks = new PriorityQueue<>(
            Comparator.comparing(Block::getTerm));

    public BlockPQ(File[] files) {
        for (File file : files) {
            blocks.add(new Block(file));
        }
    }

    public boolean isFull() {
        return !blocks.isEmpty();
    }

    public Block peek() {
        return blocks.peek();
    }

    public void next() {
        Block block = blocks.poll();
        if (block == null) return;
        if (block.nextLine())
            blocks.add(block);
    }
}