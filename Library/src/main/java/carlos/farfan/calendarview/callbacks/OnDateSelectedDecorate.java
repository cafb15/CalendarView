package carlos.farfan.calendarview.callbacks;

import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

/**
 * Created by Carlos Farfan on 5/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public interface OnDateSelectedDecorate {

    View decorate(Calendar day, ViewGroup parent);
}