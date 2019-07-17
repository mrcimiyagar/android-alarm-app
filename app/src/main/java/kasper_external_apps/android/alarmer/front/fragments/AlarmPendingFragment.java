package kasper_external_apps.android.alarmer.front.fragments;

import kasper_external_apps.android.alarmer.front.fragments.base.AlarmFragment;
import kasper_external_apps.android.alarmer.front.fragments.base.AlarmPage;

public class AlarmPendingFragment extends AlarmFragment implements AlarmPage {

    @Override
    public AlarmPageType getAlarmPageType() {
        return AlarmPageType.PENDING;
    }
}