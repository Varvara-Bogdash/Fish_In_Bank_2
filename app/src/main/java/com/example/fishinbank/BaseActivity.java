package com.example.fishinbank;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseActivity extends AppCompatActivity {

    protected ConstraintLayout rootLayout;
    protected BackgroundPrefs bgPrefs;
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bgPrefs = BackgroundPrefs.getInstance(this);
    }


    protected void setupBackground(int rootLayoutId) {
        try {
            rootLayout = findViewById(rootLayoutId);
            if (rootLayout == null) {
                Log.e(TAG, "rootLayout is NULL! Check if ID " + rootLayoutId + " exists in your XML");
                return;
            }
            applyBackground();
        } catch (Exception e) {
            Log.e(TAG, "Error applying background", e);
        }
    }

    protected void applyBackground() {
        if (rootLayout == null) {
            Log.w(TAG, "applyBackground called but rootLayout is null");
            return;
        }
        try {
            int bgResId = BackgroundPrefs.getBackgroundResId(this);
            rootLayout.setBackgroundResource(bgResId);
            Log.d(TAG, "Background applied: " + getResources().getResourceName(bgResId));
        } catch (Exception e) {
            Log.e(TAG, "Failed to set background resource", e);
            try {
                rootLayout.setBackgroundResource(R.drawable.fon);
            } catch (Exception e2) {
                Log.e(TAG, "Even fallback failed!", e2);
                rootLayout.setBackgroundColor(0xFF121212);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyBackground(); // Обновляем фон при возврате на экран
    }
}