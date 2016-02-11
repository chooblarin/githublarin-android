package com.chooblarin.githublarin.ui.widget.contributions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.chooblarin.githublarin.R;

public class ContributionsChartView extends View {

    private int width;
    private int height;
    private Rect rect;
    private Paint paint;

    private int cellSize;
    private int cellPadding;

    private int cellCount;

    public ContributionsChartView(Context context) {
        super(context);
        init();
    }

    public ContributionsChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContributionsChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        cellSize = h / 7 - cellPadding;
        this.cellCount = w / (cellSize + cellPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < cellCount; j++) {
                rect.left = j * (cellSize + cellPadding);
                rect.right = rect.left + cellSize;
                rect.top = i * (cellSize + cellPadding);
                rect.bottom = rect.top + cellSize;
                paint.setColor(getResources().getColor(R.color.cbr_color_light_gray));
                canvas.drawRect(rect, paint);
            }
        }
    }

    private void init() {
        this.rect = new Rect();
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Resources res = getResources();

        this.cellSize = res.getDimensionPixelSize(R.dimen.contributions_cell_size);
        this.cellPadding = res.getDimensionPixelSize(R.dimen.contributions_cell_margin);
    }
}
