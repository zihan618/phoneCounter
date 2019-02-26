package com.example.a12260.szh.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PackageApp {
    @Id
    private String packageName;
    @Unique
    private String appName;
    @Generated(hash = 1177092060)
    public PackageApp(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }
    @Generated(hash = 1535103291)
    public PackageApp() {
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

}
