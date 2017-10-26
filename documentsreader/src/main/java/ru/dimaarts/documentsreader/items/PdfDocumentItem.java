package ru.dimaarts.documentsreader.items;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import ru.dimaarts.documentsreader.R;

import ru.dimaarts.documentsreader.utils.FilePicker;
import ru.dimaarts.documentsreader.utils.MuPDFCore;
import ru.dimaarts.documentsreader.utils.MuPDFPageView;
import ru.dimaarts.documentsreader.utils.PageView;
import ru.dimaarts.documentsreader.utils.Size;

/**
 * Created by gorshunovdv on 1/27/2017.
 */
public class PdfDocumentItem extends DocumentItem implements PartialRenderingDocumentPage {
    private MuPDFCore mCore;
    private int mPdfNumberOfPage;
    private Size pageSize;
    private Size displayPageSize;
    private boolean blank = true;
    private Bitmap pageBitmap;
    private int screenWidth;
    private int screenHeight;

    public PdfDocumentItem(String fileName, MuPDFCore PdfDocument, int pageNumber) {
        super(fileName);
        mCore = PdfDocument;
        mPdfNumberOfPage = pageNumber;
    }

    @Override
    public void sizeComputed() {
        displayPageSize = new Size();
        displayPageSize.setWidth(screenWidth);
        displayPageSize.setHeight(pageSize.getHeight()*screenWidth/pageSize.getWidth());
        blank = false;
    }

    @Override
    public void bindHolder(View view, RecyclerView parent, FilePicker.FilePickerSupport filePickerSupport, @NonNull Size blankSize, boolean wasBounded) {
        if (pageBitmap == null || pageBitmap.getWidth() != parent.getWidth() || parent.getHeight() != screenHeight)
        {
            pageBitmap = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);
        }
        MuPDFPageView pageView = (MuPDFPageView) view;

        Point parentSize = new Point(parent.getWidth(), parent.getHeight());
       if(!wasBounded) {
            pageView.init(filePickerSupport, mCore, parentSize, pageBitmap);
        }
        Size pageSize = blankSize;
        if(!blank && displayPageSize!=null) {
            pageSize = displayPageSize;
        }
        Log.d("PdfDocumentItem", "setPage: " + blank + ", page: " + mPdfNumberOfPage);
        pageView.setPage(mPdfNumberOfPage, new PointF(pageSize.getWidth(), pageSize.getHeight()), true);

    }

    @Override
    public void onScale(View view, float scaleFactor) {
        MuPDFPageView pageView = (MuPDFPageView) view;
        pageView.setScale(scaleFactor);
    }

    @Override
    public void computeSize(int screenWidth, int screenHeight) {
            Log.d("PdfDocumentItem", "getPageSize: " + mPdfNumberOfPage);
            PointF size = mCore.getPageSize(mPdfNumberOfPage);
            pageSize = new Size();
            pageSize.setWidth((int) size.x);
            pageSize.setHeight((int) size.y);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            Log.d("PdfDocumentItem", "end getPageSize: " + mPdfNumberOfPage);
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
    public View[] prepareAndGetViewsForCache(View root) {
        return new View[0];
    }

    @Override
    public int getLayoutId() {
        return R.layout.pdf_document_page;
    }

    @Override
    public void onDisplayed(View[] cachedViews) {

    }

    @Override
    public void prepareToDisplayInBackground() {

    }

    @Override
    public void onRecycle(View itemView, View[] cachedViews) {
        MuPDFPageView pageView = (MuPDFPageView) itemView;
        pageView.releaseResources();
        pageBitmap = null;
    }

    @Override
    public void onDetach(View itemView, View[] cachedViews) {
        MuPDFPageView pageView = (MuPDFPageView) itemView;
        pageView.removeHq();
    }

    @Override
    public void onRelease(View itemView) {
        MuPDFPageView pageView = (MuPDFPageView) itemView;
        pageView.releaseBitmaps();
        if(mCore!=null) {
            mCore.onDestroy();
            mCore = null;
        }
    }

    @Override
    public void updateHQ(View view, int left, int top, int right, int bottom) {
        //if(!blank) {
            PageView pdfPageView = (PageView) view;
            pdfPageView.updateHq(false);
        //}
    }

    @Override
    public void removeHQ(View view) {
        PageView pdfPageView = (PageView)view;
        pdfPageView.removeHq();
    }
}
