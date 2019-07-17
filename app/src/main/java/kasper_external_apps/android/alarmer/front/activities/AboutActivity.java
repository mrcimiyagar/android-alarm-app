package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kasper_external_apps.android.alarmer.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.width = getResources().getDisplayMetrics().widthPixels;
        params.height = getResources().getDisplayMetrics().heightPixels;

        getWindow().setAttributes(params);

        TextView keyhanEmailTV = (TextView) findViewById(R.id.activity_about_keyhan_email_text_view);
        TextView aryanEmailTV = (TextView) findViewById(R.id.activity_about_aryan_email_text_view);

        final String keyhanEmailAddress = "mohammadi_keyhan@outlook.com";
        final String aryanEmailAddress = "Ariyanz1997@gmail.com";

        keyhanEmailTV.setText(Html.fromHtml("آدرس ایمیل :" + "<br>" + "<font color=\"#0082ce\"><u>" + keyhanEmailAddress + "</u></font>"));
        aryanEmailTV.setText(Html.fromHtml("آدرس ایمیل :" + "<br>" + "<font color=\"#0082ce\"><u>" + aryanEmailAddress + "</u></font>"));

        keyhanEmailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailsApp(keyhanEmailAddress);
            }
        });

        aryanEmailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailsApp(aryanEmailAddress);
            }
        });
    }

    private void openEmailsApp(String emailAddress) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
        intent.putExtra(Intent.EXTRA_SUBJECT, "ارسال بازخورد در اپ Time");
        startActivity(Intent.createChooser(intent, "ارسال بازخورد"));

        /*Intent gmail = new Intent(Intent.ACTION_VIEW);
        gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
        gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
        gmail.setData(Uri.parse(emailAddress));
        gmail.setType("plain/text");
        startActivity(gmail);*/
    }

    public void onBackBtnClicked(View view) {
        this.onBackPressed();
    }
}