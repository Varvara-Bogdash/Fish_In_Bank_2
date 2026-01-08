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

    // Метод для получения суммы расходов по категории
    public static double getExpenseByCategory(String category) {
        return expensesByCategory.getOrDefault(category, 0.0);
    }

    // Метод для получения суммы доходов по категории
    public static double getIncomeByCategory(String category) {
        return incomesByCategory.getOrDefault(category, 0.0);
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

    // Метод для установки данных расходов (например, при загрузке из сохраненного состояния)
    public static void setExpenseData(Map<String, Double> data) {
        expensesByCategory = new HashMap<>(data);
        totalExpense = 0.0;

        for (Double value : expensesByCategory.values()) {
            totalExpense += value;
        }

        updateExpenseChartData();
    }

    // Метод для установки данных доходов (например, при загрузке из сохраненного состояния)
    public static void setIncomeData(Map<String, Double> data) {
        incomesByCategory = new HashMap<>(data);
        totalIncome = 0.0;

        for (Double value : incomesByCategory.values()) {
            totalIncome += value;
        }

        updateIncomeChartData();
    }

    // Метод для получения данных расходов в формате Map<String, Float> (для совместимости с ChartStateManager)
    public static Map<String, Float> getExpenseDataAsFloatMap() {
        Map<String, Float> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            result.put(entry.getKey(), entry.getValue().floatValue());
        }
        return result;
    }

    // Метод для получения данных доходов в формате Map<String, Float> (для совместимости с ChartStateManager)
    public static Map<String, Float> getIncomeDataAsFloatMap() {
        Map<String, Float> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : incomesByCategory.entrySet()) {
            result.put(entry.getKey(), entry.getValue().floatValue());
        }
        return result;
    }

    // Метод для получения количества категорий расходов
    public static int getExpenseCategoryCount() {
        return expensesByCategory.size();
    }

    // Метод для получения количества категорий доходов
    public static int getIncomeCategoryCount() {
        return incomesByCategory.size();
    }

    // Метод для получения всех категорий расходов
    public static String[] getAllExpenseCategories() {
        return expensesByCategory.keySet().toArray(new String[0]);
    }

    // Метод для получения всех категорий доходов
    public static String[] getAllIncomeCategories() {
        return incomesByCategory.keySet().toArray(new String[0]);
    }

    // Метод для проверки наличия данных
    public static boolean hasExpenseData() {
        return !expensesByCategory.isEmpty();
    }

    public static boolean hasIncomeData() {
        return !incomesByCategory.isEmpty();
    }

    // Метод для получения общего баланса (доходы - расходы)
    public static double getBalance() {
        return totalIncome - totalExpense;
    }
}