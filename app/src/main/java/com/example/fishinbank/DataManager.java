package com.example.fishinbank;


import java.util.HashMap;
import java.util.Map;

public class DataManager {
    public static double totalIncome = 0.0;
    public static double totalExpense = 0.0;
    public static Map<String, Double> expensesByCategory = new HashMap<>();
    public static Map<String, Double> incomesByCategory = new HashMap<>();

    public static void addExpense(String category, double amount) {
        totalExpense += amount;

        // Если категория уже существует, добавляем к существующей сумме
        if (expensesByCategory.containsKey(category)) {
            double currentAmount = expensesByCategory.get(category);
            expensesByCategory.put(category, currentAmount + amount);
        } else {
            expensesByCategory.put(category, amount);
        }
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
    }
}