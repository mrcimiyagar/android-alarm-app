package kasper_external_apps.android.alarmer.front.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kasper_external_apps.android.alarmer.R;
import kasper_external_apps.android.alarmer.back.callbacks.OnDialogCallbackRegistered;
import kasper_external_apps.android.alarmer.back.core.MyApp;
import kasper_external_apps.android.alarmer.back.models.Alarm;
import kasper_external_apps.android.alarmer.front.adapters.AlarmAdapter;
import kasper_external_apps.android.alarmer.front.extras.CustomDecoration;

public abstract class AlarmFragment extends Fragment implements AlarmPage {

    private RecyclerView alarmsRV;
    private CustomDecoration itemDecoration;


    public AlarmFragment() {

    }

    private Runnable scrollUpCallback;
    private Runnable scrollDownCallback;
    private Runnable showNoAlarmsCallback;
    private Runnable hideNoAlarmsCallback;

    public Runnable getScrollUpCallback() {
        return scrollUpCallback;
    }

    public void setScrollUpCallback(Runnable scrollUpCallback) {
        this.scrollUpCallback = scrollUpCallback;
    }

    public Runnable getScrollDownCallback() {
        return scrollDownCallback;
    }

    public void setScrollDownCallback(Runnable scrollDownCallback) {
        this.scrollDownCallback = scrollDownCallback;
    }

    public void setShowNoAlarmsCallback(Runnable showNoAlarmsCallback) {
        this.showNoAlarmsCallback = showNoAlarmsCallback;
    }

    public void setHideNoAlarmsCallback(Runnable hideNoAlarmsCallback) {
        this.hideNoAlarmsCallback = hideNoAlarmsCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("KasperLogger", "fragment created !");

        View contentView = inflater.inflate(R.layout.fragment_alarm, container, false);

        this.initViews(contentView);
        this.initList();

        return contentView;
    }

    private void initViews(View contentView) {
        this.alarmsRV = contentView.findViewById(R.id.fragment_alarm_future_recycler_view);
    }

    private void initList() {
        this.alarmsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        if (itemDecoration != null) {
            this.alarmsRV.removeItemDecoration(itemDecoration);
        }
        this.itemDecoration = new CustomDecoration();
        this.alarmsRV.addItemDecoration(itemDecoration);

        int currentPageIndex = this.getAlarmPageType() == AlarmPageType.PENDING ? 0 : (this.getAlarmPageType() == AlarmPageType.DONE ? 1 : 2);

        this.alarmsRV.setAdapter(new AlarmAdapter((AppCompatActivity) getActivity(), this.getAlarmPageType() ==
                AlarmPageType.DONE ? MyApp.getInstance().getDatabaseHelper().getDoneAlarmsList()
                : this.getAlarmPageType() == AlarmPageType.STARRED ? MyApp.getInstance().
                getDatabaseHelper().getStarredAlarmsList() : MyApp.getInstance().getDatabaseHelper()
                .getPendingAlarmsList(), currentPageIndex, new Runnable() {
            @Override
            public void run() {
                AlarmFragment.this.updateList();
            }
        }, new OnDialogCallbackRegistered() {
            @Override
            public void registerYesBtnCallback(Runnable callback) {

                ((OnDialogCallbackRegistered)getActivity()).registerYesBtnCallback(callback);
            }
        }));

        this.alarmsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (getScrollDownCallback() != null) {
                        getScrollDownCallback().run();
                    }
                }
                else {
                    if (getScrollUpCallback() != null) {
                        getScrollUpCallback().run();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();

        if (alarmsRV != null) {
            this.updateList();
        }
    }

    @Override
    public void updateAlarmsList() {

        this.updateList();
    }

    private void updateList() {

        if (this.alarmsRV != null) {

            ArrayList<Alarm> alarms;

            if (this.getAlarmPageType() == AlarmPageType.PENDING){
                alarms = MyApp.getInstance().getDatabaseHelper().getPendingAlarmsList();
            }
            else if (this.getAlarmPageType() == AlarmPageType.STARRED) {
                alarms = MyApp.getInstance().getDatabaseHelper().getStarredAlarmsList();
            }
            else {
                alarms = MyApp.getInstance().getDatabaseHelper().getDoneAlarmsList();
            }

            if (alarms.size() == 0) {
                this.showNoAlarmsCallback.run();
            } else {
                this.hideNoAlarmsCallback.run();
            }

            int currentPageIndex = this.getAlarmPageType() == AlarmPageType.PENDING ? 0 : (this.getAlarmPageType() == AlarmPageType.DONE ? 1 : 2);

            this.alarmsRV.setAdapter(new AlarmAdapter((AppCompatActivity) getActivity(), alarms, currentPageIndex, new Runnable() {
                @Override
                public void run() {

                    AlarmFragment.this.updateList();
                }
            }, new OnDialogCallbackRegistered() {
                @Override
                public void registerYesBtnCallback(Runnable callback) {

                    ((OnDialogCallbackRegistered)getActivity()).registerYesBtnCallback(callback);
                }
            }));
        }
    }
}