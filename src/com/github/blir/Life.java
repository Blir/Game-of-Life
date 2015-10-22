package com.github.blir;

import java.util.*;

/**
 *
 * @author Blir
 */
public class Life implements Runnable, LifeSource {

    public static final String CONWAY = "Conway's Game of Life";
    public static Life life;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        life = new Life();
        life.init();
        life.start();
    }

    public static Set<Location> neighbors1(Location loc) {
        Set<Location> neighbors = new HashSet<>();
        neighbors.add(new Location(loc.x + 1, loc.y));
        neighbors.add(new Location(loc.x, loc.y + 1));
        neighbors.add(new Location(loc.x + 1, loc.y + 1));
        neighbors.add(new Location(loc.x - 1, loc.y));
        neighbors.add(new Location(loc.x, loc.y - 1));
        neighbors.add(new Location(loc.x - 1, loc.y - 1));
        neighbors.add(new Location(loc.x + 1, loc.y - 1));
        neighbors.add(new Location(loc.x - 1, loc.y + 1));
        return neighbors;
    }

    public static Set<Location> neighbors2(Location loc) {
        Set<Location> neighbors = new HashSet<>();
        int xp1 = loc.x + 1;
        int yp1 = loc.y + 1;
        int xm1 = loc.x - 1;
        int ym1 = loc.y - 1;
        neighbors.add(new Location(xp1, loc.y));
        neighbors.add(new Location(loc.x, yp1));
        neighbors.add(new Location(xp1, yp1));
        neighbors.add(new Location(xm1, loc.y));
        neighbors.add(new Location(loc.x, ym1));
        neighbors.add(new Location(xm1, ym1));
        neighbors.add(new Location(xp1, ym1));
        neighbors.add(new Location(xm1, yp1));
        return neighbors;
    }

    public final Object DISPLAY_MUTEX = new Object(); // display runtime
    public final Object TICK_MUTEX = new Object(); // tick runtime
    public final Object WORLD_MUTEX = new Object(); // world
    public final Object POP_MUTEX = new Object(); // alivepop & deadpop
    public final Object CACHE_MUTEX = new Object(); // cache tick
    public final Object CHUNK_MUTEX = new Object(); // chunk size

    public final LifeFrame frame = new LifeFrame(javax.swing.JFrame.EXIT_ON_CLOSE);
    public final Set<Location> world = new HashSet<>();
    public final LifeListener listener = new LifeListener();
    public final Cache cache = new Cache(10, 5);

    public int delayMillis = 150;
    public long runtime;
    public int gen;
    public int pausegen = -1;
    public int alivepopsize = 0;
    public int deadpopsize = 0;
    public boolean paste;

    private long millis;

    private final Set<Location> next = new HashSet<>();
    private final Map<Location, Integer> alivepops = new HashMap<>(); // for rules 1,2,3
    private final Map<Location, Integer> deadpops = new HashMap<>(); // for rule 4

    private Thread titleUpdateThread;
    private Thread repaintThread;
    private Thread lifeThread;

    public void init() {
        listener.setLife(this);
        frame.init(this);
        frame.getLifePanel().init(this);
        cache.setLife(this);
    }

    public void start() {
        frame.setVisible(true);
        (titleUpdateThread = new Thread(new TitleUpdateTask())).start();
        (repaintThread = new Thread(new RepaintTask())).start();
    }

    public void restart() {
        (lifeThread = new Thread(this)).start();
    }

    public void interrupt() {
        synchronized (WORLD_MUTEX) {
            lifeThread.interrupt();
            frame.setState(false);
        }
    }

    public void loadDesign() {
        if (frame.isRunning()) {
            paste = true;
        } else {
            paste();
        }
    }

    public void paste() {
        synchronized (WORLD_MUTEX) {
            LifePanel panel = frame.getLifePanel();
            frame.getClipboard().stream().forEach(loc -> world.add(new Location(panel.camX + loc.x, panel.camY + loc.y)));
        }
    }

    void pop(Location loc) {
        if (world.contains(loc)) {
            alivepops.put(loc, alivepops.getOrDefault(loc, 0) + 1);
        } else {
            deadpops.put(loc, deadpops.getOrDefault(loc, 0) + 1);
        }
    }

    @Override
    public void run() {
        try {
            while (frame.isRunning()) {
                //System.out.println("Tick");
                millis = System.currentTimeMillis();

                if (paste) {
                    paste = false;
                    paste();
                }

                List<Chunk> chunks = frame.useCache() ? cache.chunk(world) : null;
                Set<Location> left = frame.useCache() ? cache.check(chunks, next) : world;

                // should be O(8n)
                synchronized (WORLD_MUTEX) {
                    left.stream().map(Life::neighbors1).forEach(neighbors -> neighbors.stream().forEach(this::pop));
                }

                synchronized (POP_MUTEX) {
                    deadpopsize = deadpops.size();
                    alivepopsize = alivepops.size();
                }

                deadpops.entrySet().stream()
                        .filter(entry -> entry.getValue() == 3)
                        .forEach(entry -> next.add(entry.getKey()));

                alivepops.entrySet().stream()
                        .filter(entry -> entry.getValue() == 2 || entry.getValue() == 3)
                        .forEach(entry -> next.add(entry.getKey()));

                if (frame.doDebug()) {
                    new LifeDebug("next before cache update", next).init();
                }

                synchronized (WORLD_MUTEX) {
                    world.clear();
                    world.addAll(next);
                }

                if (frame.useCache()) {
                    cache.updateCache(chunks, next);
                }

                next.clear();
                alivepops.clear();
                deadpops.clear();

                if (frame.useCache()) {
                    cache.onTick();
                }

                if (frame.doDebug()) {
                    cache.debugCache();
                }

                //System.out.println(cache);
                synchronized (TICK_MUTEX) {
                    runtime = System.currentTimeMillis() - millis;
                }
                if (delayMillis > 0) {
                    Thread.sleep(delayMillis);
                }
                gen++;
                if (pausegen == gen && pausegen != -1) {
                    frame.setState(false);
                    pausegen = -1;
                }
            }
        } catch (InterruptedException ex) {
            System.err.println("Tick interrupted.");
        }
    }

    @Override
    public int getGridSize() {
        return cache.chunkSize;
    }

    @Override
    public void addListeners(LifePanel panel) {
        panel.addFocusListener(listener);
        panel.addMouseListener(listener);
        panel.addMouseMotionListener(listener);
        panel.addMouseWheelListener(listener);
    }

    @Override
    public boolean useColorGuides() {
        return frame.showColorGuides();
    }

    @Override
    public Object getWorldMutex() {
        return WORLD_MUTEX;
    }

    @Override
    public Object getRuntimeMutex() {
        return DISPLAY_MUTEX;
    }

    @Override
    public boolean worldContains(int x, int y) {
        return world.contains(new Location(x, y));
    }

    public Location getFurthest(Direction dir) {
        synchronized (WORLD_MUTEX) {
            Location furthest = null;
            switch (dir) {
                case UP:
                    for (Location loc : world) {
                        if (furthest == null || loc.y < furthest.y) {
                            furthest = loc;
                        }
                    }
                    return furthest;
                case RIGHT:
                    for (Location loc : world) {
                        if (furthest == null || loc.x > furthest.x) {
                            furthest = loc;
                        }
                    }
                    return furthest;
                case LEFT:
                    for (Location loc : world) {
                        if (furthest == null || loc.x < furthest.x) {
                            furthest = loc;
                        }
                    }
                    return furthest;
                case DOWN:
                    for (Location loc : world) {
                        if (furthest == null || loc.y > furthest.y) {
                            furthest = loc;
                        }
                    }
                    return furthest;
                default:
                    throw new IllegalStateException("illegal direction");
            }
        }
    }

    private class TitleUpdateTask implements Runnable {

        @Override
        public void run() {
            long t, d, c;
            int s, ap, dp, cs;
            LifePanel panel = frame.getLifePanel();
            try {
                for (;;) {
                    //System.out.println("TitleUpdate");
                    synchronized (TICK_MUTEX) {
                        t = runtime;
                    }
                    synchronized (DISPLAY_MUTEX) {
                        d = panel.runtime;
                    }
                    synchronized (WORLD_MUTEX) {
                        s = world.size();
                    }
                    synchronized (CACHE_MUTEX) {
                        c = cache.runtime;
                        cs = cache.size();
                    }
                    synchronized (POP_MUTEX) {
                        ap = alivepopsize;
                        dp = deadpopsize;
                    }
                    if (frame.useCache()) {
                        frame.setTitle(String.format("%s (x:%d,y:%d)(t:%3dms,d:%3dms)(g:%5d)(s:%6d)(a:%4d)(d:%4d)(z:%d)(c:%d,cs:%d)",
                                CONWAY, panel.camX, panel.camY, t, d, gen, s, ap, dp, panel.objectSize, c, cs));
                    } else {
                        frame.setTitle(String.format("%s (x:%d,y:%d)(t:%3dms,d:%3dms)(g:%5d)(s:%6d)(a:%4d)(d:%4d)(z:%d)",
                                CONWAY, panel.camX, panel.camY, t, d, gen, s, ap, dp, panel.objectSize));
                    }
                    Thread.sleep(delayMillis > 0 ? delayMillis * 5 : 5);
                }
            } catch (InterruptedException ex) {
                System.err.println("Title Updater interrupted.");
            }
        }
    }

    private class RepaintTask implements Runnable {

        @Override
        public void run() {
            try {
                for (;;) {
                    //System.out.println("Repaint");
                    if (frame.doRender()) {
                        frame.repaint();
                        Thread.sleep(delayMillis > 0 ? delayMillis : 1);
                    } else {
                        Thread.sleep(delayMillis > 0 ? delayMillis * 5 : 100);
                    }
                }
            } catch (InterruptedException ex) {
                System.err.println("Repainter interrupted.");
            }
        }
    }
}
