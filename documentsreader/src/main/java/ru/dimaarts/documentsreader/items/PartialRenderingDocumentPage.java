package ru.dimaarts.documentsreader.items;

import android.view.View;

/**
 * Created by gorshunovdv on 1/27/2017.
 */
public interface PartialRenderingDocumentPage {
    void updateHQ(View view, int left, int top, int right, int bottom);
    void removeHQ(View view);
}
