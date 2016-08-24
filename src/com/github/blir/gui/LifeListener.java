package com.github.blir.gui;

import com.github.blir.Life;
import com.github.blir.Location;
import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author Blir
 */
public class LifeListener implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener, FocusListener {

    private Life life;
    private boolean clickMode, copy;
    private Location grab, copy1, copy2;

    public void setLife(Life life) {
        this.life = life;
    }

    public void copy() {
        copy = true;
        life.frame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    public Highlight getHighlight() {
        return copy1 != null && copy2 != null ? new Highlight(copy1, copy2) : null;
    }

    Location parseLocation(int x, int y) {
        LifePanel panel = life.frame.getLifePanel();
        if (panel.objectSize > 0) {
            x = (x + 1) / panel.objectSize + panel.camX - (panel.getWidth() / panel.objectSize / 2);
            y = (y + 1) / panel.objectSize + panel.camY - (panel.getHeight() / panel.objectSize / 2);
        } else {
            int aggregateSize = 2 - panel.objectSize;
            x = (x + 1) * aggregateSize + panel.camX - (panel.getWidth() * aggregateSize / 2);
            y = (y + 1) * aggregateSize + panel.camY - (panel.getHeight() * aggregateSize / 2);
        }
        return new Location(x, y);
    }

    Location screenLocationFor(Location loc) {
        LifePanel panel = life.frame.getLifePanel();
        int x, y;
        if (panel.objectSize > 0) {
            x = (loc.x + (panel.getWidth() / panel.objectSize / 2) - panel.camX) * panel.objectSize;
            y = (loc.y + (panel.getHeight() / panel.objectSize / 2) - panel.camY) * panel.objectSize;
        } else {
            int aggregateSize = 2 - panel.objectSize;
            x = (loc.x + (panel.getWidth() * aggregateSize / 2) - panel.camX) / aggregateSize;
            y = (loc.y + (panel.getHeight() * aggregateSize / 2) - panel.camY) / aggregateSize;
        }
        return new Location(x, y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Location loc = parseLocation(e.getX(), e.getY());
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                synchronized (life.WORLD_MUTEX) {
                    if (life.getWorld().contains(loc)) {
                            life.getWorld().remove(loc);
                    } else {
                            life.getWorld().add(loc);
                    }
                }
                break;
            case MouseEvent.BUTTON3:
                LifePanel panel = life.frame.getLifePanel();
                panel.camX = loc.x;
                panel.camY = loc.y;
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (copy) {
            copy1 = new Location(e.getX(), e.getY());
        } else {
            synchronized (life.WORLD_MUTEX) {
                clickMode = life.getWorld().contains(parseLocation(e.getX(), e.getY()));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (copy) {
            copy1 = parseLocation(copy1.x, copy1.y);
            copy2 = parseLocation(e.getX(), e.getY());
            life.frame.setCursor(Cursor.getDefaultCursor());
            int x1 = Math.min(copy1.x, copy2.x);
            int x2 = Math.max(copy1.x, copy2.x);
            int y1 = Math.min(copy1.y, copy2.y);
            int y2 = Math.max(copy1.y, copy2.y);
            synchronized (life.getWorldMutex()) {
                life.getFrame().setClipboard(life.getWorld().stream()
                        .filter(loc -> {
                            return loc.x >= x1 && loc.x <= x2 && loc.y >= y1 && loc.y <= y2;
                        }));
            }
            copy = false;
            copy1 = null;
            copy2 = null;
        } else {
            life.frame.setCursor(Cursor.getDefaultCursor());
            grab = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        LifePanel panel = life.frame.getLifePanel();
        panel.objectSize -= e.getWheelRotation() * (1 + Math.abs(10 - panel.objectSize) / 8);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Location loc = parseLocation(e.getX(), e.getY());
        if (copy) {
            copy2 = new Location(e.getX(), e.getY());
        } else {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (clickMode) {
                    synchronized (life.WORLD_MUTEX) {
                        life.getWorld().remove(loc);
                    }
                } else {
                    synchronized (life.WORLD_MUTEX) {
                        life.getWorld().add(loc);
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (grab == null) {
                    grab = loc;
                } else {
                    life.frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    LifePanel panel = life.frame.getLifePanel();
                    panel.camX += grab.x - loc.x;
                    panel.camY += grab.y - loc.y;
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}
