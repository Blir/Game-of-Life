package com.github.blir;

/**
 *
 * @author Blir
 */
public final class Location {

    public final int x;
    public final int y;

    public final int hash;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        hash = x ^ y;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Location) {
            Location other = (Location) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

    public boolean isNextTo(Location other) {
        return Math.abs(this.x - other.x) == 1 || Math.abs(this.y - other.y) == 1;
    }
}
