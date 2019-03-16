package com.szh.a12260.phone_counter.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author 12260
 * 存储包名 app名的键值对， 主要是在app卸载之后仍然可以知道app名称
 */
@Entity
public class PackageApp {
    @Id(autoincrement = true)
    private Long id;

    private String packageName;
    private String appName;

    @Override
    public String toString() {
        return "PackageApp{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }

    @Generated(hash = 881876647)
    public PackageApp(Long id, String packageName, String appName) {
        this.id = id;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
