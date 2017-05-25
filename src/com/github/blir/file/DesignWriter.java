package com.github.blir.file;

import com.github.blir.Location;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.*;

/**
 *
 * @author Blir
 */
public class DesignWriter {

    private final File file;
    private final DesignType designType;

    public DesignWriter(File file) throws FileNotFoundException {
        this.file = file;
        String s = file.getPath();
        int delim = s.lastIndexOf('.');
        s = s.substring(delim + 1).toUpperCase();
        designType = DesignType.valueOf(s);
    }

    public void write(Design design) throws IOException {
        switch (designType) {
            case GOL:
                writeGOL(design);
                break;
            case JSON:
                writeJSON(design);
                break;
            default:
                throw new UnsupportedOperationException("invalid design type");
        }
    }
    
    private void writeJSON(Design design) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("generation", design.getGeneration());
        JsonArray arr = new JsonArray();
        Gson gson = new Gson();
        design.getDesign().forEach(loc -> {
            JsonElement elem = gson.toJsonTree(loc, Location.class);
            arr.add(elem);
        });
        object.add("aliveCells", arr);
        String json = gson.toJson(object);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        }
    }
    
    private void writeGOL(Design design) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeInt(design.getDesign().size());
            for (Location loc : design.getDesign()) {
                out.writeInt(loc.x);
                out.writeInt(loc.y);
            }
            System.out.println(design);
        }
    }
}
