package com.example.fishinbank;

import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingsActivity extends AppCompatActivity {
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
            layout.setBackgroundResource(R.drawable.new_fon);
        } else {
            layout.setBackgroundResource(R.drawable.fon);
        }
        isColorMode = !isColorMode; // переключение флага
    }

    public void home(View view) {

    }
}