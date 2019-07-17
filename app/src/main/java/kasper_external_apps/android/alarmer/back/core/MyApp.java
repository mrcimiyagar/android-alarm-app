package kasper_external_apps.android.alarmer.back.core;

import android.app.Application;

import kasper_external_apps.android.alarmer.back.helpers.DatabaseHelper;

public class MyApp extends Application {

    private static MyApp instance;
    public static MyApp getInstance() {
        return instance;
    }

    private DatabaseHelper databaseHelper;
    private Runnable uiUpdateCallback;

    public DatabaseHelper getDatabaseHelper() {
        return this.databaseHelper;
    }

    public void setUiUpdateCallback(Runnable callback) {
        this.uiUpdateCallback = callback;
    }
    public Runnable getUiUpdateCallback() {
        return this.uiUpdateCallback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.databaseHelper = new DatabaseHelper(this);
    }
}