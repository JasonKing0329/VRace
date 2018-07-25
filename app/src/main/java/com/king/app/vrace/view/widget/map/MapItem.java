package com.king.app.vrace.view.widget.map;

import android.graphics.Path;

import com.king.app.vrace.model.entity.MapPath;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/23 15:45
 */
public class MapItem {

    private Path path;

    private boolean isBg;

    private boolean isSelected;

    private MapPath bean;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isBg() {
        return isBg;
    }

    public void setBg(boolean bg) {
        isBg = bg;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public MapPath getBean() {
        return bean;
    }

    public void setBean(MapPath bean) {
        this.bean = bean;
    }

}
