package carlos.farfan.calendarview;

/**
 * Created by Carlos Farfan on 17/04/2019.
 */
public enum WeekTextValue {

    NORMAL(1),
    NORMAL_ALL_CAPS(2),
    SHORT(3);

    private int value;

    WeekTextValue(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}