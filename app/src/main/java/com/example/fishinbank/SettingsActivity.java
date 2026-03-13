package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends BaseActivity {

    private EditText amountEditText;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private Button convertButton;
    private TextView resultTextView;
    private Button fonButton;

    private Map<String, Double> rates = new HashMap<>();
    private String[] currencies = {"USD", "EUR", "RUB", "CNY", "AED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupBackground(R.id.root_layout);
        initRates();
        fonButton = findViewById(R.id.fonbutton);
        amountEditText = findViewById(R.id.amountEditText);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        resultTextView = findViewById(R.id.textView6);
        setupSpinners();
        updateButtonText();
        convertButton.setOnClickListener(v -> convertCurrency());
        fonButton.setOnClickListener(v -> fonChange(v));
    }

    private void initRates() {
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92);
        rates.put("RUB", 92.50);
        rates.put("CNY", 7.23);
        rates.put("AED", 3.67);
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setSelection(1);
    }

    private void convertCurrency() {
        String amountText = amountEditText.getText().toString().trim();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Введите сумму!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            String from = fromCurrencySpinner.getSelectedItem().toString();
            String to = toCurrencySpinner.getSelectedItem().toString();
            double result = (amount / rates.get(from)) * rates.get(to);
            resultTextView.setText(String.format("Результат: %.2f %s", result, to));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный формат числа!", Toast.LENGTH_SHORT).show();
        }
    }


    public void fonChange(View view) {
        bgPrefs.toggleBackground();
        applyBackground();
        updateButtonText();
    }

    private void updateButtonText() {
        if (bgPrefs.useAlternativeBackground()) {
            fonButton.setText("🗂️ Изменить цвет фона на светлый");
        } else {
            fonButton.setText("🎨 Изменить цвет фона на тёмный");
        }
    }

    public void home(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
