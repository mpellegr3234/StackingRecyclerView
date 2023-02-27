package com.example.mpellegrino.stackinglist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Used to layout the child views in a stack
 */
public class HorizontalStackLinearLayoutManager extends LinearLayoutManager {
    private static final String TAG = HorizontalStackLinearLayoutManager.class.getName();
    @NonNull
    private final HorizontalStackLinearConfig mStackConfig;

    /**
     * @param stackConfig The stack config to specify how the create the stack
     * @param stackFromEnd Whether to show the list pre-scrolled to the end
     */
    public HorizontalStackLinearLayoutManager(Context context, @NonNull HorizontalStackLinearConfig stackConfig, boolean stackFromEnd) {
        super(context, HORIZONTAL, false);
        mStackConfig = stackConfig;
        //TODO: stackFromEnd = true does not allow scrolling at all
        setStackFromEnd(stackFromEnd);
    }

    @Override
    public void setReverseLayout(boolean reverseLayout){
        if(reverseLayout) {
            Log.w(TAG, "This view is not compatible with reverse layout = true");
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        recycleStackedChildViews();
        layoutStack(recycler, state);
    }

    /**
     * Reset the alpha value for all child views possibly recycled from a child view in the stack
     */
    private void recycleStackedChildViews(){
        for(int i = findFirstVisibleItemPosition(); i < findLastVisibleItemPosition(); i++){
            View childView = findViewByPosition(i);
            // since we are only getting visible child views, this value should never be null.
            assert childView != null;
            childView.findViewById(R.id.opaque_part).setAlpha(1);
        }
    }

    //TODO: fix last item hard to scroll off stack
    //TODO: test with child decorations
    /**
     * Assigns each child to a layer depending on their position on screen and then lays them out.
     */
    private void layoutStack(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int numberChildrenOffScreenEnd = (getItemCount() - 1) - findLastCompletelyVisibleItemPosition();
        // number layers to show is either the number of children off end of screen (because we are
        // getting to the end of stack), or the maximum amount of layers determined by the stack config
        int numberLayers = Math.min(numberChildrenOffScreenEnd, mStackConfig.getNumberLayers());
        if (numberLayers <= 0) {
            return;
        }

        int lastVisibleItemPosition = findLastVisibleItemPosition();
        View childView = findViewByPosition(lastVisibleItemPosition);
        // since we are only getting last child view on screen, this value should never be null.
        assert childView != null;

        int marginStart = ((ViewGroup.MarginLayoutParams) childView.getLayoutParams()).getMarginStart();
        int marginEnd = ((ViewGroup.MarginLayoutParams) childView.getLayoutParams()).getMarginEnd();

        int childWidth = childView.getMeasuredWidth();
        int childHeight = childView.getMeasuredHeight();

        int layerOneChildTop = childView.getTop();
        // the width of all children even if they are offscreen. since we start laying out the last
        // visible child here, we use that child's position to determine the number of child views
        // before and including the current one and multiply it by the child view width plus margins
        int currentUnstackedWidth = (marginStart + childWidth + marginEnd) * lastVisibleItemPosition;

        int scrollX = computeHorizontalScrollOffset(state);
        // stacks starts at last fully visible child start
        @Px int stackStart = getWidth() - (marginStart + childWidth + marginEnd);

//        Log.d(TAG, "currentUnstackedWidth = " + currentUnstackedWidth + " lastVisibleItemPosition = " + lastVisibleItemPosition + " childWidth = " + childWidth + " childHeight = " + childHeight + " stackStart = " + stackStart);

        // starts laying out child views from the last visible one plus the number of layers for the stack
        for (int i = lastVisibleItemPosition; i < (lastVisibleItemPosition + numberLayers); i++) {
            if(i == lastVisibleItemPosition) {
                // gets last visible child view that is already on screen
                childView = findViewByPosition(lastVisibleItemPosition);
            } else {
                // gets child views that are not yet visible on screen for the stack
                childView = recycler.getViewForPosition(i);
                addView(childView, i);
                measureChildWithMargins(childView, 0, 0);
            }

            // since we are only getting child views either on screen or prefetched from the recycler,
            // the child view should never be null.
            if (childView == null) {
                throw new AssertionError("childView is null for position = " + i);
            }

            currentUnstackedWidth += marginStart;

            HorizontalStackLinearConfig.StackLayer layer =
                    mStackConfig.calculateLayer(scrollX, getWidth(),
                            currentUnstackedWidth, marginStart + childWidth + marginEnd);
            Log.d(TAG, "calculateLayer position = " + i + " scrollX = " + scrollX + " parentWidth = " + getWidth() + " childStart = " + currentUnstackedWidth + " childWidth = " + (marginStart + childWidth + marginEnd));

            // layer should only be null if the last visible item fits exactly in the recyclerView
            if (layer == null && i != lastVisibleItemPosition) {
                throw new AssertionError("layer is null for position = " + i);
            }

            if(layer != null){
                int childStart = stackStart + layer.getOffsetX();
                int childTop = layerOneChildTop + layer.getOffsetY();

                layoutDecoratedWithMargins(childView, childStart, childTop, childStart + childWidth, childTop + childHeight);
                //TODO: why not set entire childView alpha
                childView.findViewById(R.id.opaque_part).setAlpha(layer.getAlpha());

                Log.d(TAG, "child layout position = " + i +
                        " start = " + childView.getLeft() + " top = " + childView.getTop() +
                        " end = " + childView.getRight() + " bottom = " + childView.getBottom() +
                        " layer = " + layer.getLayer() + " offsetX = " + layer.getOffsetX() +
                        " offsetY = " + layer.getOffsetY() + " alpha = " + layer.getAlpha());
            }

            currentUnstackedWidth += childWidth + marginEnd;
        }
    }

//    @Override
//    public int getExtraLayoutSpace(RecyclerView.State state){
//        return 1000;
//    }
}
