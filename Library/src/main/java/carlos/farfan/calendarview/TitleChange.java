package carlos.farfan.calendarview;

import android.animation.Animator;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import java.util.Calendar;

import carlos.farfan.calendarview.callbacks.AnimatorListener;
import carlos.farfan.calendarview.callbacks.MonthFormatter;

/**
 * Created by Carlos Farfan on 5/12/2018
 * ForceClose
 * carlos.farfan@forceclose.pe
 */
public class TitleChange {

    public static final int DEFAULT_Y_TRANSLATION_DP = 20;

    private TextView tvMonth;
    private final int translate;
    private MonthFormatter formatter = MonthFormatter.DEFAULT;

    private final Interpolator interpolator = new DecelerateInterpolator(2f);

    TitleChange(TextView tvMonth) {
        this.tvMonth = tvMonth;

        Resources res = tvMonth.getResources();

        translate = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_Y_TRANSLATION_DP, res.getDisplayMetrics()) * -1;
    }

    void change(Calendar date) {
        if (TextUtils.isEmpty(tvMonth.getText())) {
            doChange(date, false);
            return;
        }

        doChange(date, true);
    }

    private void doChange(Calendar date, boolean animate) {
        tvMonth.animate().cancel();
        initState();

        String title = formatter.format(date.getTime());
        String month = title.substring(0, 1).toUpperCase() + title.substring(1);

        if (!animate) {
            tvMonth.setText(month);
        } else {
            animateChange(month);
        }
    }

    private void initState() {
        translation(tvMonth, 0);
        tvMonth.setAlpha(1);
    }

    private void animateChange(final String month) {
        ViewPropertyAnimator propertyAnimator = tvMonth.animate();

        propertyAnimator
                .translationY(translate)
                .alpha(0)
                .setDuration(100)
                .setInterpolator(interpolator)
                .setListener(new AnimatorListener() {

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        initState();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animatePostChange(month);
                    }
                }).start();
    }

    private void animatePostChange(String month) {
        tvMonth.setText(month);
        translation(tvMonth, translate);

        ViewPropertyAnimator viewPropertyAnimator = tvMonth.animate();

        viewPropertyAnimator
                .translationY(0)
                .alpha(1)
                .setDuration(100)
                .setInterpolator(interpolator)
                .setListener(new AnimatorListener())
                .start();
    }

    private void translation(TextView tvMonth, int translate) {
        tvMonth.setTranslationY(translate);
    }
}