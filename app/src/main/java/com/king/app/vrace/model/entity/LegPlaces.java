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
 * @date: 2018/6/20 20:16
 */
@Entity(nameInDb = "leg_place")
public class LegPlaces {

    @Id(autoincrement = true)
    private Long id;

    private long legId;

    private int seq;

    private String continent;

    private String country;

    private String city;

    private String countryEng;

    private String cityEng;

    @ToOne(joinProperty = "legId")
    private Leg leg;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1551305407)
    private transient LegPlacesDao myDao;

    @Generated(hash = 409543821)
    private transient Long leg__resolvedKey;

    @Generated(hash = 810388672)
    public LegPlaces(Long id, long legId, int seq, String continent, String country,
            String city, String countryEng, String cityEng) {
        this.id = id;
        this.legId = legId;
        this.seq = seq;
        this.continent = continent;
        this.country = country;
        this.city = city;
        this.countryEng = countryEng;
        this.cityEng = cityEng;
    }

    @Generated(hash = 1678271984)
    public LegPlaces() {
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

    public String getContinent() {
        return this.continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryEng() {
        return this.countryEng;
    }

    public void setCountryEng(String countryEng) {
        this.countryEng = countryEng;
    }

    public String getCityEng() {
        return this.cityEng;
    }

    public void setCityEng(String cityEng) {
        this.cityEng = cityEng;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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
    @Generated(hash = 1104123816)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLegPlacesDao() : null;
    }

}
