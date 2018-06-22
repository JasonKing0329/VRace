package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
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
public class Player {

    @Id(autoincrement = true)
    private Long id;

    private int gender;

    private int age;

    private String name;

    private String birthday;

    private String province;

    private String city;

    private String occupy;

    private String description;

    private long debutSeasonId;

    @ToOne(joinProperty = "debutSeasonId")
    private Season debutSeason;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2108114900)
    private transient PlayerDao myDao;

    @Generated(hash = 480360179)
    private transient Long debutSeason__resolvedKey;

    @Generated(hash = 1896570974)
    public Player(Long id, int gender, int age, String name, String birthday,
            String province, String city, String occupy, String description,
            long debutSeasonId) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.name = name;
        this.birthday = birthday;
        this.province = province;
        this.city = city;
        this.occupy = occupy;
        this.description = description;
        this.debutSeasonId = debutSeasonId;
    }

    @Generated(hash = 30709322)
    public Player() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOccupy() {
        return this.occupy;
    }

    public void setOccupy(String occupy) {
        this.occupy = occupy;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDebutSeasonId() {
        return this.debutSeasonId;
    }

    public void setDebutSeasonId(long debutSeasonId) {
        this.debutSeasonId = debutSeasonId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 600408614)
    public Season getDebutSeason() {
        long __key = this.debutSeasonId;
        if (debutSeason__resolvedKey == null
                || !debutSeason__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SeasonDao targetDao = daoSession.getSeasonDao();
            Season debutSeasonNew = targetDao.load(__key);
            synchronized (this) {
                debutSeason = debutSeasonNew;
                debutSeason__resolvedKey = __key;
            }
        }
        return debutSeason;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1361602870)
    public void setDebutSeason(@NotNull Season debutSeason) {
        if (debutSeason == null) {
            throw new DaoException(
                    "To-one property 'debutSeasonId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.debutSeason = debutSeason;
            debutSeasonId = debutSeason.getId();
            debutSeason__resolvedKey = debutSeasonId;
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
    @Generated(hash = 1600887847)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlayerDao() : null;
    }
}
