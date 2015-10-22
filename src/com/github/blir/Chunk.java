package com.github.blir;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Blir
 */
public class Chunk {

    private static final int[] harr = new int[31];

    static {
        for (int i = 0; i < 31; i++) {
            harr[i] = (int) Math.pow(2, i);
        }
    }

    private final Location chunkLocation;
    private final int chunkSize;
    private final int hash;

    private final Set<Location> local;

    public Chunk(Location cc, Set<Location> local, int chunkSize) {
        this.chunkLocation = cc;
        this.local = local;
        this.chunkSize = chunkSize;
        hash = hash();
    }

    public Location getChunkLocation() {
        return chunkLocation;
    }

    public boolean contains(Location loc) {
        return local.contains(loc);
    }

    public Set<Location> getLocal() {
        return local;
    }

    private int hash() {
        Set<Location> sum = new HashSet<>();
        int h = 0;
        int idx = 0;
        int xStop = chunkLocation.x + chunkSize;
        int yStop = chunkLocation.y + chunkSize;
        for (int x = chunkLocation.x; x < xStop; x++) {
            for (int y = chunkLocation.y; y < yStop; y++) {
                Location loc = new Location(x, y);
                if (local.contains(loc)) {
                    sum.add(loc);
                    h |= harr[idx];
                }
                idx++;
            }
        }
        if (sum.size() != local.size()) {
            String err = String.format("Chunk[0x%s,%s,%s] contains erroneous location data!", Integer.toHexString(h), chunkLocation, local);
            throw new RuntimeException(err);
        }
        return h;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    // the chunk hash is one-to-one, so this is fine
    public boolean equals(Object obj) {
        return obj instanceof Chunk && this.hash == obj.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Chunk[0x%s,%s,%s]", Integer.toHexString(hash), chunkLocation, local);
    }

    public Set<Location> max() {
        Set<Location> max = new HashSet<>();
        int xStop = chunkLocation.x + chunkSize;
        int yStop = chunkLocation.y + chunkSize;
        for (int x = chunkLocation.x; x < xStop; x++) {
            for (int y = chunkLocation.y; y < yStop; y++) {
                max.add(new Location(x, y));
            }
        }
        return max;
    }

    public void debugMax(Set<Location> max) {
        String maxS = String.format("Max Chunk for: %s", this);
        new LifeDebug(maxS, max).init();
    }

    public Set<Location> relative() {
        Set<Location> rel = new HashSet<>();
        int xStop = chunkLocation.x + chunkSize;
        int yStop = chunkLocation.y + chunkSize;
        for (int x = chunkLocation.x; x < xStop; x++) {
            for (int y = chunkLocation.y; y < yStop; y++) {
                if (local.contains(new Location(x, y))) {
                    rel.add(new Location(x - chunkLocation.x, y - chunkLocation.y));
                }
            }
        }
        return rel;
    }

    public boolean isUseful() {
        int cm1 = chunkSize - 1;
        for (int x = chunkLocation.x, relx = 0; relx < chunkSize; x++, relx++) {
            for (int y = chunkLocation.y, rely = 0; rely < chunkSize; y++, rely++) {
                if (relx > 0 && relx < cm1 && rely > 0 && rely < cm1 && local.contains(new Location(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }
}
