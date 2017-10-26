package ru.dimaarts.documentsreader.items;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import ru.dimaarts.documentsreader.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import ru.dimaarts.documentsreader.utils.BitmapUtils;
import ru.dimaarts.documentsreader.utils.FilePicker;
import ru.dimaarts.documentsreader.utils.Size;

/**
 * Created by gorshunovdv on 1/20/2017.
 */
public class ImageDocumentItem extends DocumentItem {
    private Size pageSize;
    private Size displayPageSize;
    private boolean blank = true;
    private Bitmap pageImage;
    private BitmapFactory.Options opts;
    private boolean isRecycled = false;
    private int screenWidth;
    private int screenHeight;

    @Override
    public void sizeComputed() {
        displayPageSize = new Size();
        displayPageSize.setWidth(screenWidth);
        if(pageSize.getWidth()>0) {
            displayPageSize.setHeight(pageSize.getHeight() * screenWidth / pageSize.getWidth());
            blank = false;
        }
        else {
            blank = true;
        }
    }

    @Override
    public void onScale(View view, float scaleFactor) {

    }

    public ImageDocumentItem(String fileName) {
        super(fileName);
    }

    @Override
    public void computeSize(int screenWidth, int screenHeight) {
        Point size = BitmapUtils.bitmapSize(getFileName());
        pageSize = new Size();
        pageSize.setWidth(size.x);
        pageSize.setHeight(size.y);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public Size getPageSize() {
        return pageSize;
    }

    @Override
    public Size getDisplayedPageSize() {
        return displayPageSize;
    }

    @Override
    public boolean isBlank() {
        return blank;
    }

    @Override
    public void bindHolder(View view, RecyclerView parent, FilePicker.FilePickerSupport filePickerSupport, @NonNull Size blankSize, boolean wasBounded) {

    }

    @Override
    public View[] prepareAndGetViewsForCache(View root) {
       // cancelDisplay();
        View[] cachedViews = new View[1];
        ImageView pageImageView = (ImageView) root.findViewById(R.id.page_image_view);
        pageImageView.setImageBitmap(null);
        cachedViews[0] = pageImageView;
        return cachedViews;
    }

    @Override
    public int getLayoutId() {
        return R.layout.image_document_page;
    }

    @Override
    public void onDisplayed(View[] cachedViews) {
        cancelDisplay();
        ImageView pageImageView = (ImageView) cachedViews[0];
        if(isRecycled) {
            if(pageImage!=null) {
                pageImageView.setImageBitmap(null);
                pageImage.recycle();
                pageImage = null;
            }
        }
        pageImageView.setImageBitmap(pageImage);
    }

    @Override
    public void prepareToDisplayInBackground() {
        isRecycled = false;
        Point bitmapSize = BitmapUtils.bitmapSize(getFileName());
        int maxBitmapSize = BitmapUtils.getMaxTextureSize();
        opts = new BitmapFactory.Options();
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP) {
            opts.inDither = true;
        }
        if(maxBitmapSize<bitmapSize.x || maxBitmapSize<bitmapSize.y) {
            opts.inSampleSize = BitmapUtils.calculateInSampleSize(bitmapSize.x, bitmapSize.y, maxBitmapSize, maxBitmapSize);
        }
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        FileDescriptor fd;
        pageImage = null;
        try {
            File file = new File(getFileName());
            if (file.exists()) {
                fd = new FileInputStream(getFileName()).getFD();
                pageImage = BitmapFactory.decodeFileDescriptor(fd, null, opts);
            }
            else {
                pageImage = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecycle(View itemView, View[] cachedViews) {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP) {
            opts.requestCancelDecode();
        }
        cancelDisplay();
        ImageView pageImageView = (ImageView) cachedViews[0];
        pageImageView.setImageBitmap(null);
        if(pageImage!=null) {
            pageImage.recycle();
            pageImage = null;
        }
        isRecycled = true;
    }

    @Override
    public void onDetach(View itemView, View[] cachedViews) {
        /*if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP) {
            opts.requestCancelDecode();
        }
        cancelDisplay();
        ImageView pageImageView = (ImageView) cachedViews[0];
        pageImageView.setImageBitmap(null);
        if(pageImage!=null) {
            pageImage.recycle();
            pageImage = null;
        }
        isRecycled = true;*/
    }

    @Override
    public void onRelease(View itemView) {

    }
}
