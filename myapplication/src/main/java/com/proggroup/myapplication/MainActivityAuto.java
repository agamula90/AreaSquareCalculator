package com.proggroup.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.proggroup.areasquarecalculator.utils.AutoCalculations;
import com.proggroup.squarecalculations.CalculateUtils;

public class MainActivityAuto extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auto);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.progress_container);
        EditText editText = (EditText) findViewById(R.id.edit_result);

        AutoCalculations.calculateAuto(frameLayout, editText);
    }
}
