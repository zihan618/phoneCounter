package com.example.a12260.szh.utils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

public class MyApplication extends Application {
    private static Context context;

    public static FragmentManager fragmentManager;


    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}
