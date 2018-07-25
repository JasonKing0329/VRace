package com.king.app.vrace.viewmodel.bean;

import com.king.app.vrace.model.entity.Leg;
import com.king.app.vrace.view.widget.map.MapItem;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/25 9:26
 */
public class LegMapItem {

    private String countryInMap;
    private String countryChn;
    private List<Leg> legs;

    private MapItem mapItem;

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public String getCountryInMap() {
        return countryInMap;
    }

    public void setCountryInMap(String countryInMap) {
        this.countryInMap = countryInMap;
    }

    public String getCountryChn() {
        return countryChn;
    }

    public void setCountryChn(String countryChn) {
        this.countryChn = countryChn;
    }

    public MapItem getMapItem() {
        return mapItem;
    }

    public void setMapItem(MapItem mapItem) {
        this.mapItem = mapItem;
    }
}
