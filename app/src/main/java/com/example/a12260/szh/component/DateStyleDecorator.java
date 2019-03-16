package com.example.a12260.szh.component;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.example.a12260.szh.R;
import com.example.a12260.szh.utils.MyApplication;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class DateStyleDecorator implements DayViewDecorator {
    private long first;
    private long last;

    public DateStyleDecorator(long first, long last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        long t = day.getCalendar().getTimeInMillis();
        return (t >= first && t <= last);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //   view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.2f));
        //   view.addSpan(new ForegroundColorSpan(MyApplication.getContext().getColor(R.color.piuk)));
    }
}
