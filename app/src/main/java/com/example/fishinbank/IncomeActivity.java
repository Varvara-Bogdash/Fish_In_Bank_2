package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class IncomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        // Находим все кнопки категорий доходов и назначаем обработчики
        setupButton(R.id.investitionButton, "Доходы по вкладам");
        setupButton(R.id.imageButton3, "Доходы по акциям/облигациям");
        setupButton(R.id.salaryButton, "Зарплата");
        setupButton(R.id.incomeAddButton, "Частичная занятость");
        setupButton(R.id.anywayButton, "Другие доходы");

        // Кнопка перехода к расходам
        ImageButton chargeButton = findViewById(R.id.chargeButton);
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

    // Обработчик для кнопки перехода к расходам (если используется android:onClick в XML)
    public void charge(View view) {
        startActivity(new Intent(this, ChargeActivity.class));
    }
}