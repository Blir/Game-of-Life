package com.github.blir;

import com.github.blir.gui.LifeFrame;
import com.github.blir.gui.LifeListener;
import com.github.blir.gui.LifePanel;
import java.util.Set;

/**
 *
 * @author Blir
 */
public interface LifeSource {

    public int getGridSize();

    public void addListeners(LifePanel panel);

    public boolean useColorGuides();

    public Object getWorldMutex();

    public Object getRenderRuntimeMutex();
    
    public Object getPrepareRenderRuntimeMutex();

    public boolean worldContains(int x, int y);
    
    public Set<Location> getWorld();
    
    public LifeListener getListener();
    
    public LifeFrame getFrame();
}
