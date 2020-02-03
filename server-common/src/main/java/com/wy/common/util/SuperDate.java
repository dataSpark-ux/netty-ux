package com.wy.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tkk on 2018/8/15.
 */
public class SuperDate {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Calendar calendar;

    public SuperDate(Date date) {
        if (date == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
    }

    public SuperDate() {
        this(new Date());
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDate() {
        return calendar.get(Calendar.DATE);
    }

    public String getCleanMonthTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return dateFormat.format(calendar.getTime());
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return calendar.get(Calendar.MINUTE);
    }

    public void addDays(int i) {
        calendar.add(Calendar.DATE, i);
    }

    public Date getDateTime() {
        return calendar.getTime();
    }

    public void addMin(int i) {
        calendar.add(Calendar.MINUTE, i);
    }

    public void setSecond(int i) {
        calendar.set(Calendar.SECOND, i);
    }

    public void setMin(int i) {
        calendar.set(Calendar.MINUTE, i);
    }

    public boolean isToday() {
        SuperDate superDate = new SuperDate();
        return superDate.getYear() == this.getYear() &&
                superDate.getMonth() == this.getMonth() &&
                superDate.getDate() == this.getDate();
    }

    public SuperDate setHour(int i) {
        calendar.set(Calendar.HOUR_OF_DAY, i);
        return this;
    }

    public SuperDate setDate(int i) {
        calendar.set(Calendar.DAY_OF_MONTH, i);
        return this;
    }

    public SuperDate addMonth(int i) {
        calendar.add(Calendar.MONTH, i);
        return this;
    }

    public SuperDate addHour(int i) {
        calendar.add(Calendar.HOUR_OF_DAY, i);
        return this;
    }

    public SuperDate addSecond(int i) {
        calendar.add(Calendar.SECOND, i);
        return this;
    }
}
