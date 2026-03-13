package com.example.fishinbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class BackgroundPrefs {

    private static final String PREF_NAME = "fishinbank_prefs";
    private static final String KEY_USE_ALT_BG = "use_alternative_background";
    private static final String TAG = "BackgroundPrefs";

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

    public boolean useAlternativeBackground() {
        return prefs.getBoolean(KEY_USE_ALT_BG, false);
    }

    public void setAlternativeBackground(boolean useAlt) {
        prefs.edit().putBoolean(KEY_USE_ALT_BG, useAlt).apply();
    }

    public void toggleBackground() {
        setAlternativeBackground(!useAlternativeBackground());
    }

    // Проверяем, что ресурсы существуют, перед использованием
    public static int getBackgroundResId(Context context) {
        BackgroundPrefs prefs = getInstance(context);
        int resId = prefs.useAlternativeBackground() ? R.drawable.new_fon : R.drawable.fon;

        // Проверка на случай, если ресурс не найден (защита от вылета)
        try {
            context.getResources().getResourceName(resId);
            return resId;
        } catch (Exception e) {
            Log.e(TAG, "Background resource not found! Fallback to fon.png", e);
            return R.drawable.fon; // Всегда возвращаем безопасный фон
        }
    }
}