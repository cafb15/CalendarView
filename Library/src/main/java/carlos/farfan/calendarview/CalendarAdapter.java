package carlos.farfan.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private int dayColor;
    private int dayDisabledColor;
    private int prevPosition = -1;
    private boolean dayCenter;
    private Date daySelected;

    CalendarAdapter(Context context, List<CalendarDay> days, @ColorInt int dayColor, @ColorInt int dayDisabledColor,
                    Date daySelected, boolean dayCenter) {
        super(context, R.layout.control_calendar_day, days);
        this.dayColor = dayColor;
        this.dayCenter = dayCenter;
        this.daySelected = daySelected;
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

        if (convertView == null || convertView instanceof LinearLayout) {
            convertView = inflater.inflate(dayCenter ? R.layout.control_calendar_day : R.layout.control_calendar_normal, parent,
                    false);
        }

        LinearLayout llDay = convertView.findViewById(R.id.ll_day);
        TextView tvDay = convertView.findViewById(R.id.tv_day);

        if (day.isDisabled()) {
            fillDay(tvDay, String.valueOf(calendar.get(Calendar.DATE)), dayDisabledColor);
            tvDay.setAlpha(0.5F);
        } else if (day.isCurrentMonth()) {
            fillDay(tvDay, String.valueOf(calendar.get(Calendar.DATE)), dayColor);
            click(llDay, position, day);
        }

        if (day.isDecorateSelected() && day.isCurrentMonth()) {

            if (decorate != null) {
                View view = decorate.decorate(calendar, parent);

                click(view, position, day);

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
}