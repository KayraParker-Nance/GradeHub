package kpn.projects.gradehub.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public final class SettingsManager {
    private static final String PREFS_NAME = "gradehub_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_DECIMAL_PLACES = "decimal_places";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        applyDarkMode(enabled);
    }

    /** Call once at app startup so the correct mode is set before any Activity is created. */
    public void applyStoredDarkMode() {
        applyDarkMode(isDarkMode());
    }

    private void applyDarkMode(boolean enabled) {
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public int getDecimalPlaces() {
        return prefs.getInt(KEY_DECIMAL_PLACES, 1);
    }

    public void setDecimalPlaces(int places) {
        prefs.edit().putInt(KEY_DECIMAL_PLACES, Math.max(0, Math.min(4, places))).apply();
    }
}
