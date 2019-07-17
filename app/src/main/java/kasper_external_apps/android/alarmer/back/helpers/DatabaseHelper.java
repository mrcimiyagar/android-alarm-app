package kasper_external_apps.android.alarmer.back.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.back.models.CustomDate;
import kasper_external_apps.android.alarmer.back.models.CustomTime;
import kasper_external_apps.android.alarmer.back.models.RepeatData;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public String getPassword() {
        Cursor c = getReadableDatabase().rawQuery("select * from AuthData", null);
        c.moveToNext();
        String password = c.getString(1);
        c.close();
        return password;
    }

    public void setPassword(String password) {
        ContentValues values = new ContentValues();
        values.put("password", password);
        getReadableDatabase().update("AuthData", values, "id = 1", null);
    }

    private int addAlarmBase(String name, String details, int year, int month, int day, int hour
            , int minute, boolean starred, String timeModified, String timeFinished, String soundPath
            , boolean repeat) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("details", details);
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("starred", starred ? 1 : 0);
        values.put("repeat", repeat ? 1 : 0);
        values.put("timeModified", timeModified);
        values.put("timeFinished", timeFinished);
        values.put("soundPath", soundPath);
        return (int) getReadableDatabase().insert("AlarmsData", null, values);
    }

    public int addAlarm(String name, String details, int year, int month, int day, int hour
            , int minute, boolean starred, String timeModified, String timeFinished, String soundPath
            , boolean[] days) {

        boolean repeat = false;

        for (boolean b : days) {
            if (b) {
                repeat = true;
                break;
            }
        }

        int alarmId = addAlarmBase(name, details, year, month, day, hour, minute, starred, timeModified, timeFinished, soundPath, repeat);

        if (repeat) {
            ContentValues values = new ContentValues();
            values.put("id", alarmId);
            for (int counter = 0; counter < 7; counter++) {
                values.put("day" + counter, days[counter] ? 1 : 0);
            }
            getReadableDatabase().insert("RepeatData", null, values);
        }

        return alarmId;
    }

    public ArrayList<Alarm> getAllAlarmsList() {

        ArrayList<Alarm> result = new ArrayList<>();

        Cursor cr = getReadableDatabase().rawQuery("select * from RepeatData", null);

        Hashtable<Integer, boolean[]> repeatDatas = new Hashtable<>();

        while (cr.moveToNext()) {

            boolean[] days = new boolean[7];

            for (int counter = 1; counter < 8; counter++) {
                days[counter - 1] = cr.getInt(counter) == 1;
            }

            repeatDatas.put(cr.getInt(0), days);
        }

        cr.close();

        Cursor c = getReadableDatabase().rawQuery("select * from AlarmsData", null);

        while (c.moveToNext()) {

            boolean repeat = c.getInt(12) == 1;

            Alarm alarm;

            if (repeat) {
                int id = c.getInt(0);
                alarm = new Alarm(id, c.getString(1), c.getString(2),
                        new CustomDate(c.getInt(3), c.getInt(4), c.getInt(5)),
                        new CustomTime(c.getInt(6), c.getInt(7)), (c.getInt(8) == 1),
                        c.getString(9), c.getString(10), c.getString(11), repeatDatas.get(id));
            }
            else {
                alarm = new Alarm(c.getInt(0), c.getString(1), c.getString(2),
                        new CustomDate(c.getInt(3), c.getInt(4), c.getInt(5)),
                        new CustomTime(c.getInt(6), c.getInt(7)), (c.getInt(8) == 1)
                        , c.getString(9), c.getString(10), c.getString(11));
            }

            result.add(alarm);
        }

        c.close();

        return result;
    }

    public ArrayList<Alarm> getPendingAlarmsList() {

        ArrayList<Alarm> result = new ArrayList<>();

        for (Alarm alarm : getAllAlarmsList()) {

            if (alarm.getTimeFinished().length() == 0) {

                result.add(alarm);
            }
        }

        return result;
    }

    public ArrayList<Alarm> getTomorrowAlarmsList() {

        ArrayList<Alarm> result = new ArrayList<>();

        PersianCalendar tempC;

        PersianCalendar today = new PersianCalendar();

        for (Alarm alarm : getAllAlarmsList()) {

            if (alarm.getTimeFinished().length() == 0) {

                if (alarm.isRepeat()) {

                    tempC = getRepeatPersianCalendar(alarm.getDays(), alarm.getTime()
                            .getHour(), alarm.getTime().getMinute());

                    tempC.addPersianDate(PersianCalendar.DATE, -1);

                    if (tempC.getPersianDay() == today.getPersianDay() && tempC.getPersianMonth() == today
                            .getPersianMonth() && tempC.getPersianYear() == today.getPersianYear()) {
                        result.add(alarm);
                    }
                } else {

                    tempC = new PersianCalendar();

                    tempC.setPersianDate(alarm.getDate().getYear(), alarm.getDate().getMonth(), alarm.getDate().getDay());
                    tempC.addPersianDate(PersianCalendar.DATE, -1);

                    if (tempC.getPersianDay() == today.getPersianDay() && tempC.getPersianMonth() == today
                            .getPersianMonth() && tempC.getPersianYear() == today.getPersianYear()) {
                        result.add(alarm);
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<Alarm> getRepeatingAlarmsList() {

        ArrayList<Alarm> result = new ArrayList<>();

        for (Alarm alarm : getAllAlarmsList()) {

            if (alarm.getTimeFinished().length() == 0) {

                if (alarm.isRepeat()) {

                    result.add(alarm);
                }
            }
        }

        return result;
    }

    public ArrayList<Alarm> getDoneAlarmsList() {

        ArrayList<Alarm> result = new ArrayList<>();

        for (Alarm alarm : getAllAlarmsList()) {

            if (alarm.getTimeFinished().length() > 0) {
                result.add(alarm);
            }
        }

        return result;
    }

    public ArrayList<Alarm> getStarredAlarmsList() {

        ArrayList<Alarm> result = new ArrayList<>();

        for (Alarm alarm : getAllAlarmsList()) {
            if (alarm.getStarred()) {
                result.add(alarm);
            }
        }

        return result;
    }

    public Alarm getAlarmById(int id) {

        Cursor c = getReadableDatabase().rawQuery("select * from AlarmsData where id = " + id, null);

        if (c.moveToNext()) {

            boolean repeat = c.getInt(12) == 1;

            Alarm alarm;

            if (repeat) {

                Cursor cr = getReadableDatabase().rawQuery("select * from RepeatData where id = " + id, null);

                boolean[] days = new boolean[7];

                if (cr.moveToNext()) {

                    for (int counter = 1; counter < 8; counter++) {
                        days[counter - 1] = cr.getInt(counter) == 1;
                    }
                }

                cr.close();

                alarm = new Alarm(id, c.getString(1), c.getString(2),
                        new CustomDate(c.getInt(3), c.getInt(4), c.getInt(5)),
                        new CustomTime(c.getInt(6), c.getInt(7)), (c.getInt(8) == 1),
                        c.getString(9), c.getString(10), c.getString(11)
                        , days);
            } else {
                alarm = new Alarm(c.getInt(0), c.getString(1), c.getString(2),
                        new CustomDate(c.getInt(3), c.getInt(4), c.getInt(5)),
                        new CustomTime(c.getInt(6), c.getInt(7)), (c.getInt(8) == 1),
                        c.getString(9), c.getString(10), c.getString(11));
            }

            c.close();

            return alarm;
        }

        return null;
    }

    public void editAlarm(int id, String name, String details, int year, int month, int day, int hour
            , int minute, boolean starred, String timeModified, String timeFinished, String soundPath, boolean[] days) {

        boolean repeat = false;

        for (boolean b : days) {
            if (b) {
                repeat = true;
                break;
            }
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("details", details);
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("starred", starred);
        values.put("repeat", repeat ? 1 : 0);
        values.put("timeModified", timeModified);
        values.put("timeFinished", timeFinished);
        values.put("soundPath", soundPath);
        getReadableDatabase().update("AlarmsData", values, "id = " + id, null);

        if (repeat) {

            ContentValues valuesD = new ContentValues();
            for (int counter = 0; counter < 7; counter++) {
                valuesD.put("day" + counter, days[counter] ? 1 : 0);
            }

            Cursor cursor = getReadableDatabase().rawQuery("select * from RepeatData where id = " + id, null);

            if (cursor.getCount() > 0) {

                getReadableDatabase().update("RepeatData", valuesD, "id = " + id, null);
            }
            else {

                valuesD.put("id", id);
                getReadableDatabase().insert("RepeatData", null, valuesD);
            }

            cursor.close();
        }
    }

    public void deleteAlarm(int id) {
        getReadableDatabase().delete("AlarmsData", "id = " + id, null);
        getReadableDatabase().delete("RepeatData", "id = " + id, null);
    }

    // ***

    private PersianCalendar getRepeatPersianCalendar(boolean[] days, int hour, int minute) {

        Log.d("KasperLoggGGer", "hello 1");

        PersianCalendar pc = new PersianCalendar();

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

                    if (cHour < hour || (cHour == hour && cMinute < minute)) {
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