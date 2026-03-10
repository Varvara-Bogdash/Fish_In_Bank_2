package com.example.fishinbank;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    public static double totalIncome = 0.0;
    public static double totalExpense = 0.0;
    public static Map<String, Double> expensesByCategory = new HashMap<>();
    public static Map<String, Double> incomesByCategory = new HashMap<>();

    // Для хранения данных диаграмм в виде массивов
    public static float[] expenseValues = new float[0];
    public static float[] incomeValues = new float[0];

    // Для хранения названий категорий (для легенды диаграмм)
    public static String[] expenseCategories = new String[0];
    public static String[] incomeCategories = new String[0];

    public static void addExpense(String category, double amount) {
        totalExpense += amount;

        // Если категория уже существует, добавляем к существующей сумме
        if (expensesByCategory.containsKey(category)) {
            double currentAmount = expensesByCategory.get(category);
            expensesByCategory.put(category, currentAmount + amount);
        } else {
            expensesByCategory.put(category, amount);
        }

        // Обновляем данные для диаграммы
        updateExpenseChartData();
    }

    public static void addIncome(String category, double amount) {
        totalIncome += amount;

        // Если категория уже существует, добавляем к существующей сумме
        if (incomesByCategory.containsKey(category)) {
            double currentAmount = incomesByCategory.get(category);
            incomesByCategory.put(category, currentAmount + amount);
        } else {
            incomesByCategory.put(category, amount);
        }

        // Обновляем данные для диаграммы
        updateIncomeChartData();
    }

    // Метод для сброса всех данных (для тестирования)
    public static void resetData() {
        totalIncome = 0.0;
        totalExpense = 0.0;
        expensesByCategory.clear();
        incomesByCategory.clear();
        expenseValues = new float[0];
        incomeValues = new float[0];
        expenseCategories = new String[0];
        incomeCategories = new String[0];
    }

    // Метод для обновления данных диаграммы расходов
    public static void updateExpenseChartData() {
        int size = expensesByCategory.size();
        expenseValues = new float[size];
        expenseCategories = new String[size];

        int i = 0;
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            expenseCategories[i] = entry.getKey();
            expenseValues[i] = entry.getValue().floatValue();
            i++;
        }
    }

    // Метод для обновления данных диаграммы доходов
    public static void updateIncomeChartData() {
        int size = incomesByCategory.size();
        incomeValues = new float[size];
        incomeCategories = new String[size];

        int i = 0;
        for (Map.Entry<String, Double> entry : incomesByCategory.entrySet()) {
            incomeCategories[i] = entry.getKey();
            incomeValues[i] = entry.getValue().floatValue();
            i++;
        }
    }
    // Метод для проверки наличия данных
    public static boolean hasExpenseData() {
        return !expensesByCategory.isEmpty();
    }

    public static boolean hasIncomeData() {
        return !incomesByCategory.isEmpty();
    }

}