package com.example.a12260.szh.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtils {
    public static void showMessage(View view, String message) {
        Snackbar.make(view, "提示消息", Snackbar.LENGTH_LONG)
                .setAction("按钮", v -> {
                    //点击右侧的按钮之后的操作
                }).show();
    }
}
