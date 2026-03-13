package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class IncomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        setupBackground(R.id.activity_income);
        // Находим все кнопки категорий доходов и назначаем обработчики
        setupButton(R.id.investitionButton, "Доходы по вкладам");
        setupButton(R.id.imageButton3, "Доходы по инвестициям");
        setupButton(R.id.salaryButton, "Зарплата");
        setupButton(R.id.incomeAddButton, "Частичная занятость");
        setupButton(R.id.anywayButton, "Другие доходы");
        // Кнопка перехода к расходам
        Button chargeButton = findViewById(R.id.buttonCharge);
        chargeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ChargeActivity.class));
        });
    }
    private void setupButton(int buttonId, String category) {
        ImageButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddIncomeActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        });
    }


    public void charge(View view) {
        startActivity(new Intent(this, ChargeActivity.class));
    }
}