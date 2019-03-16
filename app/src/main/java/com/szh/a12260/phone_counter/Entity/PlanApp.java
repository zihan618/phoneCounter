package com.szh.a12260.phone_counter.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author 12260
 */
@Entity
public class PlanApp {
    @Id(autoincrement = true)
    private Long id;
    private Long planID;
    private String packageName;

    @Generated(hash = 1061320430)
    public PlanApp(Long id, Long planID, String packageName) {
        this.id = id;
        this.planID = planID;
        this.packageName = packageName;
    }

    @Generated(hash = 1052911616)
    public PlanApp() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanID() {
        return this.planID;
    }

    public void setPlanID(Long planID) {
        this.planID = planID;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
