package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddChargeActivity extends AppCompatActivity {

    private EditText etAmount;
    private TextView tvCategory;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charge);

        tvCategory = findViewById(R.id.tv_category);
        etAmount = findViewById(R.id.et_amount);
        Button btnAdd = findViewById(R.id.btn_add);

        // Получаем категорию из Intent
        category = getIntent().getStringExtra("CATEGORY");
        if (category != null) {
            tvCategory.setText("Категория: " + category);
        } else {
            tvCategory.setText("Категория: Не указана");
        }

        btnAdd.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount > 0) {
                        DataManager.addExpense(category, amount);
                        // Возвращаемся на главный экран
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Сумма должна быть больше нуля", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
            }
        });
    }
}