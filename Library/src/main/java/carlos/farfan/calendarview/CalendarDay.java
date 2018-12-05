package carlos.farfan.calendarview;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Carlos Farfan on 4/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public class CalendarDay {

    private Date date;
    private boolean disabled;
    private boolean currentMonth;
    private boolean decorateSelected;

    public CalendarDay(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        this.currentMonth = currentMonth;
    }

    public boolean isDecorateSelected() {
        return decorateSelected;
    }

    public void setDecorateSelected(boolean decorateSelected) {
        this.decorateSelected = decorateSelected;
    }

    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public int getDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(date);
    }
}