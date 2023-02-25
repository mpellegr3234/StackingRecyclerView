package com.example.mpellegrino.stackinglist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class StackingRecyclerView extends RecyclerView {
    public StackingRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public StackingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StackingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        // draw child views on bottom of stack first so they do not render on top of the previous
        // stacked child views. setting elevation or z alone will not work for situations like
        // removing items, because when item removal is animated, stacked views will render on top of items not in the stack
        setChildDrawingOrderCallback((childCount, i) -> childCount - i - 1);
        // need to redraw child views while scrolling on/off stack
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                requestLayout();
            }
        });
    }
}
