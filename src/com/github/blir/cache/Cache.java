package com.github.blir.cache;

import com.github.blir.Life;
import com.github.blir.gui.LifeDebug;
import com.github.blir.Location;
import java.util.*;

/**
 *
 * @author Blir
 */
public class Cache {

    /**
     * The chunk chunkSize that the world will be split into for each tick.
     * Smaller chunk size means faster hashing and a smaller cache but slower
     * splitting. Larger chunk size means slower hashing and a larger cache but
     * faster splitting.
     */
    public int chunkSize = 5;
    public int cm1 = chunkSize - 1;
    public int cm2 = chunkSize - 2;

    public long runtime;

    private final Map<Chunk, Chunk> cache = new HashMap<>();

    private final Map<Integer, Integer> TTLs = new HashMap<>();

    private final int initTTL;

    private Life life;

    public Cache(int initTTL, int initChunkSize) {
        this.initTTL = initTTL;
        this.chunkSize = initChunkSize;
        this.cm1 = this.chunkSize - 1;
        this.cm2 = this.chunkSize - 2;
    }

    public void setLife(Life life) {
        this.life = life;
    }

    public int size() {
        return cache.size();
    }

    public void onTick() {
        long start = System.currentTimeMillis();
        cache.entrySet().stream().forEach(entry -> TTLs.put(entry.getKey().hashCode(), TTLs.get(entry.getKey().hashCode()) - 1));
        Set<Map.Entry<Integer, Integer>> TTLCopy = new HashSet<>(TTLs.entrySet());
        Map<Chunk, Chunk> cacheCopy = new HashMap<>(cache);
        TTLs.clear();
        cache.clear();
        TTLCopy.stream()
                .filter(entry -> entry.getValue() > 0)
                .forEach(entry -> TTLs.put(entry.getKey(), entry.getValue()));
        cacheCopy.entrySet().stream()
                .filter(entry -> TTLs.containsKey(entry.getKey().hashCode()))
                .forEach(entry -> cache.put(entry.getKey(), entry.getValue()));
        synchronized (life.CACHE_MUTEX) {
            runtime = System.currentTimeMillis() - start;
        }
    }

    // verified by Tester
    public List<Chunk> chunk(Set<Location> world) {
        List<Chunk> chunks = new LinkedList<>();
        Map<Location, Set<Location>> chunkData = new HashMap<>();
        synchronized (life.WORLD_MUTEX) {
            world.stream().forEach(loc -> {
                int x = loc.x / chunkSize * chunkSize, y = loc.y / chunkSize * chunkSize;
                if (loc.x < 0 && loc.x % chunkSize != 0) {
                    x -= chunkSize;
                }
                if (loc.y < 0 && loc.y % chunkSize != 0) {
                    y -= chunkSize;
                }
                Location chunkLoc = new Location(x, y);
                if (!chunkData.containsKey(chunkLoc)) {
                    chunkData.put(chunkLoc, new HashSet<>());
                }
                chunkData.get(chunkLoc).add(loc);
            });
        }
        chunkData.entrySet().stream().forEach(entry -> chunks.add(new Chunk(entry.getKey(), entry.getValue(), chunkSize)));
        if (life != null && life.frame.doDebug()) {
            chunkDebug(chunks);
        }
        return chunks;
    }

    public void chunkDebug(List<Chunk> chunks) {
        chunks.stream().forEach(chunk -> {
            new LifeDebug("From Gen " + Life.life.gen + ": " + chunk.toString(), chunk, false).init();
        });
    }

    public Set<Location> check(List<Chunk> chunks,
            Set<Location> next) {
        Set<Location> left = new HashSet<>();
        chunks.stream().forEach(chunk -> {
            Chunk chunkResult = cache.get(chunk);
            System.out.printf("Result for chunk %s: %s\n", chunk, chunkResult);
            if (chunkResult == null) {
                left.addAll(chunk.getLocal());
            } else {
                updateChunk(chunk, chunkResult, left, next);
            }
        });
        if (life.frame.doDebug()) {
            checkDebug(left);
        }
        return left;
    }

    public void checkDebug(Set<Location> left) {
        new LifeDebug("What's left after check", left).init();
    }

    // verified by Tester
    public void updateChunk(Chunk chunk, Chunk chunkResult, Set<Location> left,
            Set<Location> next) {
        Set<Location> after = new HashSet<>();
        int startX = chunk.getChunkLocation().x;
        int startY = chunk.getChunkLocation().y;
        int resultStartX = chunkResult.getChunkLocation().x;
        int resultStartY = chunkResult.getChunkLocation().y;
        for (int x = startX, resx = resultStartX, relx = 0; relx < chunkSize; x++, resx++, relx++) {
            for (int y = startY, resy = resultStartY, rely = 0; rely < chunkSize; y++, resy++, rely++) {
                Location loc = new Location(x, y);
                Location resloc = new Location(resx, resy);
                if (chunkResult.contains(resloc)) {
                    if (relx > 0 && relx < cm1 && rely > 0 && rely < cm1) {
                        next.add(loc);
                        after.add(loc);
                        if ((relx == 1 || relx == cm2 || rely == 1 || rely == cm2) && chunk.contains(loc)) {
                            left.add(loc);
                            //life.pop(loc);
                        }
                    } else if (chunk.contains(loc)) {
                        left.add(loc);
                    }
                } else if (chunk.contains(loc)) {
                    left.add(loc);
                }
            }
        }
        if (life.frame.doDebug()) {
            updateChunkDebug(chunk, chunkResult, after);
        }
    }

    public void updateChunkDebug(Chunk before, Chunk intended, Set<Location> after) {
        String beforeS = String.format("before chunk update: %s", before);
        String intendedS = String.format("intended result for %s: %s", before, intended);
        String afterS = String.format("actual result for: %s", before);
        new LifeDebug(beforeS, before, true).init();
        new LifeDebug(intendedS, intended, true).init();
        new LifeDebug(afterS, intended, true).init();
    }

    public void updateCache(List<Chunk> chunks, Set<Location> next) {
        chunks.stream().forEach(chunk -> {
            if (chunk.isUseful()) {
                int hash = chunk.hashCode();
                Chunk chunkResult = cache.get(chunk);
                if (chunkResult == null) {
                    Chunk result = getResult(chunk, next);
                    if (result.isUseful()) {
                        cache.put(chunk, result);
                        System.out.printf("Added cache result for %s: %s\n", chunk, result);
                        if (chunk.hashCode() == result.hashCode()) {
                            System.err.println("chunk mapped to itself in cache at gen " + life.gen);
                        }
                        if (life.frame.doDebug()) {
                            //updateCacheDebug(chunk, result);
                        }
                        TTLs.put(hash, initTTL);
                    }
                } else {
                    TTLs.put(hash, TTLs.get(hash) + 1);
                }
            }
        });
    }

    public void updateCacheDebug(Chunk key, Chunk val) {
        //String keyhex = Integer.toHexString(key.hashCode());
        //String valhex = Integer.toHexString(val.hashCode());
        String boobs = String.format("%s: %s", key, val);
        String keytit = String.format("key for %s", boobs);
        String valtit = String.format("val for %s", boobs);
        new LifeDebug(keytit, key, true).init();
        new LifeDebug(valtit, val, true).init();
    }

    public void debugCache() {
        cache.entrySet().stream().forEach(entry -> {
            updateCacheDebug(entry.getKey(), entry.getValue());
        });
    }

    // verified by Tester
    public Chunk getResult(Chunk chunk, Set<Location> next) {
        Set<Location> result = new HashSet<>();
        int startX = chunk.getChunkLocation().x;
        int stopX = startX + chunkSize;
        int startY = chunk.getChunkLocation().y;
        int stopY = startY + chunkSize;
        for (int x = startX; x < stopX; x++) {
            for (int y = startY; y < stopY; y++) {
                Location loc = new Location(x, y);
                if (next.contains(loc)) {
                    result.add(loc);
                }
            }
        }
        return new Chunk(chunk.getChunkLocation(), result, chunkSize);
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
