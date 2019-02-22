package com.example.a12260.szh.utils;

import java.util.HashMap;
import java.util.Map;

public class AppPackageNameMapper {
    private Map<String, String> map1;
    private Map<String, String> map2;
    public void register(String packName, String appName) {
        map1.put(packName, appName);
        map2.put(appName,packName);
    }
    public static AppPackageNameMapper getInstance() {
        return appPackageNameMapper;
    }
    public String getAppName(String packName) {
        return map1.get(packName);
    }

    public String getPackName(String appName) {
        return map2.get(appName);
    }
    private AppPackageNameMapper() {
        this.map1 = new HashMap<>();
        this.map2 = new HashMap<>();
    }
    private static AppPackageNameMapper appPackageNameMapper = new AppPackageNameMapper();

}
