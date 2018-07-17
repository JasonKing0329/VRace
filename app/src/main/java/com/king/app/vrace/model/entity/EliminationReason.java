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
 * @date: 2018/7/17 10:07
 */
@Entity(nameInDb = "elimination_reasons")
public class EliminationReason {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private long parentId;

    @ToOne(joinProperty = "parentId")
    private EliminationReason parent;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2034180707)
    private transient EliminationReasonDao myDao;

    @Generated(hash = 1320334498)
    public EliminationReason(Long id, String name, long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    @Generated(hash = 1386944227)
    public EliminationReason() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Generated(hash = 1293412156)
    private transient Long parent__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 611344388)
    public EliminationReason getParent() {
        long __key = this.parentId;
        if (parent__resolvedKey == null || !parent__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EliminationReasonDao targetDao = daoSession.getEliminationReasonDao();
            EliminationReason parentNew = targetDao.load(__key);
            synchronized (this) {
                parent = parentNew;
                parent__resolvedKey = __key;
            }
        }
        return parent;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 712535168)
    public void setParent(@NotNull EliminationReason parent) {
        if (parent == null) {
            throw new DaoException(
                    "To-one property 'parentId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.parent = parent;
            parentId = parent.getId();
            parent__resolvedKey = parentId;
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
    @Generated(hash = 2014721486)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEliminationReasonDao() : null;
    }

}
