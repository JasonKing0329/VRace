package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/23 15:31
 */
@Entity(nameInDb = "map_path")
public class MapPath {

    @Id
    private Long id;

    private long mapId;

    private String pathValues;

    private String place;

    private int type;

    @Generated(hash = 1007158151)
    public MapPath(Long id, long mapId, String pathValues, String place, int type) {
        this.id = id;
        this.mapId = mapId;
        this.pathValues = pathValues;
        this.place = place;
        this.type = type;
    }

    @Generated(hash = 1555569536)
    public MapPath() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMapId() {
        return this.mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public String getPathValues() {
        return this.pathValues;
    }

    public void setPathValues(String pathValues) {
        this.pathValues = pathValues;
    }

    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
