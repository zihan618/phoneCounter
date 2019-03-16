package com.szh.a12260.phone_counter.component;

import android.text.style.RelativeSizeSpan;

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
