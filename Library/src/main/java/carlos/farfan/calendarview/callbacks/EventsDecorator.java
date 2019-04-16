package carlos.farfan.calendarview.callbacks;

import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Carlos Farfan on 16/04/2019.
 */
public interface EventsDecorator {

    void showEvents(ListView lvEvents, TextView tvDots);
}