package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView incomeTextView;
    private TextView chargeTextView;
    private PieChartView expenseChart;
    private IncomePieChartView incomeChart;
    private ChartStateManager chartStateManager;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация форматтера для чисел с разделением разрядов
        initDecimalFormatter();

        // Инициализация менеджера сохранения состояния
        chartStateManager = new ChartStateManager(this);

        // Загрузка сохраненных данных при создании активности
        loadSavedData();

        // Инициализация TextView для отображения доходов и расходов
        incomeTextView = findViewById(R.id.income);
        chargeTextView = findViewById(R.id.charge);

        // Инициализация диаграмм
        expenseChart = findViewById(R.id.expenseChart);
        incomeChart = findViewById(R.id.incomeChart);

        // Настройка диаграмм с загруженными данными
        setupChartsWithLoadedData();

        // Обновление отображения
        updateDisplay();
    }

    /**
     * Инициализация форматтера для чисел с разделением разрядов
     */
    private void initDecimalFormatter() {
        // Создаем DecimalFormat с разделителями разрядов
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' '); // Используем пробел как разделитель тысяч
        symbols.setDecimalSeparator('.'); // Десятичный разделитель - точка

        decimalFormat = new DecimalFormat("#,##0.00", symbols);
        decimalFormat.setGroupingSize(3); // Размер группы - 3 цифры (тысячи)
        decimalFormat.setGroupingUsed(true); // Включаем группировку разрядов
    }

    /**
     * Форматирование числа с разделением разрядов
     */
    private String formatNumberWithSpaces(double number) {
        return decimalFormat.format(number) + " руб.";
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем отображение при возвращении на экран
        updateDisplay();
        refreshCharts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Сохраняем состояние при уходе с экрана
        saveCurrentState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Сохраняем состояние при выходе из приложения
        saveCurrentState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Финальное сохранение при уничтожении активности
        saveCurrentState();
    }

    /**
     * Загрузка сохраненных данных из SharedPreferences
     */
    private void loadSavedData() {
        if (chartStateManager != null) {
            chartStateManager.loadAllData();
        }
    }

    /**
     * Сохранение текущего состояния в SharedPreferences
     */
    private void saveCurrentState() {
        if (chartStateManager != null) {
            chartStateManager.saveAllData();
        }
    }

    /**
     * Настройка диаграмм с загруженными данными
     */
    private void setupChartsWithLoadedData() {
        if (expenseChart != null && DataManager.expenseValues.length > 0) {
            expenseChart.setData(DataManager.expenseValues);
            if (DataManager.expenseCategories.length > 0) {
                expenseChart.setCategoryNames(DataManager.expenseCategories);
            }
        }

        if (incomeChart != null && DataManager.incomeValues.length > 0) {
            incomeChart.setData(DataManager.incomeValues);
            if (DataManager.incomeCategories.length > 0) {
                incomeChart.setCategoryNames(DataManager.incomeCategories);
            }
        }
    }

    /**
     * Обновление отображения текстовых данных
     */
    private void updateDisplay() {
        // Обновляем отображение доходов и расходов с разделением разрядов
        String incomeText = formatNumberWithSpaces(DataManager.totalIncome);
        String chargeText = formatNumberWithSpaces(DataManager.totalExpense);

        incomeTextView.setText(incomeText);
        chargeTextView.setText(chargeText);

        // Обновляем данные диаграмм если они изменились
        if (DataManager.hasExpenseData()) {
            DataManager.updateExpenseChartData();
        }
        if (DataManager.hasIncomeData()) {
            DataManager.updateIncomeChartData();
        }
    }

    /**
     * Обновление отображения диаграмм
     */
    private void refreshCharts() {
        if (expenseChart != null) {
            // Если есть данные для отображения
            if (DataManager.expenseValues.length > 0) {
                expenseChart.setData(DataManager.expenseValues);
                if (DataManager.expenseCategories.length > 0) {
                    expenseChart.setCategoryNames(DataManager.expenseCategories);
                }
            }
            expenseChart.invalidate(); // Принудительная перерисовка
        }

        if (incomeChart != null) {
            // Если есть данные для отображения
            if (DataManager.incomeValues.length > 0) {
                incomeChart.setData(DataManager.incomeValues);
                if (DataManager.incomeCategories.length > 0) {
                    incomeChart.setCategoryNames(DataManager.incomeCategories);
                }
            }
            incomeChart.invalidate(); // Принудительная перерисовка
        }
    }

    /**
     * Обработчик нажатия кнопки добавления расхода/дохода
     */
    public void add(View view) {
        Intent intent = new Intent(this, ChargeActivity.class);
        startActivity(intent);
    }

    /**
     * Обработчик нажатия кнопки информации об авторе
     */
    public void author(View view) {
        Intent intent = new Intent(this, AuthorClass.class);
        startActivity(intent);
    }

    /**
     * Обработчик нажатия кнопки целей
     */
    public void purpose(View view) {
        Intent intent = new Intent(this, PurposeActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для принудительного обновления данных (можно вызывать из других активностей)
     */
    public void forceDataUpdate() {
        loadSavedData();
        updateDisplay();
        refreshCharts();
    }

    /**
     * Метод для сброса всех данных (для тестирования)
     */
    public void resetAllData(View view) {
        if (chartStateManager != null) {
            chartStateManager.clearAllData();
        }
        DataManager.resetData();
        updateDisplay();
        refreshCharts();
    }

    /**
     * Метод для обновления данных после возвращения из другой активности
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) { // Можно задать коды запросов
            loadSavedData();
            updateDisplay();
            refreshCharts();
        }
    }
}