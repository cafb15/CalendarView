package carlos.farfan.calendarview;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Carlos Farfan on 4/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public class Util {

    public static int month(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
}