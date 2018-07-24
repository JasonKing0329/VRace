package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/24 13:51
 */
@Entity(nameInDb = "map_country")
public class MapCountry {

    @Id
    private Long id;

    private long mapId;

    private String nameInMap;

    private String nameShort;

    private String nameChn;

    @Generated(hash = 833725247)
    public MapCountry(Long id, long mapId, String nameInMap, String nameShort,
            String nameChn) {
        this.id = id;
        this.mapId = mapId;
        this.nameInMap = nameInMap;
        this.nameShort = nameShort;
        this.nameChn = nameChn;
    }

    @Generated(hash = 1677277748)
    public MapCountry() {
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

    public String getNameInMap() {
        return this.nameInMap;
    }

    public void setNameInMap(String nameInMap) {
        this.nameInMap = nameInMap;
    }

    public String getNameShort() {
        return this.nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getNameChn() {
        return this.nameChn;
    }

    public void setNameChn(String nameChn) {
        this.nameChn = nameChn;
    }
}
