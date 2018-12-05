package carlos.farfan.calendarview.callbacks;

import carlos.farfan.calendarview.CalendarDay;

/**
 * Created by Carlos Farfan on 4/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public interface DayDecorator {

    boolean shouldDecorate(CalendarDay day);
}