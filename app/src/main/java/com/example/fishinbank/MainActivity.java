package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends BaseActivity {

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
        setupBackground(R.id.activity_main);
        initDecimalFormatter();
        chartStateManager = new ChartStateManager(this);
        loadSavedData();
        // Инициализация TextView для отображения доходов и расходов
        incomeTextView = findViewById(R.id.income);
        chargeTextView = findViewById(R.id.charge);
        // Инициализация диаграмм
        expenseChart = findViewById(R.id.expenseChart);
        incomeChart = findViewById(R.id.incomeChart);
        setupChartsWithLoadedData();
        updateDisplay();
    }
    private void initDecimalFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' '); // Используем пробел как разделитель тысяч
        symbols.setDecimalSeparator('.'); // Десятичный разделитель - точка

        decimalFormat = new DecimalFormat("#,##0.00", symbols);
        decimalFormat.setGroupingSize(3); // Размер группы - 3 цифры (тысячи)
        decimalFormat.setGroupingUsed(true); // Включаем группировку разрядов
    }
    private String formatNumberWithSpaces(double number) {
        return decimalFormat.format(number) + " руб.";
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
        refreshCharts();
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveCurrentState();
    }
    @Override
    protected void onStop() {
        super.onStop();
        saveCurrentState();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCurrentState();
    }
    private void loadSavedData() {
        if (chartStateManager != null) {
            chartStateManager.loadAllData();
        }
    }
    private void saveCurrentState() {
        if (chartStateManager != null) {
            chartStateManager.saveAllData();
        }
    }
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
    private void updateDisplay() {
        String incomeText = formatNumberWithSpaces(DataManager.totalIncome);
        String chargeText = formatNumberWithSpaces(DataManager.totalExpense);
        incomeTextView.setText(incomeText);
        chargeTextView.setText(chargeText);
        if (DataManager.hasExpenseData()) {
            DataManager.updateExpenseChartData();
        }
        if (DataManager.hasIncomeData()) {
            DataManager.updateIncomeChartData();
        }
    }
    private void refreshCharts() {
        if (expenseChart != null) {
            if (DataManager.expenseValues.length > 0) {
                expenseChart.setData(DataManager.expenseValues);
                if (DataManager.expenseCategories.length > 0) {
                    expenseChart.setCategoryNames(DataManager.expenseCategories);
                }
            }
            expenseChart.invalidate();
        }

        if (incomeChart != null) {
            if (DataManager.incomeValues.length > 0) {
                incomeChart.setData(DataManager.incomeValues);
                if (DataManager.incomeCategories.length > 0) {
                    incomeChart.setCategoryNames(DataManager.incomeCategories);
                }
            }
            incomeChart.invalidate();
        }
    }
    public void add(View view) {
        Intent intent = new Intent(this, ChargeActivity.class);
        startActivity(intent);
    }
    public void author(View view) {
        Intent intent = new Intent(this, AuthorClass.class);
        startActivity(intent);
    }
    public void purpose(View view) {
        Intent intent = new Intent(this, PurposeActivity.class);
        startActivity(intent);
    }
    public void resetAllData(View view) {
        incomeTextView.setText("0.00 руб.");
        chargeTextView.setText("0.00 руб.");
        if (expenseChart != null) {
            expenseChart.setData(new float[0]);
            expenseChart.invalidate();
        }
        if (incomeChart != null) {
            incomeChart.setData(new float[0]);
            incomeChart.invalidate();
        }
        DataManager.resetData();
        updateDisplay();
        refreshCharts();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            loadSavedData();
            updateDisplay();
            refreshCharts();
        }
    }

}