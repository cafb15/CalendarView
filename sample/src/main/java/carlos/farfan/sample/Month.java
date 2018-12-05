package carlos.farfan.sample;

import java.util.List;

/**
 * Created by Carlos Farfan on 4/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public class Month {

    public int month;
    public int year;
    public List<Day> days;

    public Month(int year, int month, List<Day> days) {
        this.year = year;
        this.month = month;
        this.days = days;
    }
}