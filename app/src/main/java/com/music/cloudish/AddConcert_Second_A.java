package com.music.cloudish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddConcert_Second_A extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concert__second_);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}