package com.king.app.vrace.viewmodel.bean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 20:31
 */

public class StatCountryItem  {

    private int headerPosition;
    private int itemPosition;

    private StatisticPlaceItem bean;

    public int getHeaderPosition() {
        return headerPosition;
    }

    public void setHeaderPosition(int headerPosition) {
        this.headerPosition = headerPosition;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public StatisticPlaceItem getBean() {
        return bean;
    }

    public void setBean(StatisticPlaceItem bean) {
        this.bean = bean;
    }
}
