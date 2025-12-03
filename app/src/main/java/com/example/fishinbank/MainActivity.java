package com.example.fishinbank;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView incomeTextView;
    private TextView chargeTextView;
    private PieChartView expenseChart;
    private IncomePieChartView incomeChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация TextView для отображения доходов и расходов
        incomeTextView = findViewById(R.id.income);
        chargeTextView = findViewById(R.id.charge);

        // Инициализация диаграмм
        expenseChart = findViewById(R.id.expenseChart);
        incomeChart = findViewById(R.id.incomeChart);

        updateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
        // Обновляем диаграммы при возвращении на экран
        if (expenseChart != null) {
            expenseChart.invalidate();
        }
        if (incomeChart != null) {
            incomeChart.invalidate();
        }
    }

    private void updateDisplay() {
        // Обновляем отображение доходов и расходов
        String incomeText = "доход: " + String.format("%.2f", DataManager.totalIncome) + " руб.";
        String chargeText = "расход: " + String.format("%.2f", DataManager.totalExpense) + " руб.";

        incomeTextView.setText(incomeText);
        chargeTextView.setText(chargeText);
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
}