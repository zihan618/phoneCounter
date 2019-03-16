package com.szh.a12260.phone_counter.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

@Entity(active = true)
public class DailyRecord {
    @Id(autoincrement = true)
    private Long id;
    private Long timestamp;
    private String packageName;
    private Long timeSpent;

    @Override
    public String toString() {
        return "DailyRecord{" +
                "id=" + id +
                ", timestamp=" + new Date(timestamp == null ? 0 : timestamp) +
                ", packageName='" + packageName + '\'' +
                ", timeSpent=" + timeSpent +
                '}';
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 412338051)
    private transient DailyRecordDao myDao;

    @Generated(hash = 34019572)
    public DailyRecord(Long id, Long timestamp, String packageName,
                       Long timeSpent) {
        this.id = id;
        this.timestamp = timestamp;
        this.packageName = packageName;
        this.timeSpent = timeSpent;
    }
    @Generated(hash = 1812185311)
    public DailyRecord() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public Long getTimeSpent() {
        return this.timeSpent;
    }
    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
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
    @Generated(hash = 796727999)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDailyRecordDao() : null;
    }


}
