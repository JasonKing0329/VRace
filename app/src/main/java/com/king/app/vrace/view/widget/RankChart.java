package com.king.app.vrace.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.king.app.vrace.utils.ScreenUtils;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:26
 */
public class RankChart extends View {

    private Paint mPaint = new Paint();

    private int mPadding = ScreenUtils.dp2px(10);

    private int mXAxisTextHeight = ScreenUtils.dp2px(30);

    private int mAxisLineColor = Color.parseColor("#333333");

    private int mDashColor = Color.parseColor("#dfdfdf");

    private RankChartAdapter mAdapter;

    private Point mOriginPoint;

    private int mDegreeX;

    private int mDegreeY;

    private boolean mDrawDashGrid;

    public RankChart(Context context) {
        super(context);
    }

    public RankChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(RankChartAdapter mAdapter) {
        this.mAdapter = mAdapter;
        invalidate();
    }

    public void setDrawDashGrid(boolean mDrawDashGrid) {
        this.mDrawDashGrid = mDrawDashGrid;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAdapter != null) {
            mOriginPoint = new Point(ScreenUtils.dp2px(30) + mPadding, getHeight() - mPadding - mXAxisTextHeight);
            drawXAxis(canvas);
            drawYAxis(canvas);
            if (mDrawDashGrid) {
                drawDashGrid(canvas);
            }
            drawPointAndLine(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawXAxis(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, getWidth() - mPadding, startY, mPaint);
        int width = getWidth() - mPadding - startX;
        int xCount = mAdapter.getXAxisCount();
        mDegreeX = width / xCount;
        int degree = ScreenUtils.dp2px(2);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(ScreenUtils.dp2px(12));
        textPaint.setColor(mAxisLineColor);

        for (int i = 0; i < xCount; i ++) {
            int x = startX + i * mDegreeX;
            if (i > 0) {
                canvas.drawLine(x, startY, x, startY - degree, mPaint);
            }
            // drawText不能换行，用TextPaint和StaticLayout
//            mPaint.setTextSize(ScreenUtils.dp2px(14));
//            int textTop = ScreenUtils.dp2px(14);
//            canvas.drawText(mAdapter.getXAxisName(i), x - 20, startY + textTop, mPaint);

            // StaticLayout只能画在canvas的0,0上，因此必须通过translate画布实现，注意save和restore
            StaticLayout layout = new StaticLayout(mAdapter.getXAxisName(i), textPaint
                    , mDegreeX, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);
            canvas.save();
            canvas.translate(x - 20, startY);
            layout.draw(canvas);
            canvas.restore();
        }
    }

    private void drawYAxis(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mAxisLineColor);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
        canvas.drawLine(startX, startY, startX, mPadding, mPaint);
        int height = startY - mPadding;
        int yCount = mAdapter.getYAxisCount();
        mDegreeY = height / yCount;
        int degree = ScreenUtils.dp2px(2);

        mPaint.setTextSize(ScreenUtils.dp2px(14));
        int textTop = ScreenUtils.dp2px(5);
        for (int i = 0; i < yCount; i ++) {
            int y = startY - i * mDegreeY;
            if (i > 0) {
                canvas.drawLine(startX, y, startX + degree, y, mPaint);
            }
            canvas.drawText(mAdapter.getYAxisName(i), mPadding, y + textTop, mPaint);
        }
    }

    private void drawDashGrid(Canvas canvas) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        mPaint.setColor(mDashColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(0.5f));
        mPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        int xCount = mAdapter.getXAxisCount();
        int yCount = mAdapter.getYAxisCount();
        for (int i = 0; i < xCount; i ++) {
            int x = startX + i * mDegreeX;
            if (i > 0) {
//                canvas.drawLine(x, startY, x, mPadding, mPaint);
                Path path = new Path();
                path.moveTo(x, startY);
                path.lineTo(x, mPadding);
                canvas.drawPath(path, mPaint);
            }
        }
        for (int i = 0; i < yCount; i ++) {
            int y = startY - i * mDegreeY;
            if (i > 0) {
//                canvas.drawLine(startX, y, getWidth() - mPadding, y, mPaint);
                Path path = new Path();
                path.moveTo(startX, y);
                path.lineTo(getWidth() - mPadding, y);
                canvas.drawPath(path, mPaint);
            }
        }

        mPaint.reset();
    }

    private void drawPointAndLine(Canvas canvas) {
        int lineNumber = mAdapter.getLineCount();
        Point startPoint;
        for (int i = 0; i < lineNumber; i ++) {
            mPaint.setColor(mAdapter.getLineColor(i));
            startPoint = null;
            for (int j = 0; j < mAdapter.getXAxisCount(); j ++) {
                Integer value = mAdapter.getValue(i, j);
                int index = findValueIndex(value);
                if (index == -1) {
                    startPoint = null;
                    continue;
                }

                Point point = getPoint(j, index);
                if (startPoint != null) {
                    mPaint.setStrokeWidth(ScreenUtils.dp2px(1));
                    canvas.drawLine(startPoint.x, startPoint.y, point.x, point.y, mPaint);
                }
                mPaint.setStrokeWidth(ScreenUtils.dp2px(2));
                canvas.drawPoint(point.x, point.y, mPaint);
                startPoint = point;

                if (j == mAdapter.getXAxisCount() - 1) {
                    mPaint.setTextSize(ScreenUtils.dp2px(12));
                    canvas.drawText(mAdapter.getLineText(i), point.x + 10, point.y, mPaint);
                }
            }
        }
    }

    private int findValueIndex(Integer value) {
        if (value != null) {
            for (int i = 0; i < mAdapter.getYAxisCount(); i ++) {
                if (mAdapter.getYAxisValue(i) != null && mAdapter.getYAxisValue(i) == value) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Point getPoint(int indexX, int indexY) {
        int startY = mOriginPoint.y;
        int startX = mOriginPoint.x;
        return new Point(startX + indexX * mDegreeX, startY - indexY * mDegreeY);
    }
}
