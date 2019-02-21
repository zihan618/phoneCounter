package com.example.a12260.szh.logic.data_producer;

import java.util.List;

public interface AbstractUsageProvider {
    List getUsageStats(int intervalTye, long start, long end);
}
