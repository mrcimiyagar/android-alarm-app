package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import kasper_external_apps.android.alarmer.R;

public class AlarmRepeatActivity extends AppCompatActivity {

    CheckBox[] checksArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_repeat);

        boolean[] days = this.getIntent().getExtras().getBooleanArray("days");

        this.checksArr = new CheckBox[7];

        this.checksArr[0] = (CheckBox) findViewById(R.id.adapter_sort_check_button0);
        this.checksArr[1] = (CheckBox) findViewById(R.id.adapter_sort_check_button1);
        this.checksArr[2] = (CheckBox) findViewById(R.id.adapter_sort_check_button2);
        this.checksArr[3] = (CheckBox) findViewById(R.id.adapter_sort_check_button3);
        this.checksArr[4] = (CheckBox) findViewById(R.id.adapter_sort_check_button4);
        this.checksArr[5] = (CheckBox) findViewById(R.id.adapter_sort_check_button5);
        this.checksArr[6] = (CheckBox) findViewById(R.id.adapter_sort_check_button6);

        int counter = 0;

        for (CheckBox cb : this.checksArr) {

            cb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    boolean[] days = new boolean[7];

                    int counter = 0;

                    for (CheckBox ccb : checksArr) {
                        days[counter] = ccb.isChecked();
                        counter++;
                    }

                    setResult(RESULT_OK, new Intent().putExtra("days", days));
                }
            });

            cb.setChecked(days[counter]);

            counter++;
        }
    }
}