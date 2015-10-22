package com.github.blir;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Blir
 */
public class LifePanel extends JPanel {

    public int camX, camY, objectSize = 10;
    public long runtime;

    private int xObjects, yObjects;
    private int xOffset, yOffset;
    private int dispXEnd, dispYEnd;
    private int aggregateSize;

    private long millis;
    private LifeSource life;

    public void init(LifeSource life) {
        this.life = life;
        life.addListeners(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (life == null) {
            return;
        }
        if (objectSize > 0) {
            paintDiscrete(g);
        } else {
            paintAggregate(g);
        }
    }

    public void paintDiscrete(Graphics g) {
        millis = System.currentTimeMillis();
        xObjects = getWidth() / objectSize;
        yObjects = getHeight() / objectSize;
        xOffset = camX - (xObjects >>> 1);
        yOffset = camY - (yObjects >>> 1);
        dispXEnd = objectSize * xObjects;
        dispYEnd = objectSize * yObjects;
        if (objectSize >= 10) {
            int sm2 = objectSize - 2;
            int x, locX, y, locY;
            for (x = 1, locX = xOffset; x < dispXEnd; x += objectSize, locX++) {
                for (y = 1, locY = yOffset; y < dispYEnd; y += objectSize, locY++) {
                    if (!life.useColorGuides()) {
                        g.setColor(Color.LIGHT_GRAY);
                    } else if (locX == camX && locY == camY) {
                        g.setColor(Color.CYAN);
                    } else if (locX % life.getGridSize() == 0 && locY % life.getGridSize() == 0) {
                        g.setColor(Color.GREEN);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    g.fillRect(x, y, sm2, sm2);
                }
            }
        }
        g.setColor(Color.DARK_GRAY);
        synchronized (life.getWorldMutex()) {
            int locX, locY, dispX, dispY;
            for (locX = xOffset, dispX = 0; dispX < dispXEnd; locX++, dispX += objectSize) {
                for (locY = yOffset, dispY = 0; dispY < dispYEnd; locY++, dispY += objectSize) {
                    if (life.worldContains(locX, locY)) {
                        g.fillRect(dispX, dispY, objectSize, objectSize);
                    }
                }
            }
        }
        synchronized (life.getRuntimeMutex()) {
            runtime = System.currentTimeMillis() - millis;
        }
    }

    public void paintAggregate(Graphics g) {
        if (life == null) {
            return;
        }
        millis = System.currentTimeMillis();
        aggregateSize = 2 - objectSize;
        xObjects = getWidth() * aggregateSize;
        yObjects = getHeight() * aggregateSize;
        xOffset = camX - (xObjects >>> 1);
        yOffset = camY - (yObjects >>> 1);
        dispXEnd = getWidth();
        dispYEnd = getHeight();
        synchronized (life.getWorldMutex()) {
            int locX, locY, dispX, dispY, objects, locXEnd, locYEnd;
            double ratio;
            for (dispX = 0; dispX < dispXEnd; dispX++) {
                for (dispY = 0; dispY < dispYEnd; dispY++) {
                    objects = 0;
                    for (locX = xOffset + aggregateSize * dispX, locXEnd = locX + aggregateSize; locX < locXEnd ; locX++) {
                        for (locY = yOffset + aggregateSize * dispY, locYEnd = locY + aggregateSize; locY < locYEnd; locY++) {
                            if (life.worldContains(locX, locY)) {
                                objects++;
                            }
                        }
                    }
                    if (objects > 0) {
                        ratio = (double) objects / (aggregateSize * aggregateSize);
                        g.setColor(new Color(64, 64, 64, (int)(255 * ratio)));
                        g.fillRect(dispX, dispY, 1, 1);
                    }
                }
            }
        }
        synchronized (life.getRuntimeMutex()) {
            runtime = System.currentTimeMillis() - millis;
        }
    }
}
