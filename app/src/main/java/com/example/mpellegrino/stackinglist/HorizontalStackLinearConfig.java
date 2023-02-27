package com.example.mpellegrino.stackinglist;

import android.support.annotation.Nullable;

/**
 * Used to configure the layer creation for a {@link HorizontalStackLinearLayoutManager}.
 */
public class HorizontalStackLinearConfig {
    private final static String TAG = HorizontalStackLinearConfig.class.getName();

    private final float mStartAlpha;
    private final float mEndAlpha;
    private final float mSlopeOffsetX;
    private final float mSlopeOffsetY;
    private final float mSlopeAlpha;
    private final int mNumberLayers;

    /**
     * Creates a stack config for the appearance of items using a linear scale for offsetting the
     * items, layering them, and the opacity to use as the list is scrolled and items are added or
     * removed from the stack.
     * @param numberLayers Number of layers to show for a stack
     * @param startAlpha Alpha value for the first stack layer
     * @param endAlpha Alpha value for the last stack layer
     * @param offsetX The x distance between items in the stack in pixels
     * @param offsetY The y distance between items in the stack in pixels
     */
    public HorizontalStackLinearConfig(int numberLayers, float startAlpha, float endAlpha, int offsetX, int offsetY) {
        if(numberLayers <= 0){
            throw new AssertionError("numberLayers must be > 0");
        }
        if(startAlpha <= 0){
            throw new AssertionError("startAlpha must be > 0");
        }
        if(endAlpha < 0){
            throw new AssertionError("endAlpha must be >= 0");
        }
        if(endAlpha >= startAlpha){
            throw new AssertionError("endAlpha must be < startAlpha");
        }
        if(offsetX < 0){
            throw new AssertionError("offsetX must be >= 0");
        }
        if(offsetY < 0){
            throw new AssertionError("offsetY must be >= 0");
        }

        mNumberLayers = numberLayers;
        mStartAlpha = startAlpha;
        mEndAlpha = endAlpha;
        mSlopeOffsetX = (offsetX * mNumberLayers) / ((float)mNumberLayers + 1);
        mSlopeOffsetY = (offsetY * mNumberLayers) / ((float)mNumberLayers + 1);
        mSlopeAlpha = (mStartAlpha - mEndAlpha) / (float) mNumberLayers;
    }

    /**
     * Calculates the stack layer a child should be on given how far off the screen it would
     * have been if it wasn't stacked.
     *
     * @param parentStart Start value of the parent's view
     * @param parentWidth Width of the parent's view
     * @param childStart Start value of the child's view if we did not stack
     * @param childWidth Width of the child's view
     * @return An object representing the layer this view should be on, null if it is not in the stack
     */
    @Nullable
    public StackLayer calculateLayer(int parentStart,
                                     int parentWidth,
                                     int childStart,
                                     int childWidth) {
        //total length of this child that would be offscreen if we didn't stack
        int offScreen = childStart + childWidth - parentWidth - parentStart;
        if (offScreen <= 0) {
            //no part of child would be offscreen
            return null;
        }

        float layer = (float)offScreen / (float)childWidth;

        if (layer <= 0 || layer > mNumberLayers) {
            // not on the stack, return null
            return null;
        }

        /*
         * Solves the following system of linear equations and computes the offset and alpha values
         * given a layer between FIRST_LAYER and LAST_LAYER:
         * offsetY = mSlopeOffsetX * layer
         * offsetY = mSlopeOffsetY * layer
         * alpha = mSlopeAlpha * layer
         */
        int offsetX = (int)(mSlopeOffsetX * layer);
        int offsetY = (int)(mSlopeOffsetY * layer);
        float alpha = mStartAlpha - (mSlopeAlpha * layer);
        return new StackLayer(layer, offsetX, offsetY, alpha);
    }

    public int getNumberLayers(){
        return mNumberLayers;
    }

    public static class StackLayer {
        private final float mLayer;
        private final int mOffsetX;
        private final int mOffsetY;
        private final float mAlpha;

        public StackLayer(float layer, int offsetX, int offsetY, float alpha) {
            mLayer = layer;
            mOffsetX = offsetX;
            mOffsetY = offsetY;
            mAlpha = alpha;
        }

        public float getLayer() {
            return mLayer;
        }

        public int getOffsetX() { return mOffsetX; }

        public int getOffsetY() { return mOffsetY; }

        public float getAlpha() {
            return mAlpha;
        }
    }
}
