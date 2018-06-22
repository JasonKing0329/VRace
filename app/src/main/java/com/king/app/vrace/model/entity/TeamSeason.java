package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:13
 */
@Entity(nameInDb = "team_season")
public class TeamSeason {

    @Id(autoincrement = true)
    private Long id;

    private long teamId;

    private long seasonId;

    private int endLeg;

    private int rank;

    private int episodeSeq;

    @ToOne(joinProperty = "seasonId")
    private Season season;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 782423572)
    private transient TeamSeasonDao myDao;

    @Generated(hash = 1915010882)
    public TeamSeason(Long id, long teamId, long seasonId, int endLeg, int rank,
            int episodeSeq) {
        this.id = id;
        this.teamId = teamId;
        this.seasonId = seasonId;
        this.endLeg = endLeg;
        this.rank = rank;
        this.episodeSeq = episodeSeq;
    }

    @Generated(hash = 109258180)
    public TeamSeason() {
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

    public long getSeasonId() {
        return this.seasonId;
    }

    public void setSeasonId(long seasonId) {
        this.seasonId = seasonId;
    }

    public int getEndLeg() {
        return this.endLeg;
    }

    public void setEndLeg(int endLeg) {
        this.endLeg = endLeg;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getEpisodeSeq() {
        return this.episodeSeq;
    }

    public void setEpisodeSeq(int episodeSeq) {
        this.episodeSeq = episodeSeq;
    }

    @Generated(hash = 1835506626)
    private transient Long season__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1175936364)
    public Season getSeason() {
        long __key = this.seasonId;
        if (season__resolvedKey == null || !season__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SeasonDao targetDao = daoSession.getSeasonDao();
            Season seasonNew = targetDao.load(__key);
            synchronized (this) {
                season = seasonNew;
                season__resolvedKey = __key;
            }
        }
        return season;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 672497649)
    public void setSeason(@NotNull Season season) {
        if (season == null) {
            throw new DaoException(
                    "To-one property 'seasonId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.season = season;
            seasonId = season.getId();
            season__resolvedKey = seasonId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2091749618)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTeamSeasonDao() : null;
    }

}
