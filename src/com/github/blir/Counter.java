package com.github.blir;

/**
 *
 * @author Travis
 */
public class Counter {
    
    private int count;
    
    public synchronized int increment() {
        return ++count;
    }
    
    public int count() {
        return count;
    }
}
