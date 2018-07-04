package com.king.app.vrace.view.widget;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/3 16:27
 */
public abstract class RankChartAdapter {
    public abstract int getXAxisCount();
    public abstract String getXAxisName(int position);

    public abstract int getYAxisCount();
    public abstract String getYAxisName(int position);
    public abstract Integer getYAxisValue(int i);

    public abstract int getLineCount();
    public abstract int getLineColor(int position);
    public abstract String getLineText(int i);

    public abstract Integer getValue(int lineIndex, int xIndex);
}
