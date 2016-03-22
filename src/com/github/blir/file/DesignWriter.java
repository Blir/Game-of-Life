package com.github.blir.file;

import com.github.blir.Location;
import java.io.*;
import java.util.Set;

/**
 *
 * @author Blir
 */
public class DesignWriter {

    private final DataOutputStream out;

    public DesignWriter(File file) throws FileNotFoundException {
        out = new DataOutputStream(new FileOutputStream(file));
    }

    public void write(Set<Location> design) throws IOException {
        out.writeInt(design.size());
        for (Location loc : design) {
            out.writeInt(loc.x);
            out.writeInt(loc.y);
        }
        System.out.println(design);
        out.close();
    }
}
