package kasper_external_apps.android.alarmer.front.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kasper_external_apps.android.alarmer.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onBackBtnClicked(View view) {
        this.onBackPressed();
    }
}