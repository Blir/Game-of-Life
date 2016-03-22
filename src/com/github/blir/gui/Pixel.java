package com.github.blir.gui;

import java.awt.Color;

/**
 *
 * @author Travis
 */
public class Pixel {
    
    private Color color;
    private int size;
    
    public Pixel(Color color, int size) {
        this.color = color;
        this.size = size;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
}
