package com.example.fishinbank;

import android.content.Context;
import android.content.SharedPreferences;

public class BackgroundPrefs {

    private static final String PREF_NAME = "fishinbank_prefs";
    private static final String KEY_BG_COLOR = "use_color_background";

    private static BackgroundPrefs instance;
    private final SharedPreferences prefs;

    public static synchronized BackgroundPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new BackgroundPrefs(context.getApplicationContext());
        }
        return instance;
    }

    private BackgroundPrefs(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isColorBackground() {
        return prefs.getBoolean(KEY_BG_COLOR, false);
    }

    public void setColorBackground(boolean useColor) {
        prefs.edit().putBoolean(KEY_BG_COLOR, useColor).apply();
    }

    public void toggleBackground() {
        setColorBackground(!isColorBackground());
    }

    public static final int COLOR_VALUE = 0xFF2103FF;
    public static final int DRAWABLE_VALUE = R.drawable.fon;
}