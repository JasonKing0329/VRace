package com.king.app.vrace.viewmodel.bean;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 20:31
 */

public class StatTeamItem {

    private int headerPosition;
    private int itemPosition;

    private TeamListItem bean;

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

    public TeamListItem getBean() {
        return bean;
    }

    public void setBean(TeamListItem bean) {
        this.bean = bean;
    }
}
