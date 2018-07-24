package com.king.app.vrace.page.adapter;

import com.king.app.vrace.model.entity.MapBean;
import com.king.app.vrace.utils.DebugLog;
import com.king.app.vrace.view.widget.map.AbsMapAdapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/24 9:26
 */
public class SeasonMapAdapter extends AbsMapAdapter {

    private MapBean map;

    public void setMap(MapBean map) {
        this.map = map;
    }

    @Override
    public int getMapWidth() {
        return map.getWidth();
    }

    @Override
    public int getMapHeight() {
        return map.getHeight();
    }

    @Override
    public void onSelectPlace(int position, boolean selected) {
        DebugLog.e("select " + mapItems.get(position).getBean().getPlace() + " " + selected);
    }
}
