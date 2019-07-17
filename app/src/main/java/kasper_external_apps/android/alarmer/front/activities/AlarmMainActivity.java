package kasper_external_apps.android.alarmer.front.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.callbacks.OnDialogCallbackRegistered;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.front.adapters.PageAdapter;
import kasper_external_apps.android.alarmer.front.fragments.AlarmDoneFragment;
import kasper_external_apps.android.alarmer.front.fragments.AlarmPendingFragment;
import kasper_external_apps.android.alarmer.front.fragments.AlarmStarredFragment;
import kasper_external_apps.android.alarmer.front.fragments.base.AlarmFragment;
import kasper_external_apps.android.alarmer.front.widgets.AlarmWidget;

public class AlarmMainActivity extends AppCompatActivity implements OnDialogCallbackRegistered {

    MaterialViewPager viewPager;

    private AlarmFragment[] alarmFragments;

    private int pageIndex = 0;

    Handler timeHandler;

    FloatingActionButton addFAB;

    private Runnable dialogYesBtnCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        timeHandler = new Handler();

        MyApp.getInstance().setUiUpdateCallback(new Runnable() {
            @Override
            public void run() {
                updateAllPages();
            }
        });

        if (getIntent().hasExtra("page-index")) {
            pageIndex = 3 - getIntent().getExtras().getInt("page-index");
        }

        this.initViews();
        this.initPages();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (this.viewPager.getViewPager().getCurrentItem() == 0
                && !AlarmMainActivity.this.addFAB.isShown()) {
            this.addFAB.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d("KasperLogger", "activity result in fragment");

        if (requestCode == 4) {

            if (resultCode == RESULT_OK) {

                if (data.getExtras().getString("dialog-result").equals("yes")) {

                    this.dialogYesBtnCallback.run();
                }
            }
        }
    }

    @Override
    public void registerYesBtnCallback(Runnable callback) {

        dialogYesBtnCallback = callback;
    }

    private void initViews() {
        this.viewPager = (MaterialViewPager) findViewById(R.id.activity_main_view_pager);
        this.addFAB = (FloatingActionButton) findViewById(R.id.activity_alarm_main_add_button);
    }

    private void initPages() {

        alarmFragments = new AlarmFragment[3];
        alarmFragments[0] = new AlarmPendingFragment();
        alarmFragments[1] = new AlarmDoneFragment();
        alarmFragments[2] = new AlarmStarredFragment();

        for (AlarmFragment alarmFragment : alarmFragments) {

            alarmFragment.setScrollUpCallback(new Runnable() {
                @Override
                public void run() {
                    if (AlarmMainActivity.this.viewPager.getViewPager().getCurrentItem() == 0
                            && !AlarmMainActivity.this.addFAB.isShown()) {
                        AlarmMainActivity.this.addFAB.show();
                    }
                }
            });

            alarmFragment.setScrollDownCallback(new Runnable() {
                @Override
                public void run() {
                    AlarmMainActivity.this.addFAB.hide();
                }
            });

            alarmFragment.setShowNoAlarmsCallback(new Runnable() {
                @Override
                public void run() {

                }
            });

            alarmFragment.setHideNoAlarmsCallback(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        this.viewPager.getViewPager().setAdapter(new PageAdapter(this.getSupportFragmentManager(), alarmFragments));

        this.viewPager.getViewPager().setOffscreenPageLimit(5);

        this.viewPager.getPagerTitleStrip().setViewPager(this.viewPager.getViewPager());

        this.viewPager.getViewPager().setCurrentItem(pageIndex);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        final Drawable headerDrawable1 = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.header1, options));
        final Drawable headerDrawable2 = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.header2, options));
        final Drawable headerDrawable3 = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.header3, options));

        final HeaderDesign headerDesign1 = HeaderDesign.fromColorResAndDrawable(R.color.colorHeader1, headerDrawable1);
        final HeaderDesign headerDesign2 = HeaderDesign.fromColorResAndDrawable(R.color.colorHeader2, headerDrawable2);
        final HeaderDesign headerDesign3 = HeaderDesign.fromColorResAndDrawable(R.color.colorHeader3, headerDrawable3);

        this.viewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        if (!AlarmMainActivity.this.addFAB.isShown()) {
                            AlarmMainActivity.this.addFAB.show();
                        }
                        return headerDesign1;
                    case 1:
                        if (AlarmMainActivity.this.addFAB.isShown()) {
                            AlarmMainActivity.this.addFAB.hide();
                        }
                        return headerDesign2;
                    case 2:
                        if (AlarmMainActivity.this.addFAB.isShown()) {
                            AlarmMainActivity.this.addFAB.hide();
                        }
                        return headerDesign3;
                }

                return null;
            }
        });

        Toolbar toolbar = this.viewPager.getToolbar();

        if (toolbar != null) {

            Drawable infoDrawable = getResources().getDrawable(R.drawable.info);

            DrawableCompat.setTint(infoDrawable, Color.WHITE);

            toolbar.setNavigationIcon(infoDrawable);

            toolbar.setTitle("");

            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AlarmMainActivity.this, AboutActivity.class));
                }
            });

            TextView textView = new TextView(this);
            textView.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.RIGHT);
            textView.setPadding(0, 0, (int)(16 * getResources().getDisplayMetrics().density), 0);
            textView.setTextColor(Color.WHITE);

            textView.setText(new PersianCalendar().getPersianLongDate());

            toolbar.addView(textView);
        }
    }

    public void onAddBtnClicked(View view) {

        AlarmMainActivity.this.addFAB.hide();

        startActivity(new Intent(AlarmMainActivity.this, AlarmAddActivity.class).putExtra("edit_mode", false));
    }

    public void updateAllPages() {

        for (AlarmFragment alarmFragment : alarmFragments) {
            alarmFragment.updateAlarmsList();
        }

        AppWidgetManager man = AppWidgetManager.getInstance(this);
        final int[] ids = man.getAppWidgetIds(new ComponentName(this, AlarmWidget.class));

        Intent intent = new Intent(this, AlarmWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public void hideFABs() {

        AlarmMainActivity.this.addFAB.hide();
    }
}