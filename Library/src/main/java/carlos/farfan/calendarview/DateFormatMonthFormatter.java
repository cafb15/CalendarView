package carlos.farfan.calendarview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import carlos.farfan.calendarview.callbacks.MonthFormatter;

/**
 * Created by Carlos Farfan on 5/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public class DateFormatMonthFormatter implements MonthFormatter {

    private final SimpleDateFormat dateFormat;

    public DateFormatMonthFormatter() {
        this(new SimpleDateFormat(DEFAULT_FORMAT, new Locale("es", "PE")));
    }

    public DateFormatMonthFormatter(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String format(Date date) {
        return dateFormat.format(date);
    }
}