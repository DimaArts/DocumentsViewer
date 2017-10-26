package ru.dimaarts.documentsreader.adapters;


import android.view.View;

import ru.dimaarts.documentsreader.items.DocumentItem;
import ru.dimaarts.documentsreader.utils.Size;

/**
 * Created by gorshunovdv on 1/25/2017.
 */
public interface PageItemsAdapter {
    Size getViewItemSize(int position);
    void recycleItem(View view, int position);
    DocumentItem getItem(int position);
}
