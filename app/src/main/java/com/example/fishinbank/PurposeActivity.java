package com.example.fishinbank;

import static java.lang.Integer.parseInt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PurposeActivity extends AppCompatActivity {
    TextView allCountText;
    TextView countText;
    String allMoney;
    String money;
    SaveClass saveClass;
    int plusCount;
    int plusAllCount;
    int minusCount;
    int minusAllCount;
    int count;
    int allCount;
    ImageView cupImage;
    int countOfClick = 0;


    ActivityResultLauncher<Intent> addPurposeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        allMoney = data.getStringExtra("allMoney");
                        money = data.getStringExtra("money");
                        allCount = allCount + Integer.parseInt(allMoney);
                        count = count + Integer.parseInt(money);
                        // Обновляем TextView с нужными надписями и значениями
                        if (allMoney != null && !allMoney.isEmpty()) {
                            allCountText.setText("Желаемая сумма: " + String.valueOf(allCount));
                        }
                        if (money != null && !money.isEmpty()) {
                            countText.setText("Накоплено: " + String.valueOf(count));
                        }
                        if(count>=allCount){
                            cupImage.setImageResource(R.drawable.cup_all);
                            countOfClick=0;
                        }
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purpose);

        allCountText = findViewById(R.id.allCountText);
        countText = findViewById(R.id.cointText);
        cupImage = findViewById(R.id.cupImage); // Инициализируем ImageView
        allCountText.setFreezesText(true);
        countText.setFreezesText(true);
        saveClass = new SaveClass();
        cupImage.setImageResource(R.drawable.cup);
    }

    public void addCount(View view) {
        Intent intent = new Intent(this, AddPurposeActivity.class);
        addPurposeLauncher.launch(intent);
        if(count>=allCount){
            cupImage.setImageResource(R.drawable.cup_all);
            countOfClick=0;
        }
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void author(View view) {
        Intent intent = new Intent(this, AuthorClass.class);
        startActivity(intent);
    }

    public void wallet(View view) {
        Intent intent = new Intent(this, ChargeActivity.class);
        startActivity(intent);
    }

    public void minusCount(View view) {
        minusCount -= 500;
        count = Integer.parseInt(plusCount + minusCount + money);
        countText.setText("Накоплено: " + String.valueOf(count));
        countOfClick++;
        if(countOfClick <= 5) {
            cupImage.setImageResource(R.drawable.cup_add);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        } else if (countOfClick <= 10) {
            cupImage.setImageResource(R.drawable.cup_one_quarter);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        } else if (countOfClick<=15) {
            cupImage.setImageResource(R.drawable.cup_half);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        } else {
            cupImage.setImageResource(R.drawable.cup_three_quarter);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        }


    }

    public void plusCount(View view) {
        plusCount += 1000;
        count = Integer.parseInt(plusCount + minusCount + money);
        countText.setText("Накоплено: " + String.valueOf(count));
        countOfClick++;
        if(countOfClick <= 5) {
            cupImage.setImageResource(R.drawable.cup_add);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        } else if (countOfClick <= 10) {
            cupImage.setImageResource(R.drawable.cup_one_quarter);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        } else if (countOfClick<=15) {
            cupImage.setImageResource(R.drawable.cup_half);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        } else {
            cupImage.setImageResource(R.drawable.cup_three_quarter);
            if(count>=allCount){
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick=0;
            }
        }

    }

    public void plusAllCount(View view) {
        plusAllCount += 1000;
        allCount = Integer.parseInt(plusAllCount + minusAllCount + allMoney);
        allCountText.setText("Желаемая сумма: " + String.valueOf(allCount));
        if(count>=allCount){
            cupImage.setImageResource(R.drawable.cup_all);
            countOfClick=0;
        }
    }

    public void minusAllCount(View view) {
        minusAllCount -= 500;
        allCount = Integer.parseInt(plusAllCount + minusAllCount + allMoney);
        allCountText.setText("Желаемая сумма: " + String.valueOf(allCount));
        if(count>=allCount){
            cupImage.setImageResource(R.drawable.cup_all);
            countOfClick=0;
        }
    }
}