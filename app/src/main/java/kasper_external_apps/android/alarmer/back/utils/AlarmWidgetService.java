package kasper_external_apps.android.alarmer.back.utils;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import kasper_external_apps.android.alarmer.front.adapters.AlarmRemoteViewsFactory;

public class AlarmWidgetService extends RemoteViewsService {

    public AlarmWidgetService() {
        super();
    }

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new AlarmRemoteViewsFactory(intent);
    }
}