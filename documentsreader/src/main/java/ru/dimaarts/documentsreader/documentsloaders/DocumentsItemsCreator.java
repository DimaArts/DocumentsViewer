package ru.dimaarts.documentsreader.documentsloaders;

import android.content.Context;

import ru.dimaarts.documentsreader.items.DocumentItem;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public interface DocumentsItemsCreator {
    DocumentItem[] create(Context context, String documentsPath, String documentExtension);
}
