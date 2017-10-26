package ru.dimaarts.documentsreader.viewholders;

import android.view.View;
import android.support.v7.widget.RecyclerView;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class DocumentsReaderViewHolder extends RecyclerView.ViewHolder {
    private View[] mCachedViews;
    private boolean wasBounded;

    public DocumentsReaderViewHolder(View view, View[] cachedVies) {
        super(view);
        mCachedViews = cachedVies;
    }

    public View[] getCachedViews() {
        return mCachedViews;
    }

    public boolean isWasBounded() {
        return wasBounded;
    }

    public void setWasBounded(boolean wasBonded) {
        this.wasBounded = wasBonded;
    }
}
