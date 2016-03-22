package com.github.blir;

import java.awt.Color;

/**
 *
 * @author Travis
 */
public class Cell {
    
    private Location loc;
    private Color color;
    
    public Cell(Location loc, Color color) {
        this.loc = loc;
        this.color = color;
    }

    /**
     * @return the loc
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * @param loc the loc to set
     */
    public void setLocation(Location loc) {
        this.loc = loc;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public int hashCode() {
        return loc.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Cell ? ((Cell)obj).getLocation().equals(this.getLocation()) : false;
    }
}
