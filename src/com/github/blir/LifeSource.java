package com.github.blir;

/**
 *
 * @author Blir
 */
public interface LifeSource {

    public int getGridSize();

    public void addListeners(LifePanel panel);

    public boolean useColorGuides();

    public Object getWorldMutex();

    public Object getRuntimeMutex();

    public boolean worldContains(int x, int y);
}
