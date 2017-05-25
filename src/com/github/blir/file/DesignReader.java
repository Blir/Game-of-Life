package com.github.blir.file;

import com.github.blir.Location;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Blir
 */
public class DesignReader {

    private final File file;
    private final DesignType designType;

    public DesignReader(File file) throws FileNotFoundException {
        this.file = file;
        String s = file.getPath();
        int delim = s.indexOf('.');
        s = s.substring(delim + 1);
        designType = DesignType.valueOf(s.toUpperCase());
    }

    public Design read() throws IOException {
        Design design = new Design();
        switch (designType) {
            case RLE:
                design.setDesign(readRLE());
                break;
            case GOL:
                design.setDesign(readGOL());
                break;
            case JSON:
                design = readJSON();
                break;
            default:
                throw new UnsupportedOperationException("invalid design type");
        }
        return design;
    }
    
    private Design readJSON() throws IOException {
        Design design = new Design();
        Set<Location> locs = new HashSet<>();
        Gson gson = new Gson();
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            JsonObject object = gson.fromJson(in, JsonObject.class);
            design.setGeneration(object.getAsJsonPrimitive("generation").getAsInt());
            JsonArray arr = object.getAsJsonArray("aliveCells");
            arr.forEach(elem -> {
                Location loc = gson.fromJson(elem, Location.class);
                locs.add(loc);
            });
        }
        design.setDesign(locs);
        return design;
    }
    
    private Set<Location> readGOL() throws IOException {
        Set<Location> design;
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            design= new HashSet<>();
            int len = in.readInt();
            for (int idx = 0; idx < len; idx++) {
                int x = in.readInt();
                int y = in.readInt();
                design.add(new Location(x, y));
            }
            System.out.println(design);
        }
        return design;
    }

    private Set<Location> readRLE() throws IOException {
        
        Set<Location> design = new HashSet<>();
        
        try (Scanner scan = new Scanner(file)) {
        
            String line = null;
            int x, y;
            boolean[][] barr;

            while (scan.hasNext()) {
                line = scan.nextLine();
                if (!line.startsWith("#")) {
                    break;
                }

            }

            if (line == null) {
                line = scan.nextLine();
            }

            /*
             Pattern header = Pattern.compile("x = \\d+, y = \\d+, rule = B\\d+\\/S\\d+");
             Matcher match = header.matcher(line);
             System.out.println("Matcher: " + match);
             x = Integer.parseInt(match.group(1));
             y = Integer.parseInt(match.group(2));
             barr = new boolean[x][y];
             */
            System.out.println("Header: " + line);
            Scanner header = new Scanner(line);
            header.next();
            header.next();
            String xs = header.next();
            x = Integer.parseInt(xs.substring(0, xs.length() - 1));
            header.next();
            header.next();
            String ys = header.next();
            y = Integer.parseInt(ys.substring(0, ys.length() - 1));

            //barr = new boolean[x][y];

            int col = 0, row = 0, dig = 0;

            String digbuf = "";

            int midx = x / 2;
            int midy = y / 2;
            
            body:
            {
                while (scan.hasNext()) {
                    line = scan.nextLine();
                    char[] carr = line.toCharArray();
                    System.out.println("Parsing line: " + line);
                    for (char c : carr) {
                        //System.out.printf("Parsing char[%d/%d,%d/%d][%s,%d]: %c\n", col, x, row, y, digbuf, dig, c);
                        if (c == 'b') {
                            col += dig == 0 ? 1 : Integer.parseInt(digbuf);
                            dig = 0;
                            digbuf = "";
                        } else if (c == 'o') {
                            int len = dig == 0 ? 1 : Integer.parseInt(digbuf);
                            for (int i = 0; i < len; i++) {
                                //barr[col++][row] = true;
                                design.add(new Location(midx - col++, midy - row));
                            }
                            dig = 0;
                            digbuf = "";
                        } else if (c == '$') {
                            row += dig == 0 ? 1 : Integer.parseInt(digbuf);
                            col = 0;
                            dig = 0;
                            digbuf = "";
                        } else if (Character.isDigit(c)) {
                            dig++;
                            digbuf += c;
                        } else if (c == '!') {
                            break body;
                        }
                    }
                }
            }

            /*
            for (col = 0; col < x; col++) {
                for (row = 0; row < y; row++) {
                    if (barr[col][row]) {
                        design.add(new Location(midx - col, midy - row));
                    }
                }
            }
            */

            System.out.println(design);

        }
        
        return design;
    }
}
