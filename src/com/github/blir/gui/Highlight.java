package com.github.blir.gui;

import com.github.blir.Location;

/**
 *
 * @author Blir
 */
public class Highlight {
    
    private final Location loc1, loc2;

    public Highlight(Location loc1, Location loc2) {
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public Location getLoc1() {
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }
}
