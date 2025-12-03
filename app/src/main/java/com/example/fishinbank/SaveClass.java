package com.example.fishinbank;

import android.widget.TextView;

public class SaveClass {
    public TextView textView;


    public static TextView saveText(TextView textView1, TextView textView, String allMoney, String money){
        textView.setText("Накоплено: " + money);
        textView1.setText("Желаемая сумма: " + allMoney);
        return textView;
    }
}
