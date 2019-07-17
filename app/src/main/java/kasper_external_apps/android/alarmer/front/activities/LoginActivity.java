package kasper_external_apps.android.alarmer.front.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.core.MyApp;

public class LoginActivity extends AppCompatActivity {

    private String password;

    private LinearLayout containerLayout;
    private TextInputEditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.password = MyApp.getInstance().getDatabaseHelper().getPassword();

        if (password.length() > 0) {

            this.containerLayout = (LinearLayout) findViewById(R.id.activity_login_container_layout);
            this.passwordET = (TextInputEditText) findViewById(R.id.activity_login_password_edit_text);

            this.containerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.this.containerLayout.animate().alpha(1).setDuration(550).start();
                }
            }, 350);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoginActivity.this, AlarmMainActivity.class));
                    LoginActivity.this.finish();
                }
            }, 350);
        }
    }

    public void onLoginBtnClicked(View view) {

        String inputPass = this.passwordET.getText().toString();

        if (inputPass.equals(this.password)) {
            startActivity(new Intent(LoginActivity.this, AlarmMainActivity.class));
            LoginActivity.this.finish();
        }
        else {
            Toast.makeText(this, "رمز عبور اشتباه", Toast.LENGTH_SHORT).show();
        }
    }
}