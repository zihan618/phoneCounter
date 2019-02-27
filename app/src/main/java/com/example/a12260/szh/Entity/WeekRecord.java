package com.example.a12260.szh.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class WeekRecord {
    @Id(autoincrement = true)
    private Long id;
    private Long timestamp;
    private String packageName;
    private Long timeSpent;

    @Generated(hash = 1696592485)
    public WeekRecord(Long id, Long timestamp, String packageName, Long timeSpent) {
        this.id = id;
        this.timestamp = timestamp;
        this.packageName = packageName;
        this.timeSpent = timeSpent;
    }
    @Generated(hash = 1197863028)
    public WeekRecord() {
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
}
