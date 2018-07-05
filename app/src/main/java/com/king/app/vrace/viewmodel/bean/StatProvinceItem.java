package com.king.app.vrace.viewmodel.bean;

import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem;

import java.util.List;

/**
 * @desc
 * @auth 景阳
 * @time 2018/6/28 0028 20:31
 */

public class StatProvinceItem implements ExpandableListItem {

    private String province;
    private int count;

    private List<StatTeamItem> list;
    public boolean mExpanded = false;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setChildItemList(List<StatTeamItem> list) {
        this.list = list;
    }

    @Override
    public List<StatTeamItem> getChildItemList() {
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
