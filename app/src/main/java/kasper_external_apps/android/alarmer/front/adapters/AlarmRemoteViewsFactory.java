package kasper_external_apps.android.alarmer.front.adapters;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.back.models.CustomDate;

public class AlarmRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private ArrayList<Pair<CustomDate, ArrayList<Alarm>>> alarmsList;

        public AlarmRemoteViewsFactory(Intent intent) {

            fetchData(intent);
        }

        private void fetchData(Intent intent) {

            ArrayList<Alarm> alarmsRawList;

            switch (intent.getIntExtra("page-index", 0)) {

                case 0: {
                    alarmsRawList = MyApp.getInstance().getDatabaseHelper().getPendingAlarmsList();
                    break;
                }
                default: {
                    alarmsRawList = MyApp.getInstance().getDatabaseHelper().getDoneAlarmsList();
                    break;
                }
            }

            this.alarmsList = new ArrayList<>();

            Collections.sort(alarmsRawList, new Comparator<Alarm>() {
                @Override
                public int compare(Alarm a1, Alarm a2) {

                    long diff = a1.getDateTimeValue() - a2.getDateTimeValue();

                    if (diff < 0) {
                        return -1;
                    }
                    else if (diff > 0) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            });

            for (Alarm alarm : alarmsRawList) {

                if (this.alarmsList.size() == 0) {
                    ArrayList<Alarm> newList = new ArrayList<>();
                    newList.add(alarm);
                    this.alarmsList.add(new Pair<>(alarm.getDate(), newList));
                }
                else {

                    CustomDate lastImportedDate = this.alarmsList.get(this.alarmsList.size() - 1).first;

                    if (alarm.hasEqualDateTo(lastImportedDate)) {
                        this.alarmsList.get(this.alarmsList.size() - 1).second.add(alarm);
                    }
                    else {
                        ArrayList<Alarm> newList = new ArrayList<>();
                        newList.add(alarm);
                        this.alarmsList.add(new Pair<>(alarm.getDate(), newList));
                    }
                }
            }
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (alarmsList.size() == 0) {
                return 0;
            }
            else {
                int sum = 0;
                for (Pair<CustomDate, ArrayList<Alarm>> pair : this.alarmsList) {
                    sum += (pair.second.size() + 1);
                }
                return sum;
            }
        }

        PersianCalendar persianCalendar;

        @Override
        public RemoteViews getViewAt(int position) {

            int tempPose = position;

            int grpCounter = 0;

            for (Pair<CustomDate, ArrayList<Alarm>> pair : this.alarmsList) {
                if (tempPose >= (pair.second.size() + 1)) {
                    tempPose -= (pair.second.size() + 1);
                    grpCounter++;
                }
                else {
                    break;
                }
            }

            CustomDate date = this.alarmsList.get(grpCounter).first;

            if (tempPose == 0) {

                persianCalendar = new PersianCalendar();
                persianCalendar.setPersianDate(date.getYear(), date.getMonth(), date.getDay());
                RemoteViews rv = new RemoteViews(MyApp.getInstance().getPackageName(), R.layout.adapter_widget_alarm_date);
                rv.setTextViewText(R.id.adapter_alarm_group_date_text_view, date.getDay() + " " + this.persianCalendar.getPersianMonthName() + " " + date.getYear());
                return rv;
            }
            else {

                RemoteViews rv = new RemoteViews(MyApp.getInstance().getPackageName(), R.layout.adapter_widget_alarm_item);
                final Alarm alarm = this.alarmsList.get(grpCounter).second.get(tempPose - 1);
                rv.setTextViewText(R.id.adapter_alarm_item_middle_title_text_view, alarm.getName());
                rv.setTextViewText(R.id.adapter_alarm_item_middle_time_text_view, alarm.getTime().getHour() + ":" + alarm.getTime().getMinute());
                rv.setTextViewText(R.id.adapter_alarm_item_middle_time_modified_text_view, alarm.getTimeFinished().length() > 0 ? "تمام شده در " + alarm.getTimeFinished() : "ایجاد شده در " + alarm.getTimeModified());

                return rv;
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }