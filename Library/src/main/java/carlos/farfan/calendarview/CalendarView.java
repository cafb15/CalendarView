package carlos.farfan.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import carlos.farfan.calendarview.callbacks.DayDecorator;
import carlos.farfan.calendarview.callbacks.EventsDecorator;
import carlos.farfan.calendarview.callbacks.OnClickDayListener;
import carlos.farfan.calendarview.callbacks.OnDateSelectedDecorate;
import carlos.farfan.calendarview.callbacks.OnDateSelectedListener;

/**
 * Created by Carlos Farfan on 23/11/2018
 * carlos.farfan015@outlook.com
 */

public class CalendarView extends LinearLayout {

    // days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    //Elements to fill calendar
    private boolean startWithSunday;
    private String language;
    private int weekText;
    private int startCount = 3;

    private int dayColor;
    private int dayTextSize;
    private int dayTextStyle;

    private int weekColor;
    private int weekTextSize;
    private int weekTextStyle;
    private boolean dayWeekHighlight;
    private int dayWeekHighlightColor;

    private int monthColor;
    private int monthTextSize;
    private int monthTextStyle;

    private int dayLayoutHeight;
    private int dayLayout;
    private int dayDisabledColor;
    private boolean disableSunday;
    private List<CalendarDay> days;
    private DayDecorator decorator;
    private Drawable buttonLeft;
    private Drawable buttonRight;
    private Date daySelected;
    private int dayLayoutValue;
    private int spacingDaysWithMonth;

    private TitleChange titleChange;

    //WeekHighlight
    private int currentMonth;
    private int year;

    // current month
    private Calendar currentDate = Calendar.getInstance();
    private CalendarAdapter adapter;

    // days to limit calendar
    private Date minDate;
    private Date maxDate;

    //internal components
    private LinearLayout llHeader;
    private ImageView ivPrevious;
    private ImageView ivNext;
    private GridView gvDays;

    //callbacks
    private OnDateSelectedListener listener;
    private OnDateSelectedDecorate decorate;
    private EventsDecorator eventsDecorator;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    /**
     *
     * Load component XML layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.control_calendar, this);

        loadStyle(context, attrs);
        uiElements();
        limitDefaults();
        daysInMonth(false);
        validateDaysDecorated();
    }

    private void loadStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        Locale locale = Locale.getDefault();
        language = locale.getLanguage();

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        try {
            startWithSunday = typedArray.getBoolean(R.styleable.CalendarView_startWithSunday, language.equals("en"));
            weekText = typedArray.getInt(R.styleable.CalendarView_weekText, WeekTextValue.NORMAL_ALL_CAPS.value());
            dayWeekHighlight = typedArray.getBoolean(R.styleable.CalendarView_dayWeekHighlight, true);
            dayWeekHighlightColor = typedArray.getColor(R.styleable.CalendarView_dayWeekHighlightColor, Color.WHITE);
            dayColor = typedArray.getColor(R.styleable.CalendarView_dayColor, Color.BLACK);
            monthColor = typedArray.getColor(R.styleable.CalendarView_monthColor, Color.BLACK);
            weekColor = typedArray.getColor(R.styleable.CalendarView_weekColor, Color.BLACK);
            dayDisabledColor = typedArray.getColor(R.styleable.CalendarView_dayDisabledColor, Color.GRAY);
            disableSunday = typedArray.getBoolean(R.styleable.CalendarView_disableSunday, false);
            dayLayoutValue = typedArray.getInt(R.styleable.CalendarView_dayLayout, 1);
            buttonLeft = typedArray.getDrawable(R.styleable.CalendarView_buttonLeft);
            buttonRight = typedArray.getDrawable(R.styleable.CalendarView_buttonRight);
            spacingDaysWithMonth = typedArray.getDimensionPixelSize(R.styleable.CalendarView_spacingDaysWithMonth, 0);
            dayLayoutHeight = typedArray.getDimensionPixelSize(R.styleable.CalendarView_dayHeight, 0);
            dayTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_dayTextSize, 48);
            dayTextStyle = typedArray.getInt(R.styleable.CalendarView_dayTextStyle, Typeface.NORMAL);
            monthTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_monthTextSize, 48);
            monthTextStyle = typedArray.getInt(R.styleable.CalendarView_monthTextStyle, Typeface.NORMAL);
            weekTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarView_weekTextSize, 33);
            weekTextStyle = typedArray.getInt(R.styleable.CalendarView_weekTextStyle, Typeface.NORMAL);

            switch (dayLayoutValue) {
                case 1:
                    dayLayout = R.layout.control_calendar_day;
                    break;
                case 2:
                    dayLayout = R.layout.control_calendar_normal;
                    break;
                case 3:
                    dayLayout = R.layout.control_calendar_for_events;
                    break;
            }

            if (startWithSunday) {
                startCount = 2;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typedArray.recycle();
        }
    }

    private void uiElements() {
        llHeader = findViewById(R.id.calendar_header);
        ivPrevious = findViewById(R.id.calendar_prev_button);
        ivNext = findViewById(R.id.calendar_next_button);
        TextView tvMonth = findViewById(R.id.calendar_month_display);
        gvDays = findViewById(R.id.calendrar_grid);

        loadHeader();
        highlightDayWeek();

        LayoutParams layoutParams = (LayoutParams) llHeader.getLayoutParams();
        layoutParams.setMargins(0, spacingDaysWithMonth, 0, 0);
        llHeader.setLayoutParams(layoutParams);

        tvMonth.setTextColor(monthColor);
        tvMonth.setTextSize(TypedValue.COMPLEX_UNIT_PX, monthTextSize);
        tvMonth.setTypeface(tvMonth.getTypeface(), monthTextStyle);

        titleChange = new TitleChange(tvMonth);

        if (buttonLeft != null) {
            ivPrevious.setImageDrawable(buttonLeft);
        }

        if (buttonRight != null) {
            ivNext.setImageDrawable(buttonRight);
        }

        ivNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelect(1);
            }
        });
        ivPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSelect(-1);
            }
        });
    }

    private void loadHeader() {
        TextView tvDay;
        String[] weekDays = new String[0];
        int[] weekDayNumbers = getResources().getIntArray(startWithSunday ? R.array.week_days_sunday : R.array.week_days);

        switch (weekText) {
            case 1://NORMAL
                weekDays = getResources().getStringArray(startWithSunday ? R.array.week_days_normal_sunday : R.array.week_days_normal);
                break;
            case 2://NORMAL ALL CAPS
                weekDays = getResources().getStringArray(startWithSunday ? R.array.week_days_normal_all_caps_sunday :
                        R.array.week_days_normal_all_caps);
                break;
            case 3://ABBREVIATION
                weekDays = getResources().getStringArray(startWithSunday ? R.array.week_days_abr_sunday : R.array.week_days_abr);
                break;
        }

        llHeader.removeAllViews();
        for (int i = 0; i < 7; i++) {
            tvDay = new TextView(getContext());
            tvDay.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            tvDay.setTag(weekDayNumbers[i]);
            tvDay.setGravity(Gravity.CENTER);
            tvDay.setText(weekDays[i]);
            tvDay.setTextColor(weekColor);
            tvDay.setTypeface(tvDay.getTypeface(), weekTextStyle);
            tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, weekTextSize);

            llHeader.addView(tvDay);
        }
    }

    private void buttonSelect(int amount) {
        currentDate.add(Calendar.MONTH, amount);
        daysInMonth(true);
        validateDaysDecorated();
        highlightDayWeek();
        updateCalendar();
    }

    private void limitDefaults() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(1900, Calendar.JANUARY, 1);
        minDate = calendar.getTime();

        calendar.set(2100, Calendar.DECEMBER, 31);
        maxDate = calendar.getTime();
    }

    private void daysInMonth(boolean changeMonth) {
        days = new ArrayList<>();

        if (daySelected != null && !changeMonth) {
            currentDate.setTime(daySelected);
        }

        Calendar calendar = (Calendar) currentDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthInitCell = calendar.get(Calendar.DAY_OF_WEEK) - startCount;

        calendar.set(Calendar.DAY_OF_MONTH, -monthInitCell);

        while (days.size() < DAYS_COUNT) {
            days.add(new CalendarDay(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void updateCalendar() {
        enabledView(ivPrevious, canGoBack());
        enabledView(ivNext, canGoNext());

        adapter = new CalendarAdapter(getContext(), days, dayColor, dayDisabledColor, daySelected, dayLayout, dayLayoutHeight,
                dayTextSize, dayTextStyle, dayLayoutValue);
        gvDays.setAdapter(adapter);

        titleChange.change(currentDate);
        adapter.setOnClickDayListener(new OnClickDayListener() {
            @Override
            public void onClickDay(CalendarDay day) {
                daySelected = day.getDate();
                listener.onDateSelected(day, adapter);
            }
        });

        adapter.setDaySelectedDecorate(decorate);
        adapter.setEventsDayDecorate(eventsDecorator);
    }

    private void validateDaysDecorated() {
        Calendar calendar = Calendar.getInstance();
        int month;

        for (CalendarDay day : days) {
            calendar.setTime(day.getDate());
            month = calendar.get(Calendar.MONTH);
            day.setCurrentMonth(isCurrentMonth(month));
            day.setDisabled((day.isCurrentMonth() && isInRange(day)) || (day.isCurrentMonth() && disableSunday(calendar.get(Calendar.DAY_OF_WEEK))));
        }

        updateCalendar();
    }

    private boolean isCurrentMonth(int month) {
        return month == currentDate.get(Calendar.MONTH);
    }

    private boolean isInRange(CalendarDay day) {
        if (!(minDate.after(day.getDate()) || maxDate.before(day.getDate()))) {
            return decorator != null && decorator.shouldDecorate(day);
        }
        return true;
    }

    private boolean disableSunday(int day) {
        return disableSunday && day == Calendar.SUNDAY;
    }

    private void highlightDayWeek() {
        if (dayWeekHighlight) {
            int childCount = llHeader.getChildCount();

            for (int i = 0; i < childCount; i++) {
                TextView tvDayWeek = (TextView) llHeader.getChildAt(i);

                if (((int) tvDayWeek.getTag()) == currentDate.get(Calendar.DAY_OF_WEEK)
                        && currentMonth == currentDate.get(Calendar.MONTH) && year == currentDate.get(Calendar.YEAR)) {
                    tvDayWeek.setTextColor(dayWeekHighlightColor);
                    break;
                } else {
                    tvDayWeek.setTextColor(weekColor);
                }
            }
        }
    }

    private boolean canGoBack() {
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.add(Calendar.MONTH, -1);

        return calendar.get(Calendar.MONTH) >= Util.month(minDate) && calendar.get(Calendar.YEAR) >= Util.year(minDate);
    }

    private boolean canGoNext() {
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.add(Calendar.MONTH, 1);

        return calendar.get(Calendar.MONTH) <= Util.month(maxDate) && calendar.get(Calendar.YEAR) >= Util.year(minDate);
    }

    private void enabledView(View view, boolean enable) {
        view.setEnabled(enable);
        view.setAlpha(enable ? 1f : .1f);
    }

    public void setMinDate(Date date) {
        this.minDate = Util.setDate(date, 0);
        validateDaysDecorated();
    }

    public void setMaxDate(Date date) {
        this.maxDate = Util.setDate(date, 24);
        validateDaysDecorated();
    }

    public void setDaySelected(Date daySelected) {
        this.daySelected = daySelected;
        daysInMonth(false);
        validateDaysDecorated();
    }

    public void addDecorator(DayDecorator dayDecorator) {
        decorator = dayDecorator;
    }

    public void setOnDateSelectedListener(final OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public void setDaySelectedDecorate(OnDateSelectedDecorate decorate) {
        this.decorate = decorate;
        adapter.setDaySelectedDecorate(decorate);
    }

    public void setEventsDayDecorate(EventsDecorator eventsDecorator) {
        this.eventsDecorator = eventsDecorator;
        adapter.setEventsDayDecorate(eventsDecorator);
    }

    public void refreshCalendar() {
        adapter.notifyDataSetChanged();
    }
}