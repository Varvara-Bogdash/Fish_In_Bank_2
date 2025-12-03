package com.example.fishinbank;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddPurposeActivity extends AppCompatActivity {
    EditText allMoneyText;
    EditText moneyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purpose);

        allMoneyText = findViewById(R.id.allMoneyAdd);
        moneyText = findViewById(R.id.moneyAdd);
    }

    public void save(View view) {
        String allMoney = allMoneyText.getText().toString();
        String money = moneyText.getText().toString();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("allMoney", allMoney);
        resultIntent.putExtra("money", money);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void house(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}