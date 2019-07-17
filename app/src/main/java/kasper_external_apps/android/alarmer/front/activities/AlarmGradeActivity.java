package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;

public class AlarmGradeActivity extends AppCompatActivity {

    RadioButton[] optionsArr;
    int checkedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_grade);

        this.checkedIndex = 2 - this.getIntent().getExtras().getInt("alarm-prio");

        this.optionsArr = new RadioButton[3];

        this.optionsArr[0] = (RadioButton) findViewById(R.id.adapter_sort_radio_button0);
        this.optionsArr[1] = (RadioButton) findViewById(R.id.adapter_sort_radio_button1);
        this.optionsArr[2] = (RadioButton) findViewById(R.id.adapter_sort_radio_button2);

        this.optionsArr[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchRadioButton(checkedIndex, false);
                checkedIndex = 0;
                switchRadioButton(checkedIndex, true);
            }
        });

        this.optionsArr[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchRadioButton(checkedIndex, false);
                checkedIndex = 1;
                switchRadioButton(checkedIndex, true);
            }
        });

        this.optionsArr[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchRadioButton(checkedIndex, false);
                checkedIndex = 2;
                switchRadioButton(checkedIndex, true);
            }
        });

        switchRadioButton(this.checkedIndex, true);
    }

    private void switchRadioButton(int index, boolean check) {

        this.optionsArr[index].setChecked(check);

        if (check) {
            setResult(RESULT_OK, new Intent().putExtra("alarm-prio", 2 - checkedIndex));
        }
    }
}