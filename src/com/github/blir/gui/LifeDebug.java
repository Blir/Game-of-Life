package com.github.blir.gui;

import com.github.blir.Life;
import com.github.blir.LifeSource;
import com.github.blir.Location;
import com.github.blir.cache.Chunk;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Blir
 */
public class LifeDebug implements LifeSource, WindowListener {
 
    private static int instanceCount = 0;
    private static final int maxInstanceCount = 5;
    
    private final LifeFrame frame = new LifeFrame(javax.swing.JFrame.DISPOSE_ON_CLOSE);
    private final Set<Location> world = new HashSet<>();
    private final String title;
    
    public LifeDebug(String title, Set<Location> world) {
        this.title = title;
        this.world.addAll(world);
    }
    
    public LifeDebug(String title, Chunk chunk, boolean relative) {
        this.title = title;
        this.world.addAll(relative ? chunk.relative() : chunk.getLocal());
    }
    
    public void init() {
        if (instanceCount >= maxInstanceCount) {
            Life.life.frame.setState(false);
        }
        instanceCount++;
        frame.init(Life.life);
        frame.getLifePanel().init(this);
        frame.setTitle(title);
        frame.setVisible(true);
    }

    @Override
    public int getGridSize() {
        return Life.life.getGridSize();
    }

    @Override
    public void addListeners(LifePanel panel) {
    }

    @Override
    public boolean useColorGuides() {
        return true;
    }

    @Override
    public Object getWorldMutex() {
        return new Object();
    }

    @Override
    public Object getRenderRuntimeMutex() {
        return new Object();
    }
    
    @Override
    public Object getPrepareRenderRuntimeMutex() {
        return new Object();
    }
    
    @Override
    public LifeListener getListener() {
        return Life.life.listener;
    }
    
    @Override
    public LifeFrame getFrame() {
        return frame;
    }

    @Override
    public boolean worldContains(int x, int y) {
        return world.contains(new Location(x, y));
    }
    
    @Override
    public Set<Location> getWorld() {
        return world;
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        instanceCount--;
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
