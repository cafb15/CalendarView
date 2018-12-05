package carlos.farfan.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import carlos.farfan.calendarview.CalendarAdapter;
import carlos.farfan.calendarview.CalendarDay;
import carlos.farfan.calendarview.CalendarView;
import carlos.farfan.calendarview.callbacks.DayDecorator;
import carlos.farfan.calendarview.callbacks.OnDateSelectedDecorate;
import carlos.farfan.calendarview.callbacks.OnDateSelectedListener;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", new Locale("es", "PE"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LayoutInflater inflater = getLayoutInflater();

        CalendarView calendarView = findViewById(R.id.calendar_view);

        LinkedHashMap<Integer, boolean[]> map = new LinkedHashMap<>();

        List<Day> daysDecember = new ArrayList<>();
        daysDecember.add(new Day(23, true));
        daysDecember.add(new Day(27, true));
        daysDecember.add(new Day(28, true));
        daysDecember.add(new Day(30, true));

        List<Day> daysJanuary = new ArrayList<>();
        daysJanuary.add(new Day(3, true));
        daysJanuary.add(new Day(6, true));
        daysJanuary.add(new Day(12, true));
        daysJanuary.add(new Day(22, true));
        daysJanuary.add(new Day(24, true));
        daysJanuary.add(new Day(26, true));

        List<Month> months = new ArrayList<>();
        months.add(new Month(2018, 12, daysDecember));
        months.add(new Month(2019, 1, daysJanuary));

        for (Month month : months) {
            map.put(month.month, Util.getDisabledDays(month.days));
        }

        DaysDisableDecorator.setMap(map);
        calendarView.addDecorator(new DaysDisableDecorator());

        Month dayMin = months.get(0);
        Month dayMax = months.get(months.size() - 1);
        int lastDay = dayMax.days.size() - 1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(dayMin.year, dayMin.month - 1, dayMin.days.get(0).day);
        calendarView.setMinDate(calendar.getTime());

        Calendar cal = Calendar.getInstance();
        cal.set(dayMax.year, dayMax.month - 1, dayMax.days.get(lastDay).day);
        calendarView.setMaxDate(cal.getTime());

        calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(CalendarDay day, CalendarAdapter adapter) {
                adapter.notifyDataSetChanged();
            }
        });

        calendarView.setDaySelectedDecorate(new OnDateSelectedDecorate() {
            @Override
            public View decorate(Calendar day, ViewGroup parent) {
                View view = inflater.inflate(R.layout.item_day_selected, parent, false);

                TextView tvDay = view.findViewById(R.id.tv_day);
                TextView tvHour = view.findViewById(R.id.tv_hour);

                tvDay.setText(String.valueOf(day.get(Calendar.DATE)));
                tvHour.setText(dateFormat.format(day.getTime()) + "\nhrs");

                return view;
            }
        });
    }

    private static class DaysDisableDecorator implements DayDecorator {

        private static LinkedHashMap<Integer, boolean[]> map;
        private static boolean[] daysTable;
        private static int currentMonth;

        private static void setMap(LinkedHashMap<Integer, boolean[]> hashMap) {
            map = hashMap;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            int dayMonth = day.getMonth();
            if (currentMonth != dayMonth) {
                currentMonth = dayMonth;
                month(dayMonth);
            }

            if (daysTable != null) {
                Log.i("DayDisabled", String.valueOf(day.getDay() + " " + daysTable[day.getDay()]));
                return daysTable[day.getDay()];
            }
            return false;
        }

        public void month(int month) {
            daysTable = map.get(month + 1);
        }
    }
}