package com.example.fishinbank;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ChargeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupBackground(R.id.activity_charge);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        setupButton(R.id.eat, "Еда");
        setupButton(R.id.car, "Транспорт");
        setupButton(R.id.gift, "Подарки");
        setupButton(R.id.relax, "Отдых");
        setupButton(R.id.travel, "Путешествия");
        setupButton(R.id.lunch, "Обеды");
        setupButton(R.id.school, "Образование");
        setupButton(R.id.myself, "Красота");
        setupButton(R.id.house, "Жилье");
        setupButton(R.id.shop, "Шоппинг");
        setupButton(R.id.notControl, "Другие расходы");
        setupButton(R.id.clinic, "Здоровье");
        Button incomeButton = findViewById(R.id.buttonCharge);
        incomeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, IncomeActivity.class));
        });
    }
    private void setupButton(int buttonId, String category) {
        ImageButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddChargeActivity.class);
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        });
    }



}