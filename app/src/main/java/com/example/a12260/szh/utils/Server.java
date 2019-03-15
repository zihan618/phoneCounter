package com.example.a12260.szh.utils;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.DailyRecordConvert;
import com.example.a12260.szh.Entity.PackageApp;
import com.example.a12260.szh.R;
import com.example.a12260.szh.model.WebResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 12260
 */
public class Server {
    static String url = MyApplication.getContext().getString(R.string.server_address);
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static Gson gson = new Gson();

    public static List<DailyRecord> getRecords(Long userId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url + "record?userId=" + userId).build();
            DailyRecordConvert[] dailyRecords = gson.fromJson(client.newCall(request).execute().body().string(), DailyRecordConvert[].class);
            return Arrays.stream(dailyRecords).map(DailyRecordConvert::toDaiyRecord).collect(Collectors.toList());
    }

    public static boolean pushRecord(List<DailyRecord> records, Long userId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, gson.toJson(records));
        Request request = new Request.Builder().post(body).url(url + "record?userId=" + userId).build();
            client.newCall(request).execute();
            return true;
    }

    public static Integer login(String name, String psw) throws IOException {
        Map<String, String> map = new HashMap<>(2);
        map.put("name", name);
        map.put("password", psw);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, gson.toJson(map));
        Request request = new Request.Builder().post(body).url(url + "login").build();
            Response response = client.newCall(request).execute();
            WebResult res = gson.fromJson(response.body().string(), WebResult.class);
            System.out.println("结果是这样的" + res.getSuccess() + res.getData());
            if (res.getSuccess()) {
                return (Double.valueOf(res.getData().toString()).intValue());
            }
        return null;
    }

    public static List<PackageApp> getKV(Long userId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url + "KV?userId=" + userId).build();
            PackageApp[] dailyRecords = gson.fromJson(client.newCall(request).execute().body().string(), PackageApp[].class);
            return Arrays.stream(dailyRecords).collect(Collectors.toList());
    }

    public static boolean pushKV(List<PackageApp> packageApps, Long userId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, gson.toJson(packageApps));
        Request request = new Request.Builder().post(body).url(url + "KV?userId=" + userId).build();
            client.newCall(request).execute();
            return true;
    }
}
