package kasper_external_apps.android.alarmer.front.fragments.base;

public interface AlarmPage {

    enum AlarmPageType { PENDING, TOMORROW, STARRED, DONE }

    AlarmPageType getAlarmPageType();

    void updateAlarmsList();
}