package com.king.app.vrace.viewmodel.bean;

import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 20:31
 */

public class StatContinentItem implements ExpandableListItem {

    private String continent;
    private int count;

    private List<StatCountryItem> list;
    public boolean mExpanded = false;

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setChildItemList(List<StatCountryItem> list) {
        this.list = list;
    }

    @Override
    public List<StatCountryItem> getChildItemList() {
        return list;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mExpanded = isExpanded;
    }
}
