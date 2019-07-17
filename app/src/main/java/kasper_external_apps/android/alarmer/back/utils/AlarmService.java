package kasper_external_apps.android.alarmer.back.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.util.UUID;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.front.activities.AlarmCardActivity;
import kasper_external_apps.android.alarmer.front.activities.AlarmMainActivity;

public class AlarmService extends IntentService {

    public static MediaPlayer mMediaPlayer;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        Alarm alarm = MyApp.getInstance().getDatabaseHelper().getAlarmById(intent.getExtras().getInt("alarm-id"));
        sendNotification(alarm);
    }

    private void sendNotification(Alarm alarm) {

        NotificationManager alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AlarmCardActivity.class).putExtra("notif-id", alarm.getId())
                .putExtra("alarm-title", alarm.getName()).putExtra("alarm-details", alarm.getMessage());
        PendingIntent contentIntent = PendingIntent.getActivity(this, UUID.randomUUID().hashCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle(alarm.getName()).setSmallIcon(R.drawable.time)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(alarm.getMessage()))
                .setContentText(alarm.getMessage()).setContentIntent(contentIntent)
                .setAutoCancel(true).setVibrate(new long[] { 1000, 1000, 1000 });

        Notification notification = alarmNotificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        try {

            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            }

            mMediaPlayer = new MediaPlayer();
            if (alarm.getSoundPath().length() == 0) {
                Log.d("KasperLogger", "sound 1");
                mMediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            }
            else {
                Log.d("KasperLogger", "sound 2");
                mMediaPlayer.setDataSource(this, Uri.fromFile(new File(alarm.getSoundPath())));
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
        catch (Exception ignored) {
            Log.d("KasperLogger", ignored.toString());
        }

        alarmNotificationManager.notify(alarm.getId(), notification);
    }
}