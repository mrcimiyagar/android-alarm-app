package kasper_external_apps.android.alarmer.front.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.callbacks.OnDialogCallbackRegistered;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.back.models.CustomDate;
import kasper_external_apps.android.alarmer.back.utils.AlarmReceiver;
import kasper_external_apps.android.alarmer.front.activities.AlarmAddActivity;
import kasper_external_apps.android.alarmer.front.activities.AlarmMainActivity;
import kasper_external_apps.android.alarmer.front.activities.YesNoActivity;

import static android.content.Context.ALARM_SERVICE;

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity context;
    private ArrayList<Pair<CustomDate, ArrayList<Alarm>>> alarmsList;
    private Runnable updateCallback;
    private OnDialogCallbackRegistered dialogCallbackRegister;
    private int currentPageIndex = 0;
    private int alarmsCount = 0;

    private int alarmsSubCount = 0;

    public AlarmAdapter(AppCompatActivity context, ArrayList<Alarm> alarmsRawList, int currentPageIndex, Runnable updateCallback
            , OnDialogCallbackRegistered yesBtnCallbackRegister) {

        this.context = context;
        this.alarmsList = new ArrayList<>();
        this.currentPageIndex = currentPageIndex;
        this.alarmsCount = alarmsRawList.size();
        this.updateCallback = updateCallback;
        this.dialogCallbackRegister = yesBtnCallbackRegister;

        PersianCalendar persianCalendar = new PersianCalendar();

        alarmsSubCount = 0;

        if (currentPageIndex == 0) {
            for (Alarm alarm : alarmsRawList) {
                if (alarm.getDate().getYear() == persianCalendar.getPersianYear()
                        && alarm.getDate().getMonth() == persianCalendar.getPersianMonth()
                        && alarm.getDate().getDay() == persianCalendar.getPersianDay()) {
                    alarmsSubCount++;
                }
            }
        }
        else if (currentPageIndex == 1) {

            PersianCalendar todayPersianCalendar = new PersianCalendar();

            for (Alarm alarm : alarmsRawList) {
                if (alarm.getTimeFinished().length() > 0) {
                    persianCalendar.parse(alarm.getTimeFinished().split(" ")[2]);
                    if (todayPersianCalendar.getPersianYear() == persianCalendar.getPersianYear()
                            && todayPersianCalendar.getPersianMonth() + 1 == persianCalendar.getPersianMonth()
                            && todayPersianCalendar.getPersianDay() == persianCalendar.getPersianDay()) {
                        alarmsSubCount++;
                    }
                }
            }
        }
        else if (currentPageIndex == 2) {

            for (Alarm alarm : alarmsRawList) {
                if (alarm.getTimeFinished().length() == 0) {
                    alarmsSubCount++;
                }
            }
        }

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

        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case 0: {
                return new PageHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_header, parent, false));
            }
            case 1: {
                return new AlarmGroupDateHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_group_date, parent, false));
            }
            case 2: {
                return new AlarmItemTopHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_item_top, parent, false));
            }
            case 3: {
                return new AlarmItemMiddleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_item_middle, parent, false));
            }
            case 4: {
                return new AlarmItemBottomHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_item_bottom, parent, false));
            }
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {

            return 0;
        }
        else {

            int tempPose = position - 1;

            int grpCounter = 0;

            for (Pair<CustomDate, ArrayList<Alarm>> pair : this.alarmsList) {
                if (tempPose >= (pair.second.size() + 1)) {
                    tempPose -= (pair.second.size() + 1);
                    grpCounter++;
                } else {
                    break;
                }
            }

            if (tempPose == 0) {
                return 1;
            } else if (tempPose == 1) {
                return 2;
            } else if (tempPose < this.alarmsList.get(grpCounter).second.size()) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position == 0) {

            if (currentPageIndex == 0) {
                ((PageHeaderHolder)holder).details1IB.setImageResource(R.drawable.pending);
                ((PageHeaderHolder)holder).details1TV.setText(alarmsCount + "  یادآوری در وضعیت انتظار");
                ((PageHeaderHolder)holder).details2IB.setImageResource(R.drawable.today);
                ((PageHeaderHolder)holder).details2TV.setText(alarmsSubCount + "  یادآوری مربوط به امروز است .");
            }
            else if (currentPageIndex == 1) {
                ((PageHeaderHolder)holder).details1IB.setImageResource(R.drawable.completed);
                ((PageHeaderHolder)holder).details1TV.setText(alarmsCount + "  کار در وضعیت انجام شده");
                ((PageHeaderHolder)holder).details2IB.setImageResource(R.drawable.today);
                ((PageHeaderHolder)holder).details2TV.setText(alarmsSubCount + "  کار امروز انجام شده است .");
            }
            else if (currentPageIndex == 2) {
                ((PageHeaderHolder)holder).details1IB.setImageResource(R.drawable.grade);
                ((PageHeaderHolder)holder).details1TV.setText(alarmsCount + "  کار علامت گذاری شده ی مهم");
                ((PageHeaderHolder)holder).details2IB.setImageResource(R.drawable.pending_star);
                ((PageHeaderHolder)holder).details2TV.setText(alarmsSubCount + "  کار مهم در حالت انتظار است .");
            }
        }
        else {

            int tempPose = position - 1;

            int grpCounter = 0;

            for (Pair<CustomDate, ArrayList<Alarm>> pair : this.alarmsList) {
                if (tempPose >= (pair.second.size() + 1)) {
                    tempPose -= (pair.second.size() + 1);
                    grpCounter++;
                } else {
                    break;
                }
            }

            CustomDate date = this.alarmsList.get(grpCounter).first;

            if (tempPose == 0) {
                persianCalendar = new PersianCalendar();
                persianCalendar.setPersianDate(date.getYear(), date.getMonth(), date.getDay());
                ((AlarmGroupDateHolder) holder).dateTV.setText(date.getDay() + " " + this.persianCalendar.getPersianMonthName() + " " + date.getYear());
            } else {
                final Alarm alarm = this.alarmsList.get(grpCounter).second.get(tempPose - 1);
                ((AlarmItemBaseHolder) holder).nameTV.setText(alarm.getName());
                ((AlarmItemBaseHolder) holder).timeTV.setText(alarm.getTime().getHour() + ":" + alarm.getTime().getMinute());
                ((AlarmItemBaseHolder) holder).timeModifiedTV.setText(alarm.getTimeFinished().length() > 0 ? "تمام شده در " + alarm.getTimeFinished() : "ایجاد شده در " + alarm.getTimeModified());
                ((AlarmItemBaseHolder) holder).doneBTN.setImageResource(alarm.getTimeFinished().length() > 0 ? R.drawable.repeat : R.drawable.done);
                ((AlarmItemBaseHolder) holder).doneBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (alarm.getTimeFinished().length() > 0) {

                            dialogCallbackRegister.registerYesBtnCallback(new Runnable() {
                                @Override
                                public void run() {

                                    MyApp.getInstance().getDatabaseHelper().editAlarm(alarm.getId(), alarm.getName()
                                            , alarm.getMessage(), alarm.getDate().getYear(), alarm.getDate().getMonth()
                                            , alarm.getDate().getDay(), alarm.getTime().getHour(), alarm.getTime().getMinute(),
                                            alarm.getStarred(), alarm.getTimeModified(), "", alarm.getSoundPath(), alarm.getDays());
                                }
                            });

                            context.startActivityForResult(new Intent(context, YesNoActivity.class).putExtra("dialog-title", "فعالسازی مجدد").putExtra("dialog-content", "آیا می خواهید این یادآور به لیست کار های باقی مانده منتقل شده  و مجددا فعال شود ؟"), 4);
                        } else {

                            dialogCallbackRegister.registerYesBtnCallback(new Runnable() {
                                @Override
                                public void run() {

                                    PersianCalendar tempCalendar = new PersianCalendar();
                                    String fullDate = tempCalendar.getPersianShortDate();
                                    String fullTime = Calendar.getInstance().getTime().getHours() + ":" + Calendar.getInstance().getTime().getMinutes();

                                    String timeFinished = fullTime + " - " + fullDate;

                                    MyApp.getInstance().getDatabaseHelper().editAlarm(alarm.getId(), alarm.getName()
                                            , alarm.getMessage(), alarm.getDate().getYear(), alarm.getDate().getMonth()
                                            , alarm.getDate().getDay(), alarm.getTime().getHour(), alarm.getTime().getMinute(),
                                            alarm.getStarred(), alarm.getTimeModified(), timeFinished, alarm.getSoundPath(), alarm.getDays());

                                    AlarmManager alarmManager = (AlarmManager) MyApp.getInstance().getSystemService(ALARM_SERVICE);
                                    Intent myIntent = new Intent(MyApp.getInstance(), AlarmReceiver.class).putExtra("alarm-id", alarm.getId());
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApp.getInstance(), alarm.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarmManager.cancel(pendingIntent);
                                }
                            });

                            context.startActivityForResult(new Intent(context, YesNoActivity.class).putExtra("dialog-title", "انجام شده").putExtra("dialog-content", "آیا می خواهید این یادآور به لیست انجام شده ها منتقل شود ؟"), 4);
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AlarmAddActivity.class);
                        intent.putExtra("edit_mode", true);
                        intent.putExtra("alarm_id", alarm.getId());
                        if (context instanceof AlarmMainActivity) {
                            ((AlarmMainActivity) context).hideFABs();
                        }
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    private PersianCalendar persianCalendar;

    @Override
    public int getItemCount() {
        if (alarmsList.size() == 0) {
            return 1;
        }
        else {
            int sum = 0;
            for (Pair<CustomDate, ArrayList<Alarm>> pair : this.alarmsList) {
                sum += (pair.second.size() + 1);
            }
            return sum + 1;
        }
    }

    private class PageHeaderHolder extends RecyclerView.ViewHolder {

        private ImageButton details1IB;
        private ImageButton details2IB;
        private TextView details1TV;
        private TextView details2TV;

        PageHeaderHolder(View itemView) {
            super(itemView);
            this.details1IB = (ImageButton) itemView.findViewById(R.id.adapter_alarm_header_alarms_details1_image_button);
            this.details2IB = (ImageButton) itemView.findViewById(R.id.adapter_alarm_header_alarms_details2_image_button);
            this.details1TV = (TextView) itemView.findViewById(R.id.adapter_alarm_header_alarms_details1_text_view);
            this.details2TV = (TextView) itemView.findViewById(R.id.adapter_alarm_header_alarms_details2_text_view);
        }
    }

    private class AlarmItemBaseHolder extends RecyclerView.ViewHolder {

        private TextView nameTV;
        private TextView timeTV;
        private TextView timeModifiedTV;
        private AppCompatImageButton doneBTN;

        AlarmItemBaseHolder(View itemView, TextView nameTV, TextView timeTV
                , TextView timeModifiedTV, AppCompatImageButton doneBTN) {
            super(itemView);
            this.nameTV = nameTV;
            this.timeTV = timeTV;
            this.timeModifiedTV = timeModifiedTV;
            this.doneBTN = doneBTN;
        }
    }

    private class AlarmItemTopHolder extends AlarmItemBaseHolder {

        AlarmItemTopHolder(View itemView) {
            super(itemView, (TextView) itemView.findViewById(R.id.adapter_alarm_item_top_title_text_view),
                    (TextView) itemView.findViewById(R.id.adapter_alarm_item_top_time_text_view),
                    (TextView) itemView.findViewById(R.id.adapter_alarm_item_top_time_modified_text_view),
                    (AppCompatImageButton) itemView.findViewById(R.id.adapter_alarm_item_top_done_image_button));
        }
    }

    private class AlarmItemMiddleHolder extends AlarmItemBaseHolder {

        AlarmItemMiddleHolder(View itemView) {
            super(itemView, (TextView) itemView.findViewById(R.id.adapter_alarm_item_middle_title_text_view),
                    (TextView) itemView.findViewById(R.id.adapter_alarm_item_middle_time_text_view),
                    (TextView) itemView.findViewById(R.id.adapter_alarm_item_middle_time_modified_text_view),
                    (AppCompatImageButton) itemView.findViewById(R.id.adapter_alarm_item_middle_done_image_button));
        }
    }

    private class AlarmItemBottomHolder extends AlarmItemBaseHolder {

        AlarmItemBottomHolder(View itemView) {
            super(itemView, (TextView) itemView.findViewById(R.id.adapter_alarm_item_bottom_title_text_view),
                    (TextView) itemView.findViewById(R.id.adapter_alarm_item_bottom_time_text_view),
                    (TextView) itemView.findViewById(R.id.adapter_alarm_item_bottom_time_modified_text_view),
                    (AppCompatImageButton) itemView.findViewById(R.id.adapter_alarm_item_bottom_done_image_button));
        }
    }

    private class AlarmGroupDateHolder extends RecyclerView.ViewHolder {

        private TextView dateTV;

        AlarmGroupDateHolder(View itemView) {
            super(itemView);
            this.dateTV = itemView.findViewById(R.id.adapter_alarm_group_date_text_view);
        }
    }
}