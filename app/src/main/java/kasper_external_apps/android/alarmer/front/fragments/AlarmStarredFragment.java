package kasper_external_apps.android.alarmer.front.fragments;

import kasper_external_apps.android.alarmer.front.fragments.base.AlarmFragment;

public class AlarmStarredFragment extends AlarmFragment {

    @Override
    public AlarmPageType getAlarmPageType() {
        return AlarmPageType.STARRED;
    }
}