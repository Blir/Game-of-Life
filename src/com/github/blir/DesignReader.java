package com.github.blir;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Blir
 */
public class DesignReader {

    private final DataInputStream in;
    private final Scanner scan;
    private final boolean rle;

    public DesignReader(File file) throws FileNotFoundException {
        in = new DataInputStream(new FileInputStream(file));
        scan = new Scanner(file);
        rle = file.getName().toLowerCase().endsWith("rle");
    }

    public Set<Location> read() throws IOException {
        if (rle) {
            return readRLE();
        }
        Set<Location> design = new HashSet<>();
        int len = in.readInt();
        for (int idx = 0; idx < len; idx++) {
            int x = in.readInt();
            int y = in.readInt();
            design.add(new Location(x, y));
        }
        System.out.println(design);
        in.close();
        scan.close();
        return design;
    }

    public Set<Location> readRLE() throws IOException {
        Set<Location> design = new HashSet<>();
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

        barr = new boolean[x][y];

        int col = 0, row = 0, dig = 0;

        String digbuf = "";

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
                            barr[col++][row] = true;
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

        int midx = x / 2;
        int midy = y / 2;

        for (col = 0; col < x; col++) {
            for (row = 0; row < y; row++) {
                if (barr[col][row]) {
                    design.add(new Location(midx - col, midy - row));
                }
            }
        }

        in.close();
        scan.close();

        System.out.println(design);

        return design;
    }
}
