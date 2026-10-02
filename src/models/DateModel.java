package models;

import java.util.Calendar;
import java.util.Date;

public class DateModel {
    private Calendar calendar;

    public DateModel() {
        this.calendar = Calendar.getInstance();
    }

    public DateModel(Date initialDate) {
        this.calendar = Calendar.getInstance();
        if (initialDate != null) {
            this.calendar.setTime(initialDate);
        }
    }

    public Date getDate() {
        return calendar != null ? calendar.getTime() : null;
    }

    public void setDate(Date date) {
        if (date != null) {
            if (this.calendar == null) {
                this.calendar = Calendar.getInstance();
            }
            this.calendar.setTime(date);
        } else {
            this.calendar = null;
        }
    }

    public int getYear() {
        return calendar != null ? calendar.get(Calendar.YEAR) : 0;
    }

    public int getMonth() {
        return calendar != null ? calendar.get(Calendar.MONTH) : 0;
    }

    public int getDay() {
        return calendar != null ? calendar.get(Calendar.DAY_OF_MONTH) : 0;
    }

    public void setDay(int year, int month, int day) {
        if (this.calendar == null) {
            this.calendar = Calendar.getInstance();
        }
        this.calendar.set(year, month, day);
    }
}
