package ru.dimaarts.documentsreader.pager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.dimaarts.documentsreader.R;

/**
 * Created by gorshunovdv on 2/17/2017.
 */
public class PagerView extends ViewGroup {
    private View pagerView;
    private TextView textView;

    public PagerView(Context context) {
        this(context, null);
    }

    public PagerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PagerView, defStyleAttr, 0);
        pagerView = LayoutInflater.from(context).inflate(
                a.getResourceId(
                        R.styleable.PagerView_layout,
                R.layout.simple_pager),
        null);
        int textViewId = a.getResourceId(R.styleable.PagerView_textViewId, 0);
        a.recycle();
        pagerView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(pagerView);
        textView = (TextView) pagerView.findViewById(textViewId);
    }

    public void setPage(int page) {
        textView.setText(String.valueOf(page+1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x=0;
        int y=0;
        if(pagerView!=null) {
            pagerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            x = pagerView.getMeasuredWidth();
            y = pagerView.getMeasuredHeight();
        }
        setMeasuredDimension(x, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(pagerView!=null)
            pagerView.layout(0, 0, r-l, b-t);
    }
}
