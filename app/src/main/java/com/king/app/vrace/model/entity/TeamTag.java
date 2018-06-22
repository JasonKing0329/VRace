package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:12
 */
@Entity(nameInDb = "team_tag")
public class TeamTag {

    @Id(autoincrement = true)
    private Long id;

    private long teamId;

    private long tagId;

    @Generated(hash = 1211591913)
    public TeamTag(Long id, long teamId, long tagId) {
        this.id = id;
        this.teamId = teamId;
        this.tagId = tagId;
    }

    @Generated(hash = 1767671675)
    public TeamTag() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
