package carlos.farfan.calendarview.callbacks;

import carlos.farfan.calendarview.CalendarAdapter;
import carlos.farfan.calendarview.CalendarDay;

/**
 * Created by Carlos Farfan on 5/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public interface OnDateSelectedListener {

    void onDateSelected(CalendarDay day, CalendarAdapter adapter);
}