package carlos.farfan.sample;

import java.util.List;

/**
 * Created by Carlos Farfan on 4/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public class Util {

    public static boolean[] getDisabledDays(List<Day> days) {
        boolean[] disabledDays = new boolean[32];

        for (Day day : days) {
            disabledDays[day.day] = day.disabled;
        }

        return disabledDays;
    }
}