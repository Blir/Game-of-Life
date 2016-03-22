package com.github.blir.cache;

import com.github.blir.Life;
import com.github.blir.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Travis
 */
public class GroupManager {

    private final Map<Long, Group> groups = new HashMap<>();
    private final Map<Location, Long> cells = new HashMap<>();

    public void clear() {
        groups.clear();
        cells.clear();
    }

    public Group groupFor(Location loc, Set<Location> world) {
        Long groupId = cells.get(loc);
        if (groupId == null) {
            Group group = new Group();
            group(loc, group, world);
            // gen group id
            //groups.put(id, group);
            group.stream().forEach(cell -> {
                //cells.put(cell, id);
            });
            return group;
        } else {
            return groups.get(groupId);
        }
    }

    private void group(Location start, Group group, Set<Location> world) {
        group.addCell(start);
        Life.neighbors1(start).stream().forEach(neighbor -> {
            if (!group.contains(neighbor) && world.contains(neighbor)) {
                group(neighbor, group, world);
            }
        });

    }

}
