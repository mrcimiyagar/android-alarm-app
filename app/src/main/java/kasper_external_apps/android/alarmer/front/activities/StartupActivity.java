package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kasper_external_apps.android.alarmer.R;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {SharedPreferences sp = getSharedPreferences("kasper-android-time", MODE_PRIVATE);

                if (sp.getBoolean("first-time", true)) {
                    sp.edit().putBoolean("first-time", false).apply();
                    startActivity(new Intent(StartupActivity.this, IntroActivity.class));
                    StartupActivity.this.finish();
                }
                else {
                    startActivity(new Intent(StartupActivity.this, AlarmMainActivity.class));
                    StartupActivity.this.finish();
                }
            }
        }, 1000);
    }
}