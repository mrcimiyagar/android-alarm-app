package kasper_external_apps.android.alarmer.front.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.utils.AlarmService;

public class AlarmCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_card);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.width = getResources().getDisplayMetrics().widthPixels;
        params.height = getResources().getDisplayMetrics().heightPixels;

        getWindow().setAttributes(params);

        ((TextView) this.findViewById(R.id.activity_alarm_card_title_text_view)).setText(getIntent().getExtras().getString("alarm-title"));
        ((TextView) this.findViewById(R.id.activity_alarm_card_details_text_view)).setText(getIntent().getExtras().getString("alarm-details"));

        try {
            Log.d("KasperLogger", "notif id : " + getIntent().getExtras().getInt("notif-id"));

            ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(getIntent().getExtras().getInt("notif-id"));

            if (AlarmService.mMediaPlayer != null) {
                AlarmService.mMediaPlayer.stop();
            }
        } catch (Exception ignored) {

        }
    }

    public void onOkBtnClicked(View view) {
        this.finish();
    }
}