package com.github.blir;

/**
 *
 * @author Blir
 */
public class Tester {

    public static void main(String[] args) {
        populateTest();
    }
    
    public static void chunkTest() {
        int[] arr = {-6, -5, -4, -1, 0, 1, 4, 5, 6};
        for (int i : arr) {
            int j = i / 5 * 5;
            if (i < 0) {
                j -= 5;
            }
            System.out.println(i + " -> " + j);
        }
        // 0 1 2 3 4 5
        //Set<Location> world = new HashSet<>(Arrays.asList(new Location[]{new Location(-1,1), new Location(-1,0), new Location(0, 0)}));
        //System.out.println(new Cache(10, 5).chunk(world));
        //System.out.println(new Cache(50, 5).getResult(new Chunk(new Location(0, 0), new HashSet<>(Arrays.asList(new Location[]{new Location(1,1)})), 5), new HashSet<>(Arrays.asList(new Location[]{new Location(2,2), new Location(-1,-1), new Location(1,1), new Location(3,3), new Location(6,6)}))));
    }

    public static void neighborTest() {
        int loop = 0;
        int loops = 100000000;

        long start, end;

        start = System.currentTimeMillis();

        for (; loop < loops; loop++) {
            Life.neighbors1(new Location(loop, loop));
        }

        end = System.currentTimeMillis();

        System.out.println(end - start);
    }
    
    public static void populateTest() {
        int loop = 0;
        int loops = 30000;
        Life life = new Life();
        life.init();

        long start, end;

        start = System.currentTimeMillis();

        for (; loop < loops; loop++) {
            life.populate(new Neighbor(new Location(loop, loop), true));
        }

        end = System.currentTimeMillis();

        System.out.println(end - start);
        
        System.exit(0);
    }
}
