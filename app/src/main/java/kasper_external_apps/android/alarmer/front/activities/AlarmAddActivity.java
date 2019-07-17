package kasper_external_apps.android.alarmer.front.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.callbacks.OnPermissionGrantListener;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.back.models.CustomDate;
import kasper_external_apps.android.alarmer.back.models.CustomTime;
import kasper_external_apps.android.alarmer.back.utils.AlarmReceiver;

public class AlarmAddActivity extends AppCompatActivity {

    private boolean editMode;
    private int alarmId;

    EditText alarmTitleET;
    EditText alarmDetailsET;

    FloatingActionButton deleteBtn;

    TextView timeBtn;
    TextView dateBtn;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private boolean starred;
    private String soundPath;

    private boolean[] days = new boolean[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_add);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.width = getResources().getDisplayMetrics().widthPixels;
        params.height = getResources().getDisplayMetrics().heightPixels;

        getWindow().setAttributes(params);

        this.editMode = getIntent().getExtras().getBoolean("edit_mode");

        this.initViews();

        if (editMode) {

            ((TextView) this.findViewById(R.id.activity_alarm_add_title_Text_view)).setText("ویرایش یادآور");

            this.deleteBtn.setVisibility(View.VISIBLE);

            this.alarmId = getIntent().getExtras().getInt("alarm_id");

            Alarm alarm = MyApp.getInstance().getDatabaseHelper().getAlarmById(alarmId);

            alarmTitleET.setText(alarm.getName());
            alarmDetailsET.setText(alarm.getMessage());

            CustomTime time = alarm.getTime();

            AlarmAddActivity.this.hour = time.getHour();
            AlarmAddActivity.this.minute = time.getMinute();

            CustomDate date = alarm.getDate();

            AlarmAddActivity.this.year = date.getYear();
            AlarmAddActivity.this.month = date.getMonth();
            AlarmAddActivity.this.day = date.getDay();

            AlarmAddActivity.this.soundPath = alarm.getSoundPath();

            try {
                ((TextView) findViewById(R.id.activity_alarm_add_sound_name_text_view))
                        .setText(new File(soundPath).getName());
            } catch (Exception ignored) {

            }

            ImageButton repeatFAB = ((ImageButton) findViewById(R.id.activity_alarm_add_repeat_button));

            if (alarm.isRepeat()) {
                this.days = alarm.getDays();
                DrawableCompat.setTint(repeatFAB.getDrawable(), Color.parseColor("#ffffff"));
                DrawableCompat.setTint(repeatFAB.getBackground(), getResources().getColor(R.color.colorAccent));
            }
            else {
                DrawableCompat.setTint(repeatFAB.getDrawable(), getResources().getColor(R.color.colorHint));
                DrawableCompat.setTint(repeatFAB.getBackground(), Color.parseColor("#FFEEEEEE"));
            }

            AlarmAddActivity.this.starred = alarm.getStarred();

            ImageButton starFAB = ((ImageButton) findViewById(R.id.activity_alarm_add_star_button));

            if (starred) {
                DrawableCompat.setTint(starFAB.getDrawable(), Color.parseColor("#ffffff"));
                DrawableCompat.setTint(starFAB.getBackground(), getResources().getColor(R.color.colorAccent));
            }
            else {
                DrawableCompat.setTint(starFAB.getDrawable(), getResources().getColor(R.color.colorHint));
                DrawableCompat.setTint(starFAB.getBackground(), Color.parseColor("#FFEEEEEE"));
            }

            timeBtn.setText((AlarmAddActivity.this.hour < 10 ? "0" + AlarmAddActivity.this.hour :
                    AlarmAddActivity.this.hour) + ":" + AlarmAddActivity.this.minute);

            PersianCalendar tempC = new PersianCalendar();
            tempC.setPersianDate(year, AlarmAddActivity.this.month, AlarmAddActivity.this.day);
            dateBtn.setText(AlarmAddActivity.this.year + " " + tempC.getPersianMonthName() + " "
                    + AlarmAddActivity.this.day);
        }
        else {

            ((TextView) this.findViewById(R.id.activity_alarm_add_title_Text_view)).setText("یادآور جدید");

            this.deleteBtn.setVisibility(View.GONE);

            Date time = Calendar.getInstance().getTime();

            AlarmAddActivity.this.hour = time.getHours();
            AlarmAddActivity.this.minute = time.getMinutes();

            final PersianCalendar persianCalendar = new PersianCalendar();

            AlarmAddActivity.this.year = persianCalendar.getPersianYear();
            AlarmAddActivity.this.month = persianCalendar.getPersianMonth();
            AlarmAddActivity.this.day = persianCalendar.getPersianDay();

            AlarmAddActivity.this.starred = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {

            if (resultCode == RESULT_OK) {

                this.soundPath = getPath(this, data.getData());

                if (soundPath != null) {
                    File soundFile = new File(soundPath);
                    String fileName = soundFile.getName();
                    ((TextView) findViewById(R.id.activity_alarm_add_sound_name_text_view))
                            .setText(fileName);
                }
            }
        }
        else if (requestCode == 3) {

            if (resultCode == RESULT_OK) {

                days = data.getBooleanArrayExtra("days");

                ImageButton repeatFAB = ((ImageButton) findViewById(R.id.activity_alarm_add_repeat_button));

                if (isRepeatActive()) {
                    DrawableCompat.setTint(repeatFAB.getDrawable(), Color.parseColor("#ffffff"));
                    DrawableCompat.setTint(repeatFAB.getBackground(), getResources().getColor(R.color.colorAccent));
                }
                else {
                    DrawableCompat.setTint(repeatFAB.getDrawable(), getResources().getColor(R.color.colorHint));
                    DrawableCompat.setTint(repeatFAB.getBackground(), Color.parseColor("#FFEEEEEE"));
                }
            }
        }
        else if (requestCode == 4) {

            if (resultCode == RESULT_OK) {

                if (data.getExtras().getString("dialog-result").equals("yes")) {

                    MyApp.getInstance().getDatabaseHelper().deleteAlarm(this.alarmId);
                    this.removeAlarm(alarmId);

                    Toast.makeText(this, "یادآور حذف شد", Toast.LENGTH_SHORT).show();

                    AlarmAddActivity.this.finish();
                }
            }
        }
    }

    private void initViews() {
        this.alarmTitleET = (EditText) findViewById(R.id.activity_alarm_add_alarm_title_edit_text);
        this.alarmDetailsET = (EditText) findViewById(R.id.activity_alarm_add_alarm_details_edit_text);
        this.timeBtn = (TextView) findViewById(R.id.activity_alarm_add_time_btn);
        this.dateBtn = (TextView) findViewById(R.id.activity_alarm_add_date_btn);
        this.deleteBtn = (FloatingActionButton) findViewById(R.id.activity_alarm_add_delete_button);
    }

    private OnPermissionGrantListener permissionGrantListener;

    public void askPermission(OnPermissionGrantListener callback, String[] permissions) {

        this.permissionGrantListener = callback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = PackageManager.PERMISSION_GRANTED;
            for (String permission : permissions) {
                permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(MyApp.getInstance(), permission);
            }

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions( permissions, 1);
            }
            else {
                this.permissionGrantListener.onPermissionGranted();
            }
        }
        else {
            this.permissionGrantListener.onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;

        for(int permission : grantResults) permissionCheck = permissionCheck + permission;

        if (((grantResults.length > 0) && PackageManager.PERMISSION_GRANTED == permissionCheck)) {
            this.permissionGrantListener.onPermissionGranted();
        }
        else {
            this.permissionGrantListener.onPermissionFailure();
        }
    }

    public void onSaveBtnClicked(View view) {

        this.askPermission(new OnPermissionGrantListener() {
            @Override
            public void onPermissionGranted() {

                String alarmTitle = AlarmAddActivity.this.alarmTitleET.getText().toString();
                String alarmDetails = AlarmAddActivity.this.alarmDetailsET.getText().toString();

                if (alarmTitle.length() > 0) {

                    if (AlarmAddActivity.this.editMode) {

                        Alarm alarm = MyApp.getInstance().getDatabaseHelper().getAlarmById(alarmId);

                        MyApp.getInstance().getDatabaseHelper().editAlarm(AlarmAddActivity.this.alarmId, alarmTitle, alarmDetails,
                                AlarmAddActivity.this.year, AlarmAddActivity.this.month, AlarmAddActivity.this.day
                                , AlarmAddActivity.this.hour, AlarmAddActivity.this.minute, AlarmAddActivity.this.starred
                                , alarm.getTimeModified(), alarm.getTimeFinished(), soundPath == null ? "" : soundPath, days);

                        if (isRepeatActive()) {

                            PersianCalendar pc = AlarmAddActivity.this.getRepeatPersianCalendar();

                            AlarmAddActivity.this.editAlarm(alarmId, pc.getPersianYear(), pc.getPersianMonth(), pc.getPersianDay()
                                    , AlarmAddActivity.this.hour, AlarmAddActivity.this.minute);
                        }
                        else {
                            AlarmAddActivity.this.editAlarm(alarmId, AlarmAddActivity.this.year, AlarmAddActivity.this.month
                                    , AlarmAddActivity.this.day, AlarmAddActivity.this.hour, AlarmAddActivity.this.minute);
                        }

                        Toast.makeText(AlarmAddActivity.this, "یادآور ویرایش شد", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        PersianCalendar tempCalendar = new PersianCalendar();
                        String fullDate = tempCalendar.getPersianShortDate();
                        String fullTime = Calendar.getInstance().getTime().getHours() + ":" + Calendar.getInstance().getTime().getMinutes();

                        String timeModified = fullTime + " - " + fullDate;

                        int alarmId = MyApp.getInstance().getDatabaseHelper().addAlarm(alarmTitle, alarmDetails, AlarmAddActivity.this.year
                                , AlarmAddActivity.this.month, AlarmAddActivity.this.day, AlarmAddActivity.this.hour, AlarmAddActivity.this.minute
                                , AlarmAddActivity.this.starred, timeModified, "", soundPath == null ? "" : soundPath, days);

                        if (isRepeatActive()) {

                            PersianCalendar pc = AlarmAddActivity.this.getRepeatPersianCalendar();

                            AlarmAddActivity.this.createAlarm(alarmId, pc.getPersianYear(), pc.getPersianMonth(), pc.getPersianDay(), hour, minute);
                        }
                        else {
                            AlarmAddActivity.this.createAlarm(alarmId, AlarmAddActivity.this.year, AlarmAddActivity.this.month
                                    , AlarmAddActivity.this.day, AlarmAddActivity.this.hour, AlarmAddActivity.this.minute);
                        }

                        Toast.makeText(AlarmAddActivity.this, "یادآور جدید ثبت شد .", Toast.LENGTH_SHORT).show();
                    }

                    AlarmAddActivity.this.finish();
                }
                else {
                    Toast.makeText(AlarmAddActivity.this, "نام یادآور وارد نشده است", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionFailure() {

                Toast.makeText(AlarmAddActivity.this, "دسترسی های لازم داده نشد .", Toast.LENGTH_SHORT).show();
            }
        }, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE });
    }

    private PersianCalendar getRepeatPersianCalendar() {

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

    public void onDeleteBtnClicked(View view) {

        startActivityForResult(new Intent(this, YesNoActivity.class).putExtra("dialog-title", "حذف یادآور")
                .putExtra("dialog-content", "آیا می خواهید این یادآور را حذف کنید ؟"), 4);
    }

    public void onTimeBtnClicked(View view) {

        Date time = Calendar.getInstance().getTime();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

                        AlarmAddActivity.this.hour = hourOfDay;
                        AlarmAddActivity.this.minute = minute;

                        timeBtn.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + minute);
                    }
                }, time.getHours(), time.getMinutes(), false);

        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
    }

    public void onDateBtnClicked(View view) {

        final PersianCalendar persianCalendar = new PersianCalendar();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        AlarmAddActivity.this.year = year;
                        AlarmAddActivity.this.month = monthOfYear;
                        AlarmAddActivity.this.day = dayOfMonth;

                        PersianCalendar tempC = new PersianCalendar();
                        tempC.setPersianDate(year, monthOfYear, dayOfMonth);

                        dateBtn.setText(year + " " + tempC.getPersianMonthName() + " " + dayOfMonth);
                    }
                }, persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
    }

    public void onStarBtnClicked(View view) {

        this.starred = !this.starred;

        ImageButton starFAB = ((ImageButton) findViewById(R.id.activity_alarm_add_star_button));

        if (starred) {
            DrawableCompat.setTint(starFAB.getDrawable(), Color.parseColor("#ffffff"));
            DrawableCompat.setTint(starFAB.getBackground(), getResources().getColor(R.color.colorAccent));
        }
        else {
            DrawableCompat.setTint(starFAB.getDrawable(), getResources().getColor(R.color.colorHint));
            DrawableCompat.setTint(starFAB.getBackground(), Color.parseColor("#FFEEEEEE"));
        }
    }

    public void onSoundBtnClicked(View view) {

        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 2);
    }

    public void onRepeatBtnClicked(View view) {

        startActivityForResult(new Intent(this, AlarmRepeatActivity.class).putExtra("days", days), 3);
    }

    public void onActionBtnClicked(View view) {
        startActivity(new Intent(this, ComminSoonActivity.class));
    }

    private void createAlarm(int alarmId, int year, int month, int day, int hour, int minute) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

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

    private void editAlarm(int alarmId, int year, int month, int day, int hour, int minute) {
        this.removeAlarm(alarmId);
        this.createAlarm(alarmId, year, month, day, hour, minute);
    }

    private void removeAlarm(int alarmId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(MyApp.getInstance(), AlarmReceiver.class).putExtra("alarm-id", alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApp.getInstance(), alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isRepeatActive() {
        boolean repeat = false;
        for (boolean b : days) {
            if (b) {
                repeat = true;
                break;
            }
        }
        return repeat;
    }
}