package ru.dimaarts.documentsreader.documentsloaders;

import android.content.Context;

import ru.dimaarts.documentsreader.items.DocumentItem;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public interface DocumentsLoader {
    void loadManyItems(DocumentItem[] documentsPaths);
    void loadOne(DocumentItem documentsPath, int position, int documentsCount);
    void createManyItems(Context context, String[] documentsPaths);
    void display(int position, Runnable runnable, Runnable runnable1);
    void release();
}
