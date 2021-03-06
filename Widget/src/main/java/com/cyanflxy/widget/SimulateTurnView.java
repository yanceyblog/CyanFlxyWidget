package com.cyanflxy.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.cyanflxy.widget.SimulateTuner.SimulatePageTurner;

public class SimulateTurnView extends View implements SimulatePageTurner.OnBitmapTurnListener {

    public interface BitmapAdapter {
        int getCount();

        Bitmap getBitmap(int index);
    }

    private int currentIndex;
    private BitmapAdapter bitmapAdapter;
    private SimulatePageTurner simulatePageTurner;

    private SparseArray<Bitmap> bitmapSparseArray;

    public SimulateTurnView(Context context, AttributeSet attrs) {
        super(context, attrs);

        simulatePageTurner = new SimulatePageTurner(context);
        simulatePageTurner.setOnBitmapTurnListener(this);
        simulatePageTurner.setDayMode();
        simulatePageTurner.setBackgroundColor(0xC0808080);

        bitmapSparseArray = new SparseArray<Bitmap>();
    }

    public void setBitmapAdapter(BitmapAdapter adapter) {
        if (adapter != bitmapAdapter) {
            clearBitmap();
        }
        currentIndex = 0;
        bitmapAdapter = adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        simulatePageTurner.setSize(getWidth(), getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        simulatePageTurner.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        simulatePageTurner.draw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        simulatePageTurner.destroy();
        clearBitmap();
        super.onDetachedFromWindow();
    }

    private Bitmap getBitmap(int index) {
        if (bitmapAdapter == null) {
            return null;
        }

        if (index < 0 || index >= bitmapAdapter.getCount()) {
            return null;
        }

        Bitmap bitmap = bitmapSparseArray.get(index);
        if (bitmap == null) {
            bitmap = bitmapAdapter.getBitmap(index);
            bitmapSparseArray.put(index, bitmap);
        }
        return bitmap;
    }

    private void clearBitmap() {
        Bitmap bitmap;
        for (int i = 0; i < bitmapSparseArray.size(); i++) {
            bitmap = bitmapSparseArray.valueAt(i);
            bitmap.recycle();
        }
        bitmapSparseArray.clear();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return getBitmap(currentIndex);
    }

    @Override
    public Bitmap getPreviousBitmap() {
        return getBitmap(currentIndex - 1);
    }

    @Override
    public Bitmap getNextBitmap() {
        return getBitmap(currentIndex + 1);
    }

    @Override
    public void invalidatePage() {
        postInvalidate();
    }

    @Override
    public void onTurningEnd(int turnType) {
        if (bitmapAdapter == null) {
            return;
        }

        if (turnType == SimulatePageTurner.TURN_TYPE_NEXT) {
            if (currentIndex < bitmapAdapter.getCount() - 1) {
                currentIndex++;
            }
        } else if (turnType == SimulatePageTurner.TURN_TYPE_PREV) {
            if (currentIndex > 0) {
                currentIndex--;
            }
        }
    }
}
