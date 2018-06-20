package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 19:25
 */
@Entity
public class Season {

    @Id(autoincrement = true)
    private Long id;

    private int index;

    private long dateFilming;

    private long dateAir;

    private int team;

    private int leg;

    private int type;

    private int teamType;

    private String theme;

    @Generated(hash = 2042846951)
    public Season(Long id, int index, long dateFilming, long dateAir, int team,
            int leg, int type, int teamType, String theme) {
        this.id = id;
        this.index = index;
        this.dateFilming = dateFilming;
        this.dateAir = dateAir;
        this.team = team;
        this.leg = leg;
        this.type = type;
        this.teamType = teamType;
        this.theme = theme;
    }

    @Generated(hash = 1022390091)
    public Season() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getDateFilming() {
        return this.dateFilming;
    }

    public void setDateFilming(long dateFilming) {
        this.dateFilming = dateFilming;
    }

    public long getDateAir() {
        return this.dateAir;
    }

    public void setDateAir(long dateAir) {
        this.dateAir = dateAir;
    }

    public int getTeam() {
        return this.team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getLeg() {
        return this.leg;
    }

    public void setLeg(int leg) {
        this.leg = leg;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTeamType() {
        return this.teamType;
    }

    public void setTeamType(int teamType) {
        this.teamType = teamType;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
