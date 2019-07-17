package kasper_external_apps.android.alarmer.back.models;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;

public class Alarm {

    private int id;
    private String name;
    private String message;

    private CustomDate date;
    private CustomTime time;
    private long dateTimeMillis;

    private boolean starred;
    private String timeModified;
    private String timeFinished;
    private String soundPath;

    private boolean repeat;

    private boolean[] days;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public CustomDate getDate() {
        if (this.isRepeat()) {
            PersianCalendar tempPC = getRepeatPersianCalendar();
            return new CustomDate(tempPC.getPersianYear(), tempPC.getPersianMonth(), tempPC.getPersianDay());
        } else {
            return date;
        }
    }

    public CustomTime getTime() {
        return time;
    }

    public boolean getStarred() {
        return starred;
    }

    public String getTimeModified() {
        return timeModified;
    }

    public String getTimeFinished() {
        return this.timeFinished;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean[] getDays() {
        return this.days;
    }

    public Alarm(int id, String name, String message, CustomDate date, CustomTime time, boolean starred
            , String timeModified, String timeFinished, String soundPath) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
        PersianCalendar calendar = new PersianCalendar();
        calendar.setPersianDate(date.getYear(), date.getMonth(), date.getDay());
        int dayTime = (Calendar.getInstance().getTime().getHours() * 3600 * 1000
                + Calendar.getInstance().getTime().getMinutes() * 60 * 1000
                + Calendar.getInstance().getTime().getSeconds() * 1000);
        this.dateTimeMillis = calendar.getTimeInMillis() - dayTime + (time.getHour() * 3600 * 1000 + time.getMinute() * 60 * 1000);
        this.starred = starred;
        this.timeModified = timeModified;
        this.timeFinished = timeFinished;
        this.soundPath = soundPath;
        this.repeat = false;
        this.days = new boolean[7];
    }

    public Alarm(int id, String name, String message, CustomDate date, CustomTime time, boolean starred
            , String timeModified, String timeFinished, String soundPath, boolean[] days) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
        this.starred = starred;
        this.timeModified = timeModified;
        this.timeFinished = timeFinished;
        this.soundPath = soundPath;
        this.repeat = true;
        this.days = days;

        if (this.days == null) {
            this.days = new boolean[7];
        }
    }

    public boolean hasEqualDateTo(Alarm alarm) {

        return this.getDate().getYear() == alarm.getDate().getYear() &&
                this.getDate().getMonth() == alarm.getDate().getMonth() &&
                this.getDate().getDay() == alarm.getDate().getDay();

    }

    public boolean hasEqualDateTo(CustomDate date) {

        return this.getDate().getYear() == date.getYear() &&
                this.getDate().getMonth() == date.getMonth() &&
                this.getDate().getDay() == date.getDay();
    }

    public long getDateTimeValue() {
        if (this.repeat) {
            PersianCalendar tempPC = getRepeatPersianCalendar();
            int dayTime = (Calendar.getInstance().getTime().getHours() * 3600 * 1000
                    + Calendar.getInstance().getTime().getMinutes() * 60 * 1000
                    + Calendar.getInstance().getTime().getSeconds() * 1000);
            return tempPC.getTimeInMillis() - dayTime + (time.getHour() * 3600 * 1000 + time.getMinute() * 60 * 1000);
        } else {
            return this.dateTimeMillis;
        }
    }

    PersianCalendar pc;

    private PersianCalendar getRepeatPersianCalendar() {

        pc = new PersianCalendar();

        final String[] persianWeekDays = { "\u0634\u0646\u0628\u0647", // Shanbeh
                "\u06cc\u06a9\u200c\u0634\u0646\u0628\u0647", // Yekshanbeh
                "\u062f\u0648\u0634\u0646\u0628\u0647", // Doshanbeh
                "\u0633\u0647\u200c\u0634\u0646\u0628\u0647", // Sehshanbeh
                "\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647", // Chaharshanbeh
                "\u067e\u0646\u062c\u200c\u0634\u0646\u0628\u0647", // Panjshanbeh
                "\u062c\u0645\u0639\u0647" // jome
        };

        int dayIndex = 0;

        String persianDayName = pc.getPersianWeekDayName();

        for (dayIndex = 0; dayIndex < 7; dayIndex++) {
            if (persianWeekDays[dayIndex].equals(persianDayName)) {
                break;
            }
        }

        int counter = 0;

        int limit = 7;

        for (counter = 0; counter < limit; counter++) {

            if (counter == 0) {
                if (days[dayIndex]) {
                    int cHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int cMinute = Calendar.getInstance().get(Calendar.MINUTE);

                    if (cHour < time.getHour() || (cHour == time.getHour() && cMinute < time.getMinute())) {
                        break;
                    }
                    else {
                        limit = 8;
                    }
                }
            }
            else if (days[dayIndex]) {
                break;
            }

            dayIndex++;
            if (dayIndex >= 7) {
                dayIndex = 0;
            }
        }

        pc.addPersianDate(Calendar.DATE, counter);

        return pc;
    }
}