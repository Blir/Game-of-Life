package com.github.blir.gui;

import com.github.blir.Life;
import com.github.blir.Location;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author Blir
 */
public class LifePanel extends JPanel {

    public static final Color highlightColor = new Color(255, 255, 0, 192);
    
    public int camX, camY, objectSize = 10;
    public long renderRuntime, prepareRenderRuntime;

    private Pixel[][] worldView;

    private final Object PAINT_MUTEX = new Object();

    private int xObjects, yObjects;
    private int xOffset, yOffset;
    private int aggregateSize;

    private boolean copy;
    private int cx, cy, xs, ys;

    private long renderMillis, prepareRenderMillis;
    private Life life;

    public void init(Life life) {
        this.life = life;
        life.addListeners(this);
        this.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles.size() > 0) {
                        life.getFrame().open(droppedFiles.get(0));
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void prepareRender() {
        prepareRenderMillis = System.currentTimeMillis();
        synchronized (PAINT_MUTEX) {
            if (objectSize > 0) {
                prepareRenderDiscrete();
            } else {
                prepareRenderAggregate();
            }
        }
        Highlight highlight = life.getListener().getHighlight();
        copy = highlight != null;
        if (highlight != null) {
            cx = highlight.getLoc1().x;
            cy = highlight.getLoc1().y;
            xs = highlight.getLoc2().x - cx;
            ys = highlight.getLoc2().y - cy;
        }
        synchronized (life.getRenderRuntimeMutex()) {
            prepareRenderRuntime = System.currentTimeMillis() - prepareRenderMillis;
        }
    }

    void prepareRenderDiscrete() {
        xObjects = getWidth() / objectSize;
        yObjects = getHeight() / objectSize;
        xOffset = camX - (xObjects >>> 1);
        yOffset = camY - (yObjects >>> 1);
        worldView = new Pixel[xObjects][yObjects];
        Set<Location> world;
        synchronized (life.getWorldMutex()) {
            world = new HashSet<>(life.getWorld());
        }
        Pixel cell = new Pixel(Color.BLACK, objectSize);
        world.stream().forEach(loc -> {
            int dispX = loc.x - xOffset;
            int dispY = loc.y - yOffset;
            if (dispX >= 0 && dispY >= 0 && dispX < xObjects && dispY < yObjects) {
                worldView[dispX][dispY] = cell;
            }
        });
        if (life.useColorGuides() && objectSize >= 4) {
            prepareColorGuides();
        }
    }
    
    void prepareColorGuides() {
        Pixel empty = new Pixel(Color.LIGHT_GRAY, objectSize - 1);
        Pixel origin = new Pixel(Color.RED, objectSize - 1);
        Pixel cam = new Pixel(Color.CYAN, objectSize - 1);
        Pixel grid = new Pixel(Color.GRAY, objectSize - 1);
        int x, locX, y, locY;
        for (x = 0, locX = xOffset; x < xObjects; x++, locX++) {
            for (y = 0, locY = yOffset; y < yObjects; y++, locY++) {
                if (worldView[x][y] == null) {
                    if (locX == 0 && locY == 0) {
                        worldView[x][y] = origin;
                    } else if (locX == camX && locY == camY) {
                        worldView[x][y] = cam;
                    } else if (locX % life.getGridSize() == 0 && locY % life.getGridSize() == 0) {
                        worldView[x][y] = grid;
                    } else {
                        worldView[x][y] = empty;
                    }
                }
            }
        }
    }

    void prepareRenderAggregate() {
        aggregateSize = 2 - objectSize;
        xObjects = aggregateSize * getWidth();
        yObjects = aggregateSize * getHeight();
        xOffset = camX - (xObjects >>> 1);
        yOffset = camY - (yObjects >>> 1);
        int width = getWidth();
        int height = getHeight();
        worldView = new Pixel[width][height];
        //Map<Location, Integer> aggregates = new HashMap<>();
        Set<Location> world;
        synchronized (life.getWorldMutex()) {
            world = new HashSet<>(life.getWorld());
        }
        Pixel cell = new Pixel(Color.DARK_GRAY, 1);
        world.stream().forEach(loc -> {
            int dispX = (loc.x - xOffset) / aggregateSize;
            int dispY = (loc.y - yOffset) / aggregateSize;
            if (dispX >= 0 && dispY >= 0 && dispX < width && dispY < height) {
                worldView[dispX][dispY] = cell;
                //Location drawLoc = new Location(dispX, dispY);
                //aggregates.put(drawLoc, aggregates.getOrDefault(drawLoc, 0) + 1);
            }
        });
        /*
         int sqAggregate = aggregateSize * aggregateSize;
         aggregates.entrySet().stream().forEach(entry -> {
         int alpha = (int) (255 * ((double) entry.getValue() / sqAggregate));
         if (alpha < 0 || alpha > 255) {
         System.err.println("invalid alpha: " + alpha);
         }
         alpha = Math.max(0, Math.min(255, alpha));
         worldView[entry.getKey().x][entry.getKey().y] = new Color(64, 64, 64, alpha);
         });*/
    }

    @Override
    public void paintComponent(Graphics g) {
        renderMillis = System.currentTimeMillis();
        if (worldView == null) {
            return;
        }
        synchronized (PAINT_MUTEX) {
            int draw = objectSize > 0 ? objectSize : 1;
            int viewX, viewY, drawX, drawY;
            for (viewX = 0, drawX = 0; viewX < worldView.length; viewX++, drawX += draw) {
                for (viewY = 0, drawY = 0; viewY < worldView[viewX].length; viewY++, drawY += draw) {
                    if (worldView[viewX][viewY] != null) {
                        Pixel pixel = worldView[viewX][viewY];
                        g.setColor(pixel.getColor());
                        g.fillRect(drawX, drawY, pixel.getSize(), pixel.getSize());
                    }
                }
            }
        }
        if (copy) {
            g.setColor(highlightColor);
            g.fillRect(cx, cy, xs, ys);
        }
        synchronized (life.getRenderRuntimeMutex()) {
            renderRuntime = System.currentTimeMillis() - renderMillis;
        }
    }
}
