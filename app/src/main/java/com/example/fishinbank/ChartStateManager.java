package com.example.fishinbank;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

public class ChartStateManager {

    private static final String PREFS_NAME = "ChartStatePrefs";
    private static final String KEY_EXPENSE_DATA = "expense_chart_data";
    private static final String KEY_INCOME_DATA = "income_chart_data";
    private static final String KEY_TOTAL_INCOME = "total_income";
    private static final String KEY_TOTAL_EXPENSE = "total_expense";
    private static final String KEY_EXPENSE_VALUES = "expense_chart_values";
    private static final String KEY_INCOME_VALUES = "income_chart_values";
    private static final String KEY_EXPENSE_CATEGORIES = "expense_categories";
    private static final String KEY_INCOME_CATEGORIES = "income_categories";

    private final SharedPreferences prefs;

    public ChartStateManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Сохранение всех данных
    public void saveAllData() {
        SharedPreferences.Editor editor = prefs.edit();

        // Сохраняем общие суммы
        editor.putFloat(KEY_TOTAL_INCOME, (float) DataManager.totalIncome);
        editor.putFloat(KEY_TOTAL_EXPENSE, (float) DataManager.totalExpense);

        // Сохраняем данные расходов
        editor.putString(KEY_EXPENSE_DATA, serializeMap(DataManager.expensesByCategory));

        // Сохраняем данные доходов
        editor.putString(KEY_INCOME_DATA, serializeMap(DataManager.incomesByCategory));

        // Сохраняем массивы значений
        saveFloatArray(KEY_EXPENSE_VALUES, DataManager.expenseValues, editor);
        saveFloatArray(KEY_INCOME_VALUES, DataManager.incomeValues, editor);

        // Сохраняем названия категорий
        saveStringArray(KEY_EXPENSE_CATEGORIES, DataManager.expenseCategories, editor);
        saveStringArray(KEY_INCOME_CATEGORIES, DataManager.incomeCategories, editor);

        editor.apply();
    }

    // Загрузка всех данных
    public void loadAllData() {
        // Загружаем общие суммы
        DataManager.totalIncome = prefs.getFloat(KEY_TOTAL_INCOME, 0.0f);
        DataManager.totalExpense = prefs.getFloat(KEY_TOTAL_EXPENSE, 0.0f);

        // Загружаем данные расходов
        String expenseDataStr = prefs.getString(KEY_EXPENSE_DATA, "");
        if (!expenseDataStr.isEmpty()) {
            DataManager.expensesByCategory = deserializeMap(expenseDataStr);
        }

        // Загружаем данные доходов
        String incomeDataStr = prefs.getString(KEY_INCOME_DATA, "");
        if (!incomeDataStr.isEmpty()) {
            DataManager.incomesByCategory = deserializeMap(incomeDataStr);
        }

        // Загружаем массивы значений
        DataManager.expenseValues = loadFloatArray(KEY_EXPENSE_VALUES);
        DataManager.incomeValues = loadFloatArray(KEY_INCOME_VALUES);

        // Загружаем названия категорий
        DataManager.expenseCategories = loadStringArray(KEY_EXPENSE_CATEGORIES);
        DataManager.incomeCategories = loadStringArray(KEY_INCOME_CATEGORIES);
    }

    // Сериализация Map в строку
    private String serializeMap(Map<String, Double> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            // Экранируем специальные символы в ключе
            String key = entry.getKey().replace(":", "\\:").replace(";", "\\;");
            builder.append(key)
                    .append(":")
                    .append(entry.getValue())
                    .append(";");
        }
        return builder.toString();
    }

    // Десериализация строки в Map
    private Map<String, Double> deserializeMap(String dataStr) {
        Map<String, Double> map = new HashMap<>();
        if (dataStr.isEmpty()) return map;

        String[] entries = dataStr.split(";(?<!\\\\)");
        for (String entry : entries) {
            if (!entry.isEmpty()) {
                String[] parts = entry.split(":(?<!\\\\)");
                if (parts.length == 2) {
                    // Убираем экранирование
                    String key = parts[0].replace("\\:", ":").replace("\\;", ";");
                    try {
                        double value = Double.parseDouble(parts[1]);
                        map.put(key, value);
                    } catch (NumberFormatException e) {
                        // Игнорируем некорректные значения
                    }
                }
            }
        }
        return map;
    }

    // Вспомогательные методы для сохранения/загрузки массивов
    private void saveFloatArray(String key, float[] array, SharedPreferences.Editor editor) {
        StringBuilder builder = new StringBuilder();
        for (float value : array) {
            builder.append(value).append(",");
        }
        editor.putString(key, builder.toString());
    }

    private float[] loadFloatArray(String key) {
        String arrayStr = prefs.getString(key, "");
        if (arrayStr.isEmpty()) return new float[0];

        String[] parts = arrayStr.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Float.parseFloat(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

    private void saveStringArray(String key, String[] array, SharedPreferences.Editor editor) {
        StringBuilder builder = new StringBuilder();
        for (String value : array) {
            // Экранируем запятые в значениях
            String escapedValue = value.replace(",", "\\,");
            builder.append(escapedValue).append(",");
        }
        editor.putString(key, builder.toString());
    }

    private String[] loadStringArray(String key) {
        String arrayStr = prefs.getString(key, "");
        if (arrayStr.isEmpty()) return new String[0];

        // Разделяем по запятым, которым не предшествует обратный слэш
        String[] parts = arrayStr.split("(?<!\\\\),");
        for (int i = 0; i < parts.length; i++) {
            // Убираем экранирование
            parts[i] = parts[i].replace("\\,", ",");
        }
        return parts;
    }

    // Очистка всех данных
    public void clearAllData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}