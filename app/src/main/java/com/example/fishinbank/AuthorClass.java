package com.example.fishinbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AuthorClass extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupBackground(R.id.activity_author);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_class);
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}