package kasper_external_apps.android.alarmer.back.models;

public class CustomTime {

    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public CustomTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }
}