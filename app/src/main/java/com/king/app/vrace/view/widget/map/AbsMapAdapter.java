package com.king.app.vrace.view.widget.map;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/23 15:38
 */
public abstract class AbsMapAdapter {

    protected List<MapItem> mapItems;

    public abstract int getMapWidth();

    public abstract int getMapHeight();

    public abstract void onSelectPlace(int position, boolean selected);

    public List<MapItem> getMapItems() {
        return mapItems;
    }

    public void setMapItems(List<MapItem> mapItems) {
        this.mapItems = mapItems;
    }
}
