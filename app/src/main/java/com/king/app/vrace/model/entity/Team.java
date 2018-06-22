package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 19:57
 */
@Entity
public class Team {

    @Id(autoincrement = true)
    private Long id;

    private String province;

    private String city;

    private String code;

    private int genderType;

    private long relationshipId;

    @ToOne(joinProperty = "relationshipId")
    private Relationship relationship;

    @ToMany(referencedJoinProperty = "teamId")
    private List<TeamSeason> seasonList;

    @ToMany
    @JoinEntity(
            entity = TeamPlayers.class,
            sourceProperty = "teamId",
            targetProperty = "playerId"
    )
    private List<Player> playerList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1539804063)
    private transient TeamDao myDao;

    @Generated(hash = 561745484)
    public Team(Long id, String province, String city, String code, int genderType, long relationshipId) {
        this.id = id;
        this.province = province;
        this.city = city;
        this.code = code;
        this.genderType = genderType;
        this.relationshipId = relationshipId;
    }

    @Generated(hash = 882286361)
    public Team() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getGenderType() {
        return this.genderType;
    }

    public void setGenderType(int genderType) {
        this.genderType = genderType;
    }

    public long getRelationshipId() {
        return this.relationshipId;
    }

    public void setRelationshipId(long relationshipId) {
        this.relationshipId = relationshipId;
    }

    @Generated(hash = 965947211)
    private transient Long relationship__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 69943963)
    public Relationship getRelationship() {
        long __key = this.relationshipId;
        if (relationship__resolvedKey == null
                || !relationship__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RelationshipDao targetDao = daoSession.getRelationshipDao();
            Relationship relationshipNew = targetDao.load(__key);
            synchronized (this) {
                relationship = relationshipNew;
                relationship__resolvedKey = __key;
            }
        }
        return relationship;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1024153900)
    public void setRelationship(@NotNull Relationship relationship) {
        if (relationship == null) {
            throw new DaoException(
                    "To-one property 'relationshipId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.relationship = relationship;
            relationshipId = relationship.getId();
            relationship__resolvedKey = relationshipId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1214621875)
    public List<TeamSeason> getSeasonList() {
        if (seasonList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TeamSeasonDao targetDao = daoSession.getTeamSeasonDao();
            List<TeamSeason> seasonListNew = targetDao._queryTeam_SeasonList(id);
            synchronized (this) {
                if (seasonList == null) {
                    seasonList = seasonListNew;
                }
            }
        }
        return seasonList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1479285937)
    public synchronized void resetSeasonList() {
        seasonList = null;
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

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1861065475)
    public List<Player> getPlayerList() {
        if (playerList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlayerDao targetDao = daoSession.getPlayerDao();
            List<Player> playerListNew = targetDao._queryTeam_PlayerList(id);
            synchronized (this) {
                if (playerList == null) {
                    playerList = playerListNew;
                }
            }
        }
        return playerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1310905913)
    public synchronized void resetPlayerList() {
        playerList = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 256592523)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTeamDao() : null;
    }

}
