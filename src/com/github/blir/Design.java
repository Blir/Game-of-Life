package com.github.blir;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Blir
 */
public class Design {
    
    public static Set<Location> make(Set<Location> base, int x, int y) {
        Set<Location> design = new HashSet<>();
        base.stream().forEach(loc -> design.add(new Location(loc.x - x, loc.y - y)));
        return design;
    }
}
