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
    String baseAllMoney = "0"; // Базовое значение цели (из AddPurposeActivity)
    String baseMoney = "0";     // Базовое значение накопления (из AddPurposeActivity)
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
                            baseAllMoney = newAllMoney;
                            allCount = Integer.parseInt(baseAllMoney) + plusAllCount + minusAllCount;
                            allCountText.setText("Желаемая сумма: " + allCount);
                            congratulationShown = false;

                            // Сохраняем в SaveClass
                            if (saveClass != null) {
                                saveClass.saveBaseAllMoney(baseAllMoney);
                            }
                        }
                        if (newMoney != null && !newMoney.isEmpty()) {
                            baseMoney = newMoney;
                            count = Integer.parseInt(baseMoney) + plusCount + minusCount;
                            countText.setText("Накоплено: " + count);

                            // Сохраняем в SaveClass
                            if (saveClass != null) {
                                saveClass.saveBaseMoney(baseMoney);
                            }
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
        saveClass = SaveClass.getInstance(this);

        // Загружаем сохраненные данные
        loadSavedData();

        // Пересчитываем итоговые значения
        allCount = Integer.parseInt(baseAllMoney) + plusAllCount + minusAllCount;
        count = Integer.parseInt(baseMoney) + plusCount + minusCount;

        allCountText.setText("Желаемая сумма: " + allCount);
        countText.setText("Накоплено: " + count);

        // Устанавливаем начальное изображение
        updateCupImage();
    }

    private void loadSavedData() {
        if (saveClass != null) {
            // Загружаем базовые значения
            baseAllMoney = saveClass.getBaseAllMoney();
            baseMoney = saveClass.getBaseMoney();

            // Загружаем счетчики изменений
            plusCount = saveClass.getPlusCount();
            minusCount = saveClass.getMinusCount();
            plusAllCount = saveClass.getPlusAllCount();
            minusAllCount = saveClass.getMinusAllCount();

            // Проверяем на null и устанавливаем значения по умолчанию
            if (baseAllMoney == null || baseAllMoney.isEmpty()) {
                baseAllMoney = "0";
            }
            if (baseMoney == null || baseMoney.isEmpty()) {
                baseMoney = "0";
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Сохраняем данные при уходе с экрана
        saveData();
    }

    private void saveData() {
        if (saveClass != null) {
            saveClass.saveBaseAllMoney(baseAllMoney);
            saveClass.saveBaseMoney(baseMoney);
            saveClass.savePlusCount(plusCount);
            saveClass.saveMinusCount(minusCount);
            saveClass.savePlusAllCount(plusAllCount);
            saveClass.saveMinusAllCount(minusAllCount);
        }
    }

    private void updateCupImage() {
        if (allCount > 0) {
            // Используем double для правильного расчета процентов
            double progress = (double) count / (double) allCount;

            if (count >= allCount) {
                cupImage.setImageResource(R.drawable.cup_all);
                countOfClick = 0;

                // Показываем поздравление, если еще не показывали
                if (!congratulationShown) {
                    showCongratulationsDialog();
                    congratulationShown = true;
                }
            } else {
                // Сбрасываем флаг, если счет стал меньше цели
                if (congratulationShown) {
                    congratulationShown = false;
                }
                if(progress < 0.1){
                    cupImage.setImageResource(R.drawable.cup);
                } else if (progress < 0.25 && progress > 0.1) {
                    cupImage.setImageResource(R.drawable.cup_add);
                } else if (progress < 0.5) {
                    cupImage.setImageResource(R.drawable.cup_one_quarter);
                } else if (progress < 0.75) {
                    cupImage.setImageResource(R.drawable.cup_half);
                } else {
                    cupImage.setImageResource(R.drawable.cup_three_quarter);
                }
            }
        } else {
            // Если цель не установлена (allCount = 0), показываем начальное изображение
            cupImage.setImageResource(R.drawable.cup);
            // Сбрасываем флаг поздравления
            congratulationShown = false;
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
        saveData();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void author(View view) {
        saveData();
        Intent intent = new Intent(this, AuthorClass.class);
        startActivity(intent);
    }

    public void wallet(View view) {
        saveData();
        Intent intent = new Intent(this, ChargeActivity.class);
        startActivity(intent);
    }

    public void minusCount(View view) {
        int newCount = Integer.parseInt(baseMoney) + plusCount + minusCount - 500;
        if (newCount >= 0) {
            minusCount -= 500;
            count = newCount;
            countText.setText("Накоплено: " + count);
            countOfClick++;
            updateCupImage();
        }
    }

    public void plusCount(View view) {
        plusCount += 1000;
        count = Integer.parseInt(baseMoney) + plusCount + minusCount;
        countText.setText("Накоплено: " + count);
        countOfClick++;
        updateCupImage();
    }

    public void plusAllCount(View view) {
        plusAllCount += 1000;
        allCount = Integer.parseInt(baseAllMoney) + plusAllCount + minusAllCount;
        allCountText.setText("Желаемая сумма: " + allCount);
        congratulationShown = false;
        updateCupImage();
    }

    public void minusAllCount(View view) {
        int newAllCount = Integer.parseInt(baseAllMoney) + plusAllCount + minusAllCount - 500;
        if (newAllCount >= 0) {
            minusAllCount -= 500;
            allCount = newAllCount;
            allCountText.setText("Желаемая сумма: " + allCount);
            congratulationShown = false;
            updateCupImage();
        }
    }
}