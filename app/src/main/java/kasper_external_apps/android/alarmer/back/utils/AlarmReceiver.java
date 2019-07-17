package kasper_external_apps.android.alarmer.back.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;

import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("KasperLogger", "received alarm-id : " + intent.getExtras().getInt("alarm-id"));

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        /*Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        AlarmManager alarmManager = (AlarmManager) MyApp.getInstance().getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApp.getInstance(), intent.getExtras().getInt("alarm-id"), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        Alarm alarm = MyApp.getInstance().getDatabaseHelper().getAlarmById(intent.getExtras().getInt("alarm-id"));

        if (alarm.isRepeat()) {
            handleSequence(alarm);
        }
        else {
            PersianCalendar tempCalendar = new PersianCalendar();
            String fullDate = tempCalendar.getPersianShortDate();
            String fullTime = Calendar.getInstance().getTime().getHours() + ":" + Calendar.getInstance().getTime().getMinutes();

            String timeFinished = fullTime + " - " + fullDate;

            MyApp.getInstance().getDatabaseHelper().editAlarm(alarm.getId(), alarm.getName()
                    , alarm.getMessage(), alarm.getDate().getYear(), alarm.getDate().getMonth()
                    , alarm.getDate().getDay(), alarm.getTime().getHour(), alarm.getTime().getMinute(),
                    alarm.getStarred(), alarm.getTimeModified(), timeFinished, alarm.getSoundPath(), alarm.getDays());
        }

        if (MyApp.getInstance() != null && MyApp.getInstance().getUiUpdateCallback() != null) {
            MyApp.getInstance().getUiUpdateCallback().run();
        }
    }

    private void handleSequence(Alarm alarm) {

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
                if (alarm.getDays()[dayIndex]) {
                    int cHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int cMinute = Calendar.getInstance().get(Calendar.MINUTE);

                    if (cHour < alarm.getTime().getHour() || (cHour == alarm.getTime().getHour() &&
                            cMinute < alarm.getTime().getMinute())) {
                        break;
                    }
                    else {
                        limit = 8;
                    }
                }
            }
            else if (alarm.getDays()[dayIndex]) {
                break;
            }

            dayIndex++;
            if (dayIndex >= 7) {
                dayIndex = 0;
            }
        }

        pc.addPersianDate(Calendar.DATE, counter);

        this.createAlarm(alarm.getId(), pc.getPersianYear(), pc.getPersianMonth(), pc.getPersianDay()
                , alarm.getTime().getHour(), alarm.getTime().getMinute());
    }

    private void createAlarm(int alarmId, int year, int month, int day, int hour, int minute) {

        AlarmManager alarmManager = (AlarmManager) MyApp.getInstance().getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(MyApp.getInstance(), AlarmReceiver.class).putExtra("alarm-id", alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApp.getInstance(), alarmId, myIntent, 0);

        PersianCalendar calendar = new PersianCalendar();
        calendar.setPersianDate(year, month, day);

        int dayTime = (Calendar.getInstance().getTime().getHours() * 3600 * 1000
                + Calendar.getInstance().getTime().getMinutes() * 60 * 1000
                + Calendar.getInstance().getTime().getSeconds() * 1000);

        long finalMillis = calendar.getTimeInMillis() - dayTime + (hour * 3600 * 1000 + minute * 60 * 1000);

        alarmManager.set(AlarmManager.RTC_WAKEUP,  finalMillis, pendingIntent);
    }
}