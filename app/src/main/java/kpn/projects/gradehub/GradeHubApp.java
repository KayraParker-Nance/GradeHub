package kpn.projects.gradehub;

import android.app.Application;

import kpn.projects.gradehub.utils.SettingsManager;

public class GradeHubApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new SettingsManager(this).applyStoredDarkMode();
    }
}
