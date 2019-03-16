package com.szh.a12260.phone_counter.component;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * @author 12260
 */
public class DateRangeDecorator implements DayViewDecorator {
    private long first;
    private long last;

    public DateRangeDecorator(long first, long last) {
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
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.5f));
        //  view.setDaysDisabled(false);
        view.setDaysDisabled(true);
        //   view.addSpan(new ForegroundColorSpan(MyApplication.getContext().getColor(R.color.piuk)));
    }
}