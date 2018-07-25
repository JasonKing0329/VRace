package com.king.app.vrace.view.widget.map;

import java.util.List;

/**
 * Desc:provide the Path of places and flight trace
 *
 * @author：Jing Yang
 * @date: 2018/7/23 15:38
 */
public abstract class AbsMapAdapter {

    /**
     * the Path of places
     */
    protected List<MapItem> mapItems;

    /**
     * flight trace
     */
    protected LinePoint linePoint;

    public abstract int getMapWidth();

    public abstract int getMapHeight();

    public abstract void onSelectPlace(int position, boolean selected);

    public List<MapItem> getMapItems() {
        return mapItems;
    }

    public void setMapItems(List<MapItem> mapItems) {
        this.mapItems = mapItems;
    }

    public void setLinePoint(LinePoint linePoint) {
        this.linePoint = linePoint;
    }

    public LinePoint getLinePoint() {
        return linePoint;
    }
}
