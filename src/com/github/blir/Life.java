package com.github.blir;

import com.github.blir.gui.LifeFrame;
import com.github.blir.gui.LifeListener;
import com.github.blir.gui.LifePanel;
import java.util.*;

/**
 *
 * @author Blir
 */
public class Life implements Runnable {

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
    
    public List<Neighbor> neighbors3(Location loc) {
        Location l;
        neighbors.add(new Neighbor(l = new Location(loc.x + 1, loc.y), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x, loc.y + 1), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x + 1, loc.y + 1), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x - 1, loc.y), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x, loc.y - 1), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x - 1, loc.y - 1), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x + 1, loc.y - 1), world.contains(l)));
        neighbors.add(new Neighbor(l = new Location(loc.x - 1, loc.y + 1), world.contains(l)));
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

    public final Object RENDER_MUTEX = new Object(); // display runtime
    public final Object PREPARE_RENDER_MUTEX = new Object();
    public final Object TICK_MUTEX = new Object(); // tick runtime
    public final Object WORLD_MUTEX = new Object(); // world
    public final Object POP_MUTEX = new Object(); // alivepop & deadpop
    public final Object CACHE_MUTEX = new Object(); // cache tick
    public final Object CHUNK_MUTEX = new Object(); // chunk size

    public final LifeFrame frame = new LifeFrame(javax.swing.JFrame.EXIT_ON_CLOSE);
    private final Set<Location> world = new HashSet<>();
    public final LifeListener listener = new LifeListener();

    public int delayMillis = 150;
    public long runtime;
    public int gen;
    public int pausegen = -1;
    public int alivePopSize;
    public int deadPopSize;
    public boolean paste;

    private long millis;

    private final List<Location> next = new ArrayList<>();
    private List<Neighbor> neighbors = new ArrayList<>();
    private final Map<Location, Counter> aliveNeighbors = new HashMap<>(); // for rules 1,2,3
    private final Map<Location, Counter> deadNeighbors = new HashMap<>(); // for rule 4

    private Thread titleUpdateThread;
    private Thread repaintThread;
    private Thread lifeThread;

    public void init() {
        listener.setLife(this);
        frame.init(this);
        frame.getLifePanel().init(this);
    }

    public void start() {
        frame.setVisible(true);
        (titleUpdateThread = new Thread(new TitleUpdateTask(), "TitleUpdate")).start();
        (repaintThread = new Thread(new RepaintTask(), "Repaint")).start();
    }

    public void restart() {
        (lifeThread = new Thread(this, "Life")).start();
        if (!titleUpdateThread.isAlive()) {
            (titleUpdateThread = new Thread(new TitleUpdateTask(), "TitleUpdate")).start();
        }
        if (!repaintThread.isAlive()) {
            (repaintThread = new Thread(new RepaintTask(), "Repaint")).start();
        }
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
    
    public void clearWorld() {
        synchronized (life.WORLD_MUTEX) {
            world.clear();
            gen = 0;
        }
        neighbors = new ArrayList<>();
    }
    
    void populate(Neighbor neighbor) {
        Location loc = neighbor.getLocation();
        Map<Location, Counter> neighbors = (neighbor.isAlive() ? aliveNeighbors : deadNeighbors);
        Counter counter;
        synchronized (neighbors) {
            counter = neighbors.get(loc);
            if (counter == null) {
                neighbors.put(loc, counter = new Counter());
            }
        }
        counter.increment();
    }
    
    void populate() {
        neighbors.clear();
        
        synchronized (WORLD_MUTEX) {
            world.stream().forEach(loc -> this.neighbors3(loc));
        }
        neighbors.stream()
                .parallel()
                .forEach(this::populate);
    }

    void applyRules() {
        deadNeighbors.entrySet().stream()
                .filter(entry -> entry.getValue().count() == 3)
                .forEach(entry -> next.add(entry.getKey()));

        aliveNeighbors.entrySet().stream()
                .filter(entry -> {
                    int count = entry.getValue().count();
                    return count == 2 || count == 3;
                })
                .forEach(entry -> next.add(entry.getKey()));
    }

    void updateWorld() {
        synchronized (WORLD_MUTEX) {
            world.clear();
            world.addAll(next);
        }
    }

    void clearState() {
        next.clear();
        aliveNeighbors.clear();
        deadNeighbors.clear();
    }

    @Override
    public void run() {
        try {
            while (frame.isRunning()) {

                millis = System.currentTimeMillis();

                if (paste) {
                    paste = false;
                    paste();
                }

                populate();

                synchronized (POP_MUTEX) {
                    deadPopSize = deadNeighbors.size();
                    alivePopSize = aliveNeighbors.size();
                }
                
                applyRules();

                updateWorld();

                clearState();

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

    public int getGridSize() {
        return 5;
    }

    public void addListeners(LifePanel panel) {
        panel.addFocusListener(listener);
        panel.addMouseListener(listener);
        panel.addMouseMotionListener(listener);
        panel.addMouseWheelListener(listener);
    }

    public boolean useColorGuides() {
        return frame.showColorGuides();
    }

    public Object getWorldMutex() {
        return WORLD_MUTEX;
    }

    public Object getRenderRuntimeMutex() {
        return RENDER_MUTEX;
    }

    public Object getPrepareRenderRuntimeMutex() {
        return PREPARE_RENDER_MUTEX;
    }
    
    public LifeListener getListener() {
        return listener;
    }
    
    public LifeFrame getFrame() {
        return this.frame;
    }

    public boolean worldContains(int x, int y) {
        return world.contains(new Location(x, y));
    }

    public Set<Location> getWorld() {
        return world;
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
            long t, d, pd;
            int s, ap, dp;
            LifePanel panel = frame.getLifePanel();
            try {
                for (;;) {
                    //System.out.println("TitleUpdate");
                    synchronized (TICK_MUTEX) {
                        t = runtime;
                    }
                    synchronized (RENDER_MUTEX) {
                        d = panel.renderRuntime;
                    }
                    synchronized (PREPARE_RENDER_MUTEX) {
                        pd = panel.prepareRenderRuntime;
                    }
                    synchronized (WORLD_MUTEX) {
                        s = world.size();
                    }
                    synchronized (POP_MUTEX) {
                        ap = alivePopSize;
                        dp = deadPopSize;
                    }
                    frame.setTitle(String.format("%s (x:%d,y:%d)(t:%3dms,p:%3dms,d:%3dms)(g:%5d)(s:%6d)(a:%4d)(d:%4d)(z:%d)",
                            CONWAY, panel.camX, panel.camY, t, pd, d, gen, s, ap, dp, panel.objectSize));
                    Thread.sleep(20);
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
                        frame.getLifePanel().prepareRender();
                        frame.repaint();
                        Thread.sleep(20);
                    } else {
                        Thread.sleep(100);
                    }
                }
            } catch (InterruptedException ex) {
                System.err.println("Repainter interrupted.");
            }
        }
    }
}
