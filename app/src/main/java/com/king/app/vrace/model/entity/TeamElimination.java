package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/17 10:09
 */
@Entity(nameInDb = "team_elimination")
public class TeamElimination {

    @Id(autoincrement = true)
    private Long id;

    private long seasonId;

    private long teamId;

    private long legId;

    private long reasonId;

    @ToOne(joinProperty = "teamId")
    private Team team;

    @ToOne(joinProperty = "legId")
    private Leg leg;

    @ToOne(joinProperty = "seasonId")
    private Season season;

    @ToOne(joinProperty = "reasonId")
    private EliminationReason reason;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 494550899)
    private transient TeamEliminationDao myDao;

    @Generated(hash = 224802513)
    private transient Long reason__resolvedKey;

    @Generated(hash = 1834174654)
    private transient Long team__resolvedKey;

    @Generated(hash = 409543821)
    private transient Long leg__resolvedKey;

    @Generated(hash = 1835506626)
    private transient Long season__resolvedKey;

    @Generated(hash = 2097918907)
    public TeamElimination(Long id, long seasonId, long teamId, long legId,
            long reasonId) {
        this.id = id;
        this.seasonId = seasonId;
        this.teamId = teamId;
        this.legId = legId;
        this.reasonId = reasonId;
    }

    @Generated(hash = 976840431)
    public TeamElimination() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSeasonId() {
        return this.seasonId;
    }

    public void setSeasonId(long seasonId) {
        this.seasonId = seasonId;
    }

    public long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getLegId() {
        return this.legId;
    }

    public void setLegId(long legId) {
        this.legId = legId;
    }

    public long getReasonId() {
        return this.reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 740317)
    public EliminationReason getReason() {
        long __key = this.reasonId;
        if (reason__resolvedKey == null || !reason__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EliminationReasonDao targetDao = daoSession.getEliminationReasonDao();
            EliminationReason reasonNew = targetDao.load(__key);
            synchronized (this) {
                reason = reasonNew;
                reason__resolvedKey = __key;
            }
        }
        return reason;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1475023204)
    public void setReason(@NotNull EliminationReason reason) {
        if (reason == null) {
            throw new DaoException(
                    "To-one property 'reasonId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.reason = reason;
            reasonId = reason.getId();
            reason__resolvedKey = reasonId;
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

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1193690988)
    public Team getTeam() {
        long __key = this.teamId;
        if (team__resolvedKey == null || !team__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TeamDao targetDao = daoSession.getTeamDao();
            Team teamNew = targetDao.load(__key);
            synchronized (this) {
                team = teamNew;
                team__resolvedKey = __key;
            }
        }
        return team;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1078274893)
    public void setTeam(@NotNull Team team) {
        if (team == null) {
            throw new DaoException(
                    "To-one property 'teamId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.team = team;
            teamId = team.getId();
            team__resolvedKey = teamId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 848126557)
    public Leg getLeg() {
        long __key = this.legId;
        if (leg__resolvedKey == null || !leg__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LegDao targetDao = daoSession.getLegDao();
            Leg legNew = targetDao.load(__key);
            synchronized (this) {
                leg = legNew;
                leg__resolvedKey = __key;
            }
        }
        return leg;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1690450095)
    public void setLeg(@NotNull Leg leg) {
        if (leg == null) {
            throw new DaoException(
                    "To-one property 'legId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.leg = leg;
            legId = leg.getId();
            leg__resolvedKey = legId;
        }
    }

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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1832866544)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTeamEliminationDao() : null;
    }
}
