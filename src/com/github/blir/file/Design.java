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
    
    private Set<Location> design;
    private int generation;
    
    public Design() {
    }
    
    public Design(Set<Location> design, int generation) {
        this.design = design;
        this.generation = generation;
    }

    /**
     * @return the design
     */
    public Set<Location> getDesign() {
        return design;
    }

    /**
     * @param design the design to set
     */
    public void setDesign(Set<Location> design) {
        this.design = design;
    }

    /**
     * @return the generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * @param generation the generation to set
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }
    
    @Override
    public String toString() {
        return design.toString();
    }
}
