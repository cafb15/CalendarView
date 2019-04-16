package carlos.farfan.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import carlos.farfan.calendarview.callbacks.EventsDecorator;
import carlos.farfan.calendarview.callbacks.OnClickDayListener;
import carlos.farfan.calendarview.callbacks.OnDateSelectedDecorate;

/**
 * Created by Carlos Farfan on 23/11/2018
 * carlos.farfan015@outlook.com
 */
public class CalendarAdapter extends ArrayAdapter<CalendarDay> {

    private LayoutInflater inflater;
    private OnClickDayListener listener;
    private OnDateSelectedDecorate decorate;
    private EventsDecorator eventsDecorator;

    private int dayColor;
    private int dayDisabledColor;
    private int prevPosition = -1;
    private int dayLayout;
    private int dayTextSize;
    private int dayTextStyle;
    private Date daySelected;
    private int dayLayoutValue;
    private int dayLayoutHeight;

    CalendarAdapter(Context context, List<CalendarDay> days, @ColorInt int dayColor, @ColorInt int dayDisabledColor,
                    Date daySelected, int dayLayout, int dayLayoutHeight, int dayTextSize, int dayTextStyle, int dayLayoutValue) {
        super(context, dayLayout, days);
        this.dayColor = dayColor;
        this.dayLayout = dayLayout;
        this.daySelected = daySelected;
        this.dayTextSize = dayTextSize;
        this.dayTextStyle = dayTextStyle;
        this.dayLayoutValue = dayLayoutValue;
        this.dayLayoutHeight = dayLayoutHeight;
        this.dayDisabledColor = dayDisabledColor;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        //Day to show
        final Calendar calendar = Calendar.getInstance();
        final CalendarDay day = getItem(position);
        calendar.setTime(day.getDate());

        if (daySelected != null && daySelected.getTime() == day.getDate().getTime()) {
            prevPosition = position;
            day.setDecorateSelected(true);
        }

        if (!(convertView instanceof LinearLayout)) {
            convertView = inflater.inflate(dayLayout, parent, false);
        }

        LinearLayout llDay = convertView.findViewById(R.id.ll_day);
        TextView tvDay = convertView.findViewById(R.id.tv_day);
        ListView lvEvents = convertView.findViewById(R.id.lv_events);
        TextView tvDots = convertView.findViewById(R.id.tv_points);
        tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, dayTextSize);

        changeLayoutHeight(dayLayoutHeight, llDay);

        if (day.isDisabled()) {
            fillDay(tvDay, String.valueOf(calendar.get(Calendar.DATE)), dayDisabledColor);
            tvDay.setAlpha(0.5F);
        } else if (day.isCurrentMonth() && dayLayoutValue == TypeLayout.DAY_WITH_EVENTS.value()) {
            fillDay(tvDay, String.valueOf(calendar.get(Calendar.DATE)), dayColor);
            click(llDay, position, day);

            if (eventsDecorator != null) {
                eventsDecorator.showEvents(lvEvents, tvDots);
            }
        } else if (day.isCurrentMonth()) {
            fillDay(tvDay, String.valueOf(calendar.get(Calendar.DATE)), dayColor);
            click(llDay, position, day);
        }

        if (day.isDecorateSelected() && day.isCurrentMonth()) {

            if (decorate != null) {
                View view = decorate.decorate(calendar, parent);

                click(view, position, day);
                changeLayoutHeight(dayLayoutHeight, view);

                return view;
            }

            tvDay.setTextColor(Color.CYAN);
            click(tvDay, position, day);
        }

        return convertView;
    }

    private void click(View view, final int position, final CalendarDay day) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daySelected = null;
                if (prevPosition != position) {
                    disableDaySelected(position);
                    day.setDecorateSelected(true);
                }

                listener.onClickDay(day);
            }
        });
    }

    private void fillDay(TextView tv, String day, int color) {
        tv.setText(day);
        tv.setTextColor(color);
        tv.setTypeface(tv.getTypeface(), dayTextStyle);
    }

    private void changeLayoutHeight(int height, View view) {
        if (height != 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    private void disableDaySelected(int position) {
        if (prevPosition != -1) {
            CalendarDay calendarDay = getItem(prevPosition);
            calendarDay.setDecorateSelected(false);
        }

        prevPosition = position;
    }

    void setOnClickDayListener(OnClickDayListener listener) {
        this.listener = listener;
    }

    void setDaySelectedDecorate(OnDateSelectedDecorate decorate) {
        this.decorate = decorate;
    }

    void setEventsDayDecorate(EventsDecorator eventsDecorator) {
        this.eventsDecorator = eventsDecorator;
    }
}