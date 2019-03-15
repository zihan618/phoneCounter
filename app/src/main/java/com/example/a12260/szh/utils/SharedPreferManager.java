package com.example.a12260.szh.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.a12260.szh.R;
import com.example.a12260.szh.model.FilterRule;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

public class SharedPreferManager {
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
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences(MyApplication.getContext().getString(R.string.loginReference), Context.MODE_PRIVATE);
        String id = sp.getString("filter", "");
        if (StringUtils.isBlank(id)) {
            return FilterRule.getDefaultRule();
        } else {
            Gson gson = new Gson();
            return gson.fromJson(id, FilterRule.class);
        }
    }
}
