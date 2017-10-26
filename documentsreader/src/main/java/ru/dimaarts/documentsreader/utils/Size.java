package ru.dimaarts.documentsreader.utils;

import java.io.Serializable;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class Size implements Serializable {
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
