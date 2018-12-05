package carlos.farfan.calendarview.callbacks;

import java.util.Date;

import carlos.farfan.calendarview.DateFormatMonthFormatter;

/**
 * Created by Carlos Farfan on 5/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public interface MonthFormatter {

    String DEFAULT_FORMAT = "MMMM yyyy";
    MonthFormatter DEFAULT = new DateFormatMonthFormatter();

    String format(Date date);
}