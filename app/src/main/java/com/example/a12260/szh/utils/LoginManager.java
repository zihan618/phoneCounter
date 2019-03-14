package com.example.a12260.szh.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.a12260.szh.R;

public class LoginManager {
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
}
