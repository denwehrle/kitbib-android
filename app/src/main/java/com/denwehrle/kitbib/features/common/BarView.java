package com.denwehrle.kitbib.features.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.denwehrle.kitbib.R;

/**
 * @author Dennis Wehrle
 */
public class BarView extends FrameLayout {

    private final View occupiedView;
    private final View freeView;

    // values
    private int max;
    private int occupied;

    public BarView(Context context) {
        this(context, null);
    }

    public BarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_bar, this, true);

        this.occupiedView = findViewById(R.id.occupied_bar);
        this.freeView = findViewById(R.id.free_bar);
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
        invalidate();
    }

    @Override
    public void invalidate() {
        if (occupied == max) {
            setOccupation(100);
        } else {
            int percentage = (int) ((100.0 * occupied) / max);

            setOccupation(percentage);
        }
        super.invalidate();
    }

    private void setOccupation(int occupation) {
        int free = 100 - occupation;

        // get the old layout parameter and change the weight dynamically
        LinearLayout.LayoutParams occupiedLayoutParams = (LinearLayout.LayoutParams) occupiedView.getLayoutParams();
        LinearLayout.LayoutParams freeLayoutParams = (LinearLayout.LayoutParams) freeView.getLayoutParams();

        occupiedLayoutParams.weight = occupation;
        freeLayoutParams.weight = free;

        occupiedView.setLayoutParams(occupiedLayoutParams);
        freeView.setLayoutParams(freeLayoutParams);
    }
}