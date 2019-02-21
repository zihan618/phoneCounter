package com.example.a12260.szh.model.usage;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;

public class CustomUsageStats {
    private UsageStats usageStats;
    private Drawable icon;

    public CustomUsageStats(UsageStats usageStats, Drawable icon) {
        this.usageStats = usageStats;
        this.icon = icon;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(UsageStats usageStats) {
        this.usageStats = usageStats;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
