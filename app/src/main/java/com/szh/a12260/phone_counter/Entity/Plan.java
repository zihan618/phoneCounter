package com.szh.a12260.phone_counter.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Plan {
    @Id
    private Long id;
    /**
     * 到多久了提醒，单位是minute
     */
    private Integer timeToWarn;
    /**
     * @Link{@PlanType}
     */
    private Integer type;

    /**
     * sort index
     */
    private Integer index;
    private Boolean effective;

    //private Integer startDay, endDay;
    @Generated(hash = 137717383)
    public Plan(Long id, Integer timeToWarn, Integer type, Integer index,
                Boolean effective) {
        this.id = id;
        this.timeToWarn = timeToWarn;
        this.type = type;
        this.index = index;
        this.effective = effective;
    }

    @Generated(hash = 592612124)
    public Plan() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTimeToWarn() {
        return this.timeToWarn;
    }

    public void setTimeToWarn(Integer timeToWarn) {
        this.timeToWarn = timeToWarn;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getEffective() {
        return this.effective;
    }

    public void setEffective(Boolean effective) {
        this.effective = effective;
    }

}
