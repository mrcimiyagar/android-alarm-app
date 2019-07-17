package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kasper_external_apps.android.alarmer.R;

public class YesNoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_no);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.width = getResources().getDisplayMetrics().widthPixels;
        params.height = getResources().getDisplayMetrics().heightPixels;

        getWindow().setAttributes(params);

        String title = getIntent().getExtras().getString("dialog-title");

        TextView titleTV = (TextView) findViewById(R.id.activity_yes_no_title_text_view);
        titleTV.setText(title);

        String content = getIntent().getExtras().getString("dialog-content");

        TextView contentTV = (TextView) findViewById(R.id.activity_yes_no_content_text_view);
        contentTV.setText(content);
    }

    public void onCancelBtnClicked(View view) {
        setResult(RESULT_OK, new Intent().putExtra("dialog-result", "no"));
        this.finish();
    }

    public void onOkBtnClicked(View view) {
        setResult(RESULT_OK, new Intent().putExtra("dialog-result", "yes"));
        this.finish();
    }
}