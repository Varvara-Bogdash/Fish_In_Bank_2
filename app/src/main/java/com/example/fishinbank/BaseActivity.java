package com.example.fishinbank;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseActivity extends AppCompatActivity {

    protected ConstraintLayout rootLayout;
    protected BackgroundPrefs bgPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bgPrefs = BackgroundPrefs.getInstance(this);
        // setContentView() вызывается в дочерних классах
    }


    protected void setupBackground(int layoutId) {
        rootLayout = findViewById(layoutId);
        applyBackground();
    }


    protected void applyBackground() {
        if (rootLayout == null) return;

        if (bgPrefs.isColorBackground()) {
            rootLayout.setBackgroundColor(BackgroundPrefs.COLOR_VALUE);
        } else {
            rootLayout.setBackgroundResource(BackgroundPrefs.DRAWABLE_VALUE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        applyBackground();
    }


    protected void toggleGlobalBackground() {
        bgPrefs.toggleBackground();
        applyBackground();
    }
}