package ru.dimaarts.documentsreader.layouts;

import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.dimaarts.documentsreader.adapters.PageItemsAdapter;
import ru.dimaarts.documentsreader.items.DocumentItem;
import ru.dimaarts.documentsreader.items.PartialRenderingDocumentPage;
import ru.dimaarts.documentsreader.pager.PagerView;
import ru.dimaarts.documentsreader.recyclerviews.DocumentsReaderRecyclerView;

public class DocumentsReaderLayoutManager extends RecyclerView.LayoutManager implements DocumentsReaderRecyclerView.ActionsCallback {
    private float mScale = 1;
    private RecyclerView.Recycler mRecycler;
    private boolean isScaling;
    private float offsetLeft;
    private float offsetTop;
    private PageItemsAdapter mAdapter;
    private float oldFullHeight = -1;
    private float oldMaxWidth = -1;
    private boolean needUpdateHqsAfterOnLayout;
    private PagerView pager;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler);
        mRecycler = recycler;
        if(needUpdateHqsAfterOnLayout) {
            updateAllHQ();
            needUpdateHqsAfterOnLayout = false;
        }
    }

    private void fill(RecyclerView.Recycler recycler) {
        List<View> detachedViews = new ArrayList<>();
        for(int i=0; i<getChildCount(); i++) {
            detachedViews.add(getChildAt(i));
        }
        int allViewsCount = getChildCount();
        detachAndScrapAttachedViews(recycler);
        fillDown(recycler, detachedViews);
        int detachedCount = 0;
        for (View viewFoRecycle:
                detachedViews) {
            detachedCount++;
            //recycler.recycleView(viewFoRecycle);
        }
        Log.d("fill", "Views count = " + detachedCount + " from " + allViewsCount);
    }

    private void fillDown(RecyclerView.Recycler recycler, List<View> viewsFoRecycle) {
        Offset offset = new Offset();
        int pos = getItemPositionByCoordinatesUp(offsetTop, offset);
        pager.setPage(pos);
        int viewTop =  offset.getOffset();
        float anchorLeft = -offsetLeft;

        boolean fillDown = true;
        int itemCount = getItemCount();
        int height = getHeight();

        while (fillDown && pos < itemCount && pos >= 0) {
            View view = recycler.getViewForPosition(pos);
            viewsFoRecycle.remove(view);
            addView(view);

            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            int originalWidth = lp.width;
            if(mAdapter!=null) {
                originalWidth = mAdapter.getViewItemSize(pos).getWidth();
            }
            int originalHeight = lp.height;
            if(mAdapter!=null) {
                originalHeight = mAdapter.getViewItemSize(pos).getHeight();
            }
            int scaledChildWidth = (int) (originalWidth * mScale);
            int scaledChildHeight = (int) (originalHeight * mScale);
            measureChildWithMargins(view, scaledChildWidth, scaledChildHeight);
            layoutDecorated(view, (int) anchorLeft, viewTop, (int) (anchorLeft + scaledChildWidth), viewTop + scaledChildHeight);
            viewTop = getDecoratedBottom(view);
            fillDown = viewTop <= height;
            pos++;
        }
    }

    public void setPager(PagerView pager) {
        this.pager = pager;
    }

    private class Offset {
        private int offset;
        public int getOffset() {
            return offset;
        }
        public void setOffset(int offset) {
            this.offset = offset;
        }
    }

    private int getItemPositionByCoordinatesUp(float y, Offset offset)
    {
        if(mAdapter == null) return 0;
        double itemHeight = 0;
        double fullHeight = 0;
        int firstItemInList = -1;
        for (int i = 0; i < getItemCount(); i++)
        {
            firstItemInList = i-1;
            fullHeight += itemHeight;
            itemHeight = mAdapter.getViewItemSize(i).getHeight() * mScale;
            if (fullHeight+itemHeight >= y)
            {
                break;
            }
        }

        offset.setOffset((int) (fullHeight - y));
        return firstItemInList + 1;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    public void measureChildWithMargins(View child, int childWidth, int childHeight) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();

        Rect insets = new Rect();
        calculateItemDecorationsForChild(child, insets);
        int widthUsed = insets.left + insets.right;
        int heightUsed = insets.top + insets.bottom;

        final int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(),
                getPaddingLeft() + getPaddingRight() +
                        lp.leftMargin + lp.rightMargin + widthUsed, childWidth,
                canScrollHorizontally());
        final int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(),
                getPaddingTop() + getPaddingBottom() +
                        lp.topMargin + lp.bottomMargin + heightUsed, childHeight,
                canScrollVertically());
        if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
            child.measure(widthSpec, heightSpec);
        }
    }

    boolean shouldMeasureChild(View child, int widthSpec, int heightSpec, RecyclerView.LayoutParams lp) {
        return child.isLayoutRequested()
                || !isMeasurementCacheEnabled()
                || !isMeasurementUpToDate(child.getWidth(), widthSpec, lp.width)
                || !isMeasurementUpToDate(child.getHeight(), heightSpec, lp.height);
    }

    private static boolean isMeasurementUpToDate(int childSize, int spec, int dimension) {
        final int specMode = View.MeasureSpec.getMode(spec);
        final int specSize = View.MeasureSpec.getSize(spec);
        if (dimension > 0 && childSize != dimension) {
            return false;
        }
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                return true;
            case View.MeasureSpec.AT_MOST:
                return specSize >= childSize;
            case View.MeasureSpec.EXACTLY:
                return  specSize == childSize;
        }
        return false;
    }

    private float availableScrollTop(float offset) {
        float fullHeight = 0;
        for(int i=0; i<getItemCount(); i++) {
            fullHeight += mAdapter.getViewItemSize(i).getHeight()*mScale;
        }
        return Math.min(Math.max(offset, 0), fullHeight-getHeight());
    }

    private float availableScrollLeft(float offset) {
        float maxViewWidth = 0;
        for(int i=0; i<getItemCount(); i++) {
            maxViewWidth = Math.max(mAdapter.getViewItemSize(i).getWidth()*mScale, maxViewWidth);
        }
        return Math.min(Math.max(offset, 0), maxViewWidth-getWidth());
    }

    @Override
    public void onScale(float scale, final float scaleFactor, PointF focusPoint) {
        this.mScale = scale;
        // Compute list offset after scaling
        float deltaY = availableScrollTop(offsetTop * scaleFactor + focusPoint.y);
        offsetTop = deltaY;
        offsetChildrenVertical((int) -deltaY);

        float newOffsetLeft = offsetLeft * scaleFactor + focusPoint.x;
        float deltaX = scrollHorizontallyInternal(offsetLeft - newOffsetLeft, false);
        offsetLeft = availableScrollLeft(newOffsetLeft);
        offsetChildrenHorizontal((int) -deltaX);
        fill(mRecycler);

        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            DocumentItem item = mAdapter.getItem(position);
            item.onScale(view, scaleFactor);
        }
    }

    @Override
    public void onScaleBegin() {
        isScaling = true;
    }

    @Override
    public void onScroll(float dx, float dy) {
        if(!isScaling) {
            offsetLeft += dx;
            offsetTop += dy;
            fill(mRecycler);
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(oldFullHeight>-1) {
            float fullHeight = 0;
            for (int i = 0; i < getItemCount(); i++) {
                fullHeight += mAdapter.getViewItemSize(i).getHeight() * mScale;
            }
            float maxViewWidth = 0;
            for(int i=0; i<getItemCount(); i++) {
                maxViewWidth = Math.max(mAdapter.getViewItemSize(i).getWidth()*mScale, maxViewWidth);
            }
            float changeH = fullHeight/oldFullHeight;
            float changeW = maxViewWidth/oldMaxWidth;
            offsetTop *= changeH;
            offsetTop = availableScrollTop(offsetTop);
            offsetLeft *= changeW;
            offsetLeft = availableScrollLeft(offsetLeft);
            oldFullHeight = -1;
            needUpdateHqsAfterOnLayout = true;
            requestLayout();
        }
    }

    @Override
    public void onScrollEnded() {
        updateAllHQ();
    }

    @Override
    public void onDestroy() {
        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            DocumentItem item = mAdapter.getItem(position);
            item.onRelease(view);
        }
        for(int i=0; i<mRecycler.getScrapList().size();i++) {
            View view = mRecycler.getScrapList().get(i).itemView;
            int position = getPosition(view);
            DocumentItem item = mAdapter.getItem(position);
            item.onRelease(view);
        }
    }

    private void updateAllHQ() {
        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            DocumentItem item = mAdapter.getItem(position);
            if(item instanceof PartialRenderingDocumentPage) {
                PartialRenderingDocumentPage partialRenderingDocumentPage = (PartialRenderingDocumentPage) item;
                partialRenderingDocumentPage.updateHQ(view, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public void onScaleEnd() {
        isScaling = false;
        updateAllHQ();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        float delta = scrollVerticallyInternal(dy, true);
        offsetChildrenVertical((int) -delta);
        return (int) delta;
    }

    private float scrollVerticallyInternal(float dy, boolean checkScaling) {
        if(isScaling && checkScaling) return 0;
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0){
            return 0;
        }

        final View topView = getChildAt(0);
        final View bottomView = getChildAt(childCount - 1);

        if(getPosition(topView) == 0 && getPosition(bottomView) == itemCount-1) {
            int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
            if (viewSpan <= getHeight()) {
                return 0;
            }
        }

        float delta = 0;
        if (dy < 0){
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0){
                delta = dy;
            } else {
                int viewTop = getDecoratedTop(firstView);
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0){
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1){
                delta = dy;
            } else {
                int viewBottom = getDecoratedBottom(lastView);
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
        }
        return delta;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        float delta = scrollHorizontallyInternal(dx, true);
        offsetChildrenHorizontal((int) -delta);
        return (int) delta;
    }

    private float scrollHorizontallyInternal(float dx, boolean checkScaling) {
        if(isScaling && checkScaling) return 0;
        int childCount = getChildCount();
        if (childCount == 0){
            return 0;
        }
        float delta = 0;
        final View topView = getChildAt(0);
        int viewLeft = getDecoratedLeft(topView);
        int viewRight = getDecoratedRight(topView);
        if(dx < 0) {
            delta = Math.max(viewLeft, dx);
        }
        else if(dx > 0) {
            int parentRight = getWidth();
            delta = Math.min(viewRight-parentRight, dx);
        }
        return delta;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        if(newAdapter instanceof PageItemsAdapter) {
            mAdapter = (PageItemsAdapter) newAdapter;
        }
        else {
            mAdapter = null;
        }
    }

    private static final String SUPER_INSTANCE_STATE = "saved_instance_state_parcelable";


    // TODO: Need uncomment this code and
    @Override
    public Parcelable onSaveInstanceState() {
        // Create a bundle to put super parcelable in
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        // Use abstract method to put mOriginalValue in the bundle;
        bundle.putFloat("vOffset", offsetTop);
        bundle.putFloat("hOffset", offsetLeft);
        bundle.putFloat("scale", mScale);
        float fullHeight = 0;
        float maxViewWidth = 0;
        if(mAdapter!=null) {
            for (int i = 0; i < getItemCount(); i++) {
                fullHeight += mAdapter.getViewItemSize(i).getHeight() * mScale;
            }
            for(int i=0; i<getItemCount(); i++) {
                maxViewWidth = Math.max(mAdapter.getViewItemSize(i).getWidth()*mScale, maxViewWidth);
            }
        }
        bundle.putFloat("fullHeight", fullHeight);
        bundle.putFloat("maxWidth", maxViewWidth);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        // We know state is a Bundle:
        Bundle bundle = (Bundle) state;
        // Get mViewIds out of the bundle
        offsetTop = bundle.getFloat("vOffset");
        offsetLeft = bundle.getFloat("hOffset");
        mScale = bundle.getFloat("scale");
        oldFullHeight = bundle.getFloat("fullHeight");
        oldMaxWidth = bundle.getFloat("maxWidth");

        state = bundle.getParcelable(SUPER_INSTANCE_STATE);
        super.onRestoreInstanceState(state);
    }

}