package com.szh.a12260.phone_counter.entity;

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
