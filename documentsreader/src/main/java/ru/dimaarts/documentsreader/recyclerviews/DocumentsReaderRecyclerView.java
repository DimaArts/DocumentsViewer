package ru.dimaarts.documentsreader.recyclerviews;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by gorshunovdv on 1/23/2017.
 */
public class DocumentsReaderRecyclerView extends RecyclerView {
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private int savedW;
    private int savedH;
    private int savedOldW;
    private int savedOldH;
    private boolean pageSizeCalculated;

    public void documentsLoaded() {
        final LayoutManager layoutManager = getLayoutManager();
        if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
            ActionsCallback scaleListener = ((ActionsCallback)layoutManager);
            scaleListener.onSizeChanged(savedW, savedH, savedOldW, savedOldH);
        }
        pageSizeCalculated = true;
    }

    public interface ActionsCallback {
        void onScale(float mScaleFactor, float factor, PointF focusPoint);
        void onScaleBegin();
        void onScaleEnd();
        void onScroll(float dx, float dy);
        void onSizeChanged(int w, int h, int oldw, int oldh);
        void onScrollEnded();
        void onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mScaleDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    public void onDestroy() {
        final LayoutManager layoutManager = getLayoutManager();
        if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
            ActionsCallback scaleListener = ((ActionsCallback)layoutManager);
            scaleListener.onDestroy();
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        final LayoutManager layoutManager = getLayoutManager();
        if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
            ActionsCallback scaleListener = ((ActionsCallback) layoutManager);
            scaleListener.onScroll(dx, dy);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        boolean hasEnded = state == SCROLL_STATE_IDLE;
        if(hasEnded) {
            final LayoutManager layoutManager = getLayoutManager();
            if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
                ActionsCallback scaleListener = ((ActionsCallback) layoutManager);
                scaleListener.onScrollEnded();
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            final LayoutManager layoutManager = getLayoutManager();
            if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
                ActionsCallback scaleListener = ((ActionsCallback)layoutManager);
                scaleListener.onScaleBegin();
            }
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            final LayoutManager layoutManager = getLayoutManager();
            if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
                ActionsCallback scaleListener = ((ActionsCallback)layoutManager);
                scaleListener.onScaleEnd();
            }
            super.onScaleEnd(detector);
        }


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float previousScale = mScaleFactor;
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 3.0f));
            float factor =  mScaleFactor / previousScale;

            float viewFocusX = detector.getFocusX() * factor - detector.getFocusX();
            float viewFocusY = detector.getFocusY() * factor - detector.getFocusY();
            final PointF focusPoint = new PointF(viewFocusX, viewFocusY);

            final LayoutManager layoutManager = getLayoutManager();
            if(layoutManager!=null && layoutManager instanceof ActionsCallback) {
                ActionsCallback scaleListener = ((ActionsCallback)layoutManager);
                scaleListener.onScale(mScaleFactor, factor, focusPoint);
            }
            return true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        savedW = w;
        savedH = h;
        savedOldW = oldw;
        savedOldH = oldh;
        if(pageSizeCalculated) {
            final LayoutManager layoutManager = getLayoutManager();
            if (layoutManager != null && layoutManager instanceof ActionsCallback) {
                ActionsCallback scaleListener = ((ActionsCallback) layoutManager);
                scaleListener.onSizeChanged(savedW, savedH, savedOldW, savedOldH);
            }
        }
    }

    public DocumentsReaderRecyclerView(Context context) {
        this(context, null);
    }

    public DocumentsReaderRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DocumentsReaderRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private static final String SUPER_INSTANCE_STATE = "saved_instance_state_parcelable";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat("scale", mScaleFactor);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        mScaleFactor = bundle.getFloat("scale");
        state = bundle.getParcelable(SUPER_INSTANCE_STATE);
        super.onRestoreInstanceState(state);
    }
}
