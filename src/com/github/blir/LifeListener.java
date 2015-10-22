package com.github.blir;

import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author Blir
 */
public class LifeListener implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener, FocusListener {

    private Life life;
    private boolean clickMode;
    private Location grab;

    public void setLife(Life life) {
        this.life = life;
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
                if (life.world.contains(loc)) {
                    synchronized (life.WORLD_MUTEX) {
                        life.world.remove(loc);
                    }
                } else {
                    synchronized (life.WORLD_MUTEX) {
                        life.world.add(loc);
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
        clickMode = life.world.contains(parseLocation(e.getX(), e.getY()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        life.frame.setCursor(Cursor.getDefaultCursor());
        grab = null;
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
        panel.objectSize -= e.getWheelRotation();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Location loc = parseLocation(e.getX(), e.getY());
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (clickMode) {
                synchronized (life.WORLD_MUTEX) {
                    life.world.remove(loc);
                }
            } else {
                synchronized (life.WORLD_MUTEX) {
                    life.world.add(loc);
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

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        LifePanel panel = life.frame.getLifePanel();
        int shamt = panel.objectSize > 0
                ? (panel.getWidth() / panel.objectSize) / 8
                : (panel.getWidth() * (2 - panel.objectSize) / 8);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                panel.camY -= shamt;
                break;
            case KeyEvent.VK_LEFT:
                panel.camX -= shamt;
                break;
            case KeyEvent.VK_RIGHT:
                panel.camX += shamt;
                break;
            case KeyEvent.VK_DOWN:
                panel.camY += shamt;
                break;
        }
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
