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
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Date;

/**
 * @author 12260
 */
public class DateRangeDecorator implements DayViewDecorator {
    private long first;
    private long last;

    public DateRangeDecorator(long first, long last) {
        this.first = first;
        this.last = last;
        System.out.println("last: -- " + new Date(last));
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        long t = day.getCalendar().getTimeInMillis();
        return !(t >= first && t <= last);
    }

    @Override
    public void decorate(DayViewFacade view) {
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.5f));
        //  view.setDaysDisabled(false);
        view.setDaysDisabled(true);
        //   view.addSpan(new ForegroundColorSpan(MyApplication.getContext().getColor(R.color.piuk)));
    }
}