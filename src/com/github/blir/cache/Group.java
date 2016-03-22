package com.github.blir.cache;

import com.github.blir.Location;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Travis
 */
public class Group {
    
    private static final int[] harr = new int[31];

    static {
        for (int i = 0; i < 61; i++) {
            harr[i] = (int) Math.pow(2, i);
        }
    }
    
    private long hash;
    private final Set<Location> cells = new HashSet<>();
    // bounds possibly
    
    public void addCell(Location loc) {
        cells.add(loc);
        if (cells.size() > 64) {
            throw new IllegalStateException("group too large");
        }
    }
    
    public Stream<Location> stream() {
        return cells.stream();
    }
    
    public boolean contains(Location cell) {
        return cells.contains(cell);
    }
    
    public long genHash() {
        return -1;
    }
}
