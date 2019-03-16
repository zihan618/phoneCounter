package com.example.a12260.szh.component;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class MyDayViewDecorator implements DayViewDecorator {
    private long first;
    private long last;

    public MyDayViewDecorator(long first, long last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        long t = day.getCalendar().getTimeInMillis();
        return !(t >= first && t <= last);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
    }
}