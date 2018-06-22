package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:13
 */
@Entity(nameInDb = "team_player")
public class TeamPlayers {

    @Id(autoincrement = true)
    private Long id;

    private long teamId;

    private long playerId;

    @Generated(hash = 1769470966)
    public TeamPlayers(Long id, long teamId, long playerId) {
        this.id = id;
        this.teamId = teamId;
        this.playerId = playerId;
    }

    @Generated(hash = 1296249600)
    public TeamPlayers() {
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

    public long getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

}
