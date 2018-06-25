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
 * @date: 2018/6/20 20:16
 */
@Entity(nameInDb = "leg_team")
public class LegTeam {

    @Id(autoincrement = true)
    private Long id;

    private long legId;

    private long seasonId;

    private long teamId;

    private int position;

    private boolean isLast;

    private boolean eliminated;

    private String description;

    @ToOne(joinProperty = "teamId")
    private Team team;

    @ToOne(joinProperty = "legId")
    private Leg leg;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 241954552)
    private transient LegTeamDao myDao;

    @Generated(hash = 1834174654)
    private transient Long team__resolvedKey;

    @Generated(hash = 409543821)
    private transient Long leg__resolvedKey;

    @Generated(hash = 1451976245)
    public LegTeam(Long id, long legId, long seasonId, long teamId, int position, boolean isLast,
            boolean eliminated, String description) {
        this.id = id;
        this.legId = legId;
        this.seasonId = seasonId;
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

    public long getSeasonId() {
        return this.seasonId;
    }

    public void setSeasonId(long seasonId) {
        this.seasonId = seasonId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1469951892)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLegTeamDao() : null;
    }

}
