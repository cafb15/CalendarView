package carlos.farfan.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import carlos.farfan.calendarview.callbacks.DayDecorator;
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
    private int dayColor;
    private int dayDisabledColor;
    private List<CalendarDay> days;
    private DayDecorator decorator;
    private Drawable buttonLeft;
    private Drawable buttonRight;
    private Date daySelected;

    private TitleChange titleChange;

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
        daysInMonth();
        validateDaysDecorated();
        updateCalendar();
    }

    private void loadStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            dayColor = typedArray.getColor(R.styleable.CalendarView_dayColor, Util.getThemeAccentColor(context));
            dayDisabledColor = typedArray.getColor(R.styleable.CalendarView_dayColor, Color.GRAY);
            buttonLeft = typedArray.getDrawable(R.styleable.CalendarView_buttonLeft);
            buttonRight = typedArray.getDrawable(R.styleable.CalendarView_buttonRight);
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

    private void buttonSelect(int amount) {
        currentDate.add(Calendar.MONTH, amount);
        daysInMonth();
        validateDaysDecorated();
        updateCalendar();
    }

    private void limitDefaults() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(1900, Calendar.JANUARY, 1);
        minDate = calendar.getTime();

        calendar.set(2100, Calendar.DECEMBER, 31);
        maxDate = calendar.getTime();
    }

    private void daysInMonth() {
        days = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthInitCell = calendar.get(Calendar.DAY_OF_WEEK) - 3;

        calendar.set(Calendar.DAY_OF_MONTH, -monthInitCell);

        while (days.size() < DAYS_COUNT) {
            days.add(new CalendarDay(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void updateCalendar() {
        enabledView(ivPrevious, canGoBack());
        enabledView(ivNext, canGoNext());

        adapter = new CalendarAdapter(getContext(), days, dayColor, dayDisabledColor, daySelected);
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
    }

    private void validateDaysDecorated() {
        Calendar calendar = Calendar.getInstance();
        int month;

        for (CalendarDay day : days) {
            calendar.setTime(day.getDate());
            month = calendar.get(Calendar.MONTH);
            day.setCurrentMonth(isCurrentMonth(month));
            day.setDisabled(day.isCurrentMonth() && isInRange(day));
        }

        updateCalendar();
    }

    private boolean isCurrentMonth(int month) {
        return month == currentDate.get(Calendar.MONTH);
    }

    private boolean isInRange(CalendarDay day) {
        if (!((minDate.after(day.getDate())) || (maxDate.before(day.getDate())))) {
            return decorator != null && decorator.shouldDecorate(day);
        }
        return true;
    }

    private boolean canGoBack() {
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.add(Calendar.MONTH, -1);

        return calendar.get(Calendar.MONTH) >= Util.month(minDate);
    }

    private boolean canGoNext() {
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.add(Calendar.MONTH, 1);

        return calendar.get(Calendar.MONTH) <= Util.month(maxDate);
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
}