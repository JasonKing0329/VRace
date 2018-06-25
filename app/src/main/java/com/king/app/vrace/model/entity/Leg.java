package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:16
 */
@Entity
public class Leg {

    @Id(autoincrement = true)
    private Long id;

    private long seasonId;

    private int index;

    private int type;

    private int seqInRaw;

    private int playerNumber;

    private String description;

    @ToOne(joinProperty = "seasonId")
    private Season season;

    @ToMany(referencedJoinProperty = "legId")
    @OrderBy("position ASC")
    private List<LegTeam> teamList;

    @ToMany(referencedJoinProperty = "legId")
    private List<LegPlaces> placeList;

    @ToMany(referencedJoinProperty = "legId")
    private List<LegSpecial> specialList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 356688205)
    private transient LegDao myDao;

    @Generated(hash = 610432906)
    public Leg(Long id, long seasonId, int index, int type, int seqInRaw,
            int playerNumber, String description) {
        this.id = id;
        this.seasonId = seasonId;
        this.index = index;
        this.type = type;
        this.seqInRaw = seqInRaw;
        this.playerNumber = playerNumber;
        this.description = description;
    }

    @Generated(hash = 1197517636)
    public Leg() {
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

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSeqInRaw() {
        return this.seqInRaw;
    }

    public void setSeqInRaw(int seqInRaw) {
        this.seqInRaw = seqInRaw;
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 658763057)
    public List<LegTeam> getTeamList() {
        if (teamList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LegTeamDao targetDao = daoSession.getLegTeamDao();
            List<LegTeam> teamListNew = targetDao._queryLeg_TeamList(id);
            synchronized (this) {
                if (teamList == null) {
                    teamList = teamListNew;
                }
            }
        }
        return teamList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 924184687)
    public synchronized void resetTeamList() {
        teamList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 522750057)
    public List<LegPlaces> getPlaceList() {
        if (placeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LegPlacesDao targetDao = daoSession.getLegPlacesDao();
            List<LegPlaces> placeListNew = targetDao._queryLeg_PlaceList(id);
            synchronized (this) {
                if (placeList == null) {
                    placeList = placeListNew;
                }
            }
        }
        return placeList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1423199708)
    public synchronized void resetPlaceList() {
        placeList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1828108454)
    public List<LegSpecial> getSpecialList() {
        if (specialList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LegSpecialDao targetDao = daoSession.getLegSpecialDao();
            List<LegSpecial> specialListNew = targetDao._queryLeg_SpecialList(id);
            synchronized (this) {
                if (specialList == null) {
                    specialList = specialListNew;
                }
            }
        }
        return specialList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 48180605)
    public synchronized void resetSpecialList() {
        specialList = null;
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
    @Generated(hash = 1033854990)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLegDao() : null;
    }

}
