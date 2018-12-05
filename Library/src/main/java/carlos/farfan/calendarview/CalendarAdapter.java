package carlos.farfan.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
    private CalendarDay daySelected;

    CalendarAdapter(Context context, List<CalendarDay> days, @ColorInt int dayColor, @ColorInt int dayDisabledColor,
                    CalendarDay daySelected) {
        super(context, R.layout.control_calendar_day, days);
        this.dayColor = dayColor;
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

        if (daySelected != null) {
            if (day.getDate().getTime() == daySelected.getDate().getTime()) {
                prevPosition = position;
                day.setDecorateSelected(true);
            }
        }

        calendar.setTime(day.getDate());

        if (convertView == null || convertView instanceof LinearLayout) {
            convertView = inflater.inflate(R.layout.control_calendar_day, parent, false);
        }

        TextView tvDay = (TextView) convertView;

        if (day.isDisabled()) {
            tvDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
            tvDay.setTextColor(dayDisabledColor);
            convertView.setAlpha(0.5F);
        } else if (day.isCurrentMonth()) {
            tvDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
            tvDay.setTextColor(dayColor);

            tvDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    daySelected = null;
                    if (prevPosition != position) {
                        if (prevPosition != -1) {
                            CalendarDay calendarDay = getItem(prevPosition);
                            calendarDay.setDecorateSelected(false);
                        }

                        prevPosition = position;
                        day.setDecorateSelected(true);
                    } else {
                        prevPosition = -1;
                        day.setDecorateSelected(false);
                    }

                    listener.onClickDay(day);
                }
            });
        }

        if (day.isDecorateSelected()) {
            if (decorate != null) {
                return decorate.decorate(calendar, inflater, parent);
            } else {
                tvDay.setTextColor(Color.CYAN);
            }
        }

        return convertView;
    }

    void setOnClickDayListener(OnClickDayListener listener) {
        this.listener = listener;
    }

    void setDaySelectedDecorate(OnDateSelectedDecorate decorate) {
        this.decorate = decorate;
    }
}