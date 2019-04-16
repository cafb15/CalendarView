package carlos.farfan.calendarview;

/**
 * Created by Carlos Farfan on 16/04/2019.
 */
public enum TypeLayout {

    DAY_CENTER_HORIZONTAL(1),
    DAY_CENTER(2),
    DAY_WITH_EVENTS(3);

    private int value;

    TypeLayout(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}