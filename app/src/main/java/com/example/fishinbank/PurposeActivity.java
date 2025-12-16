package com.example.fishinbank;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PurposeActivity extends AppCompatActivity {
    TextView allCountText;
    TextView countText;
    String allMoney = "0";
    String money = "0";
    SaveClass saveClass;
    int plusCount = 0;
    int plusAllCount = 0;
    int minusCount = 0;
    int minusAllCount = 0;
    int count = 0;
    int allCount = 0;
    ImageView cupImage;
    int countOfClick = 0;

    private boolean congratulationShown = false;

    ActivityResultLauncher<Intent> addPurposeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String newAllMoney = data.getStringExtra("allMoney");
                        String newMoney = data.getStringExtra("money");

                        if (newAllMoney != null && !newAllMoney.isEmpty()) {
                            allMoney = newAllMoney;
                            allCount = Integer.parseInt(allMoney);
                            allCountText.setText("Желаемая сумма: " + allCount);
                            congratulationShown = false;
                        }
                        if (newMoney != null && !newMoney.isEmpty()) {
                            money = newMoney;
                            count = Integer.parseInt(money);
                            countText.setText("Накоплено: " + count);
                        }

                        updateCupImage();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purpose);

        allCountText = findViewById(R.id.allCountText);
        countText = findViewById(R.id.cointText);
        cupImage = findViewById(R.id.cupImage);
        allCountText.setFreezesText(true);
        countText.setFreezesText(true);
        saveClass = new SaveClass();
        cupImage.setImageResource(R.drawable.cup);

        allCountText.setText("Желаемая сумма: 0");
        countText.setText("Накоплено: 0");
    }

    private void updateCupImage() {
        if (count >= allCount && allCount > 0) {
            cupImage.setImageResource(R.drawable.cup_all);
            countOfClick = 0;

            // Показываем поздравление, если еще не показывали
            if (!congratulationShown) {
                showCongratulationsDialog();
                congratulationShown = true;
            }
        } else {
            // Сбрасываем флаг, если счет стал меньше цели
            if (congratulationShown && count < allCount) {
                congratulationShown = false;
            }

            if (countOfClick <= 5) {
                cupImage.setImageResource(R.drawable.cup_add);
            } else if (countOfClick <= 10) {
                cupImage.setImageResource(R.drawable.cup_one_quarter);
            } else if (countOfClick <= 15) {
                cupImage.setImageResource(R.drawable.cup_half);
            } else {
                cupImage.setImageResource(R.drawable.cup_three_quarter);
            }
        }
    }

    private void showCongratulationsDialog() {
        // Используем простой AlertDialog без кастомного layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎉 Поздравляем!");
        builder.setMessage("Вы достигли своей цели!\nВы накопили " + count + " из " + allCount);
        builder.setPositiveButton("Ура!", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addCount(View view) {
        Intent intent = new Intent(this, AddPurposeActivity.class);
        addPurposeLauncher.launch(intent);
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
        count = plusCount + minusCount + Integer.parseInt(money);
        countText.setText("Накоплено: " + count);
        countOfClick++;
        updateCupImage();
    }

    public void plusCount(View view) {
        plusCount += 1000;
        count = plusCount + minusCount + Integer.parseInt(money);
        countText.setText("Накоплено: " + count);
        countOfClick++;
        updateCupImage();
    }

    public void plusAllCount(View view) {
        plusAllCount += 1000;
        allCount = plusAllCount + minusAllCount + Integer.parseInt(allMoney);
        allCountText.setText("Желаемая сумма: " + allCount);
        congratulationShown = false;
        updateCupImage();
    }

    public void minusAllCount(View view) {
        minusAllCount -= 500;
        allCount = plusAllCount + minusAllCount + Integer.parseInt(allMoney);
        allCountText.setText("Желаемая сумма: " + allCount);
        congratulationShown = false;
        updateCupImage();
    }
}