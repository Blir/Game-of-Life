package com.github.blir.file;

import com.github.blir.Location;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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
    
    public static Stream<Location> make(Stream<Location> base, int x, int y) {
        return base.map(loc -> new Location(loc.x - x, loc.y - y));
    }
}
