package kasper_external_apps.android.alarmer.front.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;
import java.util.Random;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.back.utils.AlarmWidgetService;
import kasper_external_apps.android.alarmer.front.activities.AlarmMainActivity;

public class AlarmWidget extends AppWidgetProvider {

    public static final String ACTION_COMPLETE_ALARM = "action_complete_alarm";

    public static String ALARM_lEFT_BTN_ACTION = "kasper_external_apps.android.alarmer.AlarmLeftBtnClick";
    public static String ALARM_RIGHT_BTN_ACTION = "kasper_external_apps.android.alarmer.AlarmRightBtnClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        updateDataSet(context);
    }

    PersianCalendar persianCalendar;

    private void updateDataSet(Context context) {

        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = man.getAppWidgetIds(new ComponentName(MyApp.getInstance().getPackageName(), AlarmWidget.class.getName()));

        SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("MyAppPrefs", 0);

        for (int appWidgetId : appWidgetIds) {

            int pageIndex = preferences.getInt(appWidgetId + "-page-index", 0);

            Intent intent = new Intent(context, AlarmWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("page-index", pageIndex);
            intent.putExtra("random", new Random().nextFloat() * 1000);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_alarm);
            rv.setRemoteAdapter(R.id.widget_alarm_list_view, intent);

            switch (pageIndex) {

                case 0: {
                    rv.setTextViewText(R.id.widget_alarm_title_text_view, "کار های باقی مانده");
                    break;
                }
                default: {
                    rv.setTextViewText(R.id.widget_alarm_title_text_view, "کار های تمام شده");
                    break;
                }
            }

            persianCalendar = new PersianCalendar();

            rv.setTextViewText(R.id.widget_alarm_date_text_view, persianCalendar.getPersianLongDate());

            long todayTimeMillis = persianCalendar.getTimeInMillis();

            long leftTime = (todayTimeMillis + 86400000) - System.currentTimeMillis();

            new Handler().postAtTime(new Runnable() {
                @Override
                public void run() {

                    persianCalendar = new PersianCalendar();

                    rv.setTextViewText(R.id.widget_alarm_date_text_view, persianCalendar.getPersianLongDate());
                }
            }, SystemClock.uptimeMillis() + leftTime);

            Log.d("KasperLogger", "hello alarm widget !");

            Intent leftBtnIntent = new Intent(ALARM_lEFT_BTN_ACTION);
            leftBtnIntent.putExtra("app-widget-id", appWidgetId);
            PendingIntent leftBtnPendingIntent = PendingIntent.getBroadcast(context, 0, leftBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.widget_alarm_left_button, leftBtnPendingIntent);

            Intent rightBtnIntent = new Intent(ALARM_RIGHT_BTN_ACTION);
            rightBtnIntent.putExtra("app-widget-id", appWidgetId);
            PendingIntent rightBtnPendingIntent = PendingIntent.getBroadcast(context, 0, rightBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.widget_alarm_right_button, rightBtnPendingIntent);

            Intent listClickIntent = new Intent(context, AlarmMainActivity.class);
            listClickIntent.putExtra("page-index", pageIndex);
            PendingIntent listPendingIntent = PendingIntent.getActivity(context, 0, listClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.widget_alarm_title_text_view, listPendingIntent);

            man.updateAppWidget(appWidgetId, rv);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        String action = intent.getAction();

        if (action.equals(ALARM_lEFT_BTN_ACTION)) {

            SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            int pageIndex = preferences.getInt(intent.getIntExtra("app-widget-id", 0) + "-page-index", 0);

            Log.d("KasperLogger", pageIndex + "");

            pageIndex--;

            if (pageIndex < 0) {
                pageIndex = 1;
            }

            preferences.edit().putInt(intent.getIntExtra("app-widget-id", 0) + "-page-index", pageIndex).apply();

            updateDataSet(context);
        }
        else if (action.equals(ALARM_RIGHT_BTN_ACTION)) {

            SharedPreferences preferences = MyApp.getInstance().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            int pageIndex = preferences.getInt(intent.getIntExtra("app-widget-id", 0) + "-page-index", 0);

            Log.d("KasperLogger", pageIndex + "");

            pageIndex++;

            if (pageIndex > 1) {
                pageIndex = 0;
            }

            preferences.edit().putInt(intent.getIntExtra("app-widget-id", 0) + "-page-index", pageIndex).apply();

            updateDataSet(context);
        }
    }
}