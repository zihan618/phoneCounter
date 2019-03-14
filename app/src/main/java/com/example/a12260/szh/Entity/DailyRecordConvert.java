package com.example.a12260.szh.Entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

public class DailyRecordConvert {
    private Long id;
    private Date timestamp;
    private String packageName;
    private Long timeSpent;

    public DailyRecord toDaiyRecord() {
        return new DailyRecord(id, timestamp.getTime(), packageName, timeSpent);
    }


}
