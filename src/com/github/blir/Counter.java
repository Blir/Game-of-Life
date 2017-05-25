package com.github.blir;

/**
 *
 * @author Travis
 */
public class Counter {
    
    private int count;
    
    public synchronized int syncIncrement() {
        return ++count;
    }
    
    public int increment() {
        return ++count;
    }
    
    public int count() {
        return count;
    }
    
    @Override
    public String toString() {
        return Integer.toString(count);
    }
}
