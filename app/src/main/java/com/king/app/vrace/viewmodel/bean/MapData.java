package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.MapBean;
import com.king.app.vrace.view.widget.map.LinePoint;
import com.king.app.vrace.view.widget.map.MapItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/24 9:28
 */
public class MapData {

    private MapBean map;

    private String legsDesc;

    private List<MapItem> itemList;

    private LinePoint linePoint;

    private String errorInfo;

    public MapBean getMap() {
        return map;
    }

    public void setMap(MapBean map) {
        this.map = map;
    }

    public List<MapItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<MapItem> itemList) {
        this.itemList = itemList;
    }

    public String getLegsDesc() {
        return legsDesc;
    }

    public void setLegsDesc(String legsDesc) {
        this.legsDesc = legsDesc;
    }

    public LinePoint getLinePoint() {
        return linePoint;
    }

    public void setLinePoint(LinePoint linePoint) {
        this.linePoint = linePoint;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
