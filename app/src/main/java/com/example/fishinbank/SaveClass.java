package com.example.fishinbank;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveClass {
    private static SaveClass instance;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "PurposePrefs";
    private static final String KEY_BASE_ALL_MONEY = "base_all_money";
    private static final String KEY_BASE_MONEY = "base_money";
    private static final String KEY_PLUS_COUNT = "plus_count";
    private static final String KEY_MINUS_COUNT = "minus_count";
    private static final String KEY_PLUS_ALL_COUNT = "plus_all_count";
    private static final String KEY_MINUS_ALL_COUNT = "minus_all_count";

    private SaveClass(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SaveClass getInstance(Context context) {
        if (instance == null) {
            instance = new SaveClass(context);
        }
        return instance;
    }

    // Методы для сохранения данных
    public void saveBaseAllMoney(String value) {
        prefs.edit().putString(KEY_BASE_ALL_MONEY, value).apply();
    }

    public void saveBaseMoney(String value) {
        prefs.edit().putString(KEY_BASE_MONEY, value).apply();
    }

    public void savePlusCount(int value) {
        prefs.edit().putInt(KEY_PLUS_COUNT, value).apply();
    }

    public void saveMinusCount(int value) {
        prefs.edit().putInt(KEY_MINUS_COUNT, value).apply();
    }

    public void savePlusAllCount(int value) {
        prefs.edit().putInt(KEY_PLUS_ALL_COUNT, value).apply();
    }

    public void saveMinusAllCount(int value) {
        prefs.edit().putInt(KEY_MINUS_ALL_COUNT, value).apply();
    }

    // Методы для загрузки данных
    public String getBaseAllMoney() {
        return prefs.getString(KEY_BASE_ALL_MONEY, "0");
    }

    public String getBaseMoney() {
        return prefs.getString(KEY_BASE_MONEY, "0");
    }

    public int getPlusCount() {
        return prefs.getInt(KEY_PLUS_COUNT, 0);
    }

    public int getMinusCount() {
        return prefs.getInt(KEY_MINUS_COUNT, 0);
    }

    public int getPlusAllCount() {
        return prefs.getInt(KEY_PLUS_ALL_COUNT, 0);
    }

    public int getMinusAllCount() {
        return prefs.getInt(KEY_MINUS_ALL_COUNT, 0);
    }

    // Метод для очистки всех данных (если нужно)
    public void clearAllData() {
        prefs.edit().clear().apply();
    }
}