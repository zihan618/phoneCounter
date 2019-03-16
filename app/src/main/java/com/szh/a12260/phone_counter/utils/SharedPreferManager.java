package com.szh.a12260.phone_counter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.szh.a12260.phone_counter.R;
import com.szh.a12260.phone_counter.model.FilterRule;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 12260
 */
public class SharedPreferManager {
    private static FilterRule filterRule;

    public static boolean isLogin() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(MyApplication.getContext().getString(R.string.loginReference), Context.MODE_PRIVATE);
        int id = sp.getInt("userId", 0);
        return id != 0;
    }

    public static int getId() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(MyApplication.getContext().getString(R.string.loginReference), Context.MODE_PRIVATE);
        int id = sp.getInt("userId", 0);
        return id;
    }

    public static FilterRule getFilter() {
        if (filterRule != null) {
            return filterRule;
        }
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(MyApplication.getContext().getString(R.string.loginReference), Context.MODE_PRIVATE);
        String id = sp.getString("filter", "");
        if (StringUtils.isBlank(id)) {
            return FilterRule.getDefaultRule();
        } else {
            Gson gson = new Gson();
            filterRule = gson.fromJson(id, FilterRule.class);
            return filterRule;
        }
    }

    public static void saveFilterRule(FilterRule rule) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(MyApplication.getContext().getString(R.string.loginReference), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        sp.edit().putString("filter", gson.toJson(rule)).apply();
        filterRule = rule;
    }

    public static boolean canRecord(String pack) {
        FilterRule rule = getFilter();
        if (rule.isSelectPartOrAll() && !rule.getPackages().contains(pack)) {
            return true;
        }

        if (!rule.isSelectPartOrAll() && rule.getPackages().contains(pack)) {
            return true;
        }

        return false;
    }
}
