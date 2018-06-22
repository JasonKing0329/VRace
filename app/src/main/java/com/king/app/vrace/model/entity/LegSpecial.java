package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:16
 */
@Entity(nameInDb = "leg_special")
public class LegSpecial {

    @Id(autoincrement = true)
    private Long id;

    private long legId;

    private int specialType;

    @Generated(hash = 1645255172)
    public LegSpecial(Long id, long legId, int specialType) {
        this.id = id;
        this.legId = legId;
        this.specialType = specialType;
    }

    @Generated(hash = 1376358015)
    public LegSpecial() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLegId() {
        return this.legId;
    }

    public void setLegId(long legId) {
        this.legId = legId;
    }

    public int getSpecialType() {
        return this.specialType;
    }

    public void setSpecialType(int specialType) {
        this.specialType = specialType;
    }
}
