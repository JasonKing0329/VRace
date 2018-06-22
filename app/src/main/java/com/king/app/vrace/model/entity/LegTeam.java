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
@Entity(nameInDb = "leg_team")
public class LegTeam {

    @Id(autoincrement = true)
    private Long id;

    private long legId;

    private long teamId;

    private int position;

    private boolean isLast;

    private boolean eliminated;

    private String description;

    @Generated(hash = 628659864)
    public LegTeam(Long id, long legId, long teamId, int position, boolean isLast,
            boolean eliminated, String description) {
        this.id = id;
        this.legId = legId;
        this.teamId = teamId;
        this.position = position;
        this.isLast = isLast;
        this.eliminated = eliminated;
        this.description = description;
    }

    @Generated(hash = 1421994899)
    public LegTeam() {
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

    public long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getIsLast() {
        return this.isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public boolean getEliminated() {
        return this.eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
