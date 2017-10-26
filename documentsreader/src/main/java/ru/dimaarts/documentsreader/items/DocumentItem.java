package ru.dimaarts.documentsreader.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import ru.dimaarts.documentsreader.utils.FilePicker;
import ru.dimaarts.documentsreader.utils.Size;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public abstract class DocumentItem {
    private String mFileName;
    private DisplayCallback displayCallback;

    public DisplayCallback getDisplayCallback() {
        return displayCallback;
    }

    public void setDisplayCallback(DisplayCallback displayCallback) {
        this.displayCallback = displayCallback;
    }

    protected void cancelDisplay() {
        if(displayCallback!=null) {
            displayCallback.cancelDisplay();
        }
    }

    public abstract void onScale(View view, float scaleFactor);

    public interface DisplayCallback {
        void cancelDisplay();
    }

    public DocumentItem(String fileName) {
        mFileName = fileName;
    }

    public abstract void computeSize(int screenWidth, int screenHeight);
    public abstract void sizeComputed();
    public abstract Size getPageSize();
    public abstract Size getDisplayedPageSize();
    public abstract boolean isBlank();

    public abstract void bindHolder(View view, RecyclerView parent, FilePicker.FilePickerSupport filePickerSupport, @NotNull Size blankSize, boolean wasBounded);
    public abstract View[] prepareAndGetViewsForCache(View root);

    public String getFileName() {
        return mFileName;
    }
    public abstract int getLayoutId();
    public abstract void onDisplayed(View[] cachedViews);
    public abstract void prepareToDisplayInBackground();
    public abstract void onRecycle(View itemView, View[] cachedViews);
    public abstract void onDetach(View itemView, View[] cachedViews);
    public abstract void onRelease(View itemView);
}
