package com.github.blir;

/**
 *
 * @author Blir
 */
public class Neighbor {
    
    private final boolean alive;
    
    private final Location loc;
    
    public Neighbor(Location loc, boolean alive) {
        this.loc = loc;
        this.alive = alive;
    }
    
    public Location getLocation() {
        return loc;
    }
    
    public boolean isAlive() {
        return alive;
    }
}
