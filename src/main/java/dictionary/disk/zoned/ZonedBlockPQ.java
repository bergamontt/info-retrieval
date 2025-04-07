package dictionary.disk.zoned;

import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ZonedBlockPQ {

    private final PriorityQueue<ZonedBlock> blocks = new PriorityQueue<>(
            Comparator.comparing(ZonedBlock::getTerm));

    public ZonedBlockPQ(File[] files) {
        for (File file : files)
            blocks.add(new ZonedBlock(file));
    }

    public boolean isFull() {
        return !blocks.isEmpty();
    }

    public ZonedBlock peek() {
        return blocks.peek();
    }

    public void next() {
        ZonedBlock block = blocks.poll();
        if (block == null) return;
        if (block.nextLine())
            blocks.add(block);
    }
}