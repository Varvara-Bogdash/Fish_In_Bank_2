package com.example.fishinbank;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingsActivity extends BaseActivity {
    ConstraintLayout layout;

    private boolean isColorMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layout = findViewById(R.id.root_layout);
    }
    public void fonChange(View view) {
        if (!isColorMode) {
            setupBackground(R.id.root_layout);
        } else {
            setupBackground(R.id.root_layout);
        }
        isColorMode = !isColorMode; // переключение флага
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}