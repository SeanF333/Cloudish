package com.music.cloudish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PaymentConfirmation_A extends AppCompatActivity {

    List<String> album_list;
    MaterialButton okay_btn;
    TextView payment_counter, payment_amount;
    CountDownTimer cTimer;
    Integer album_count, count;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation_);

        // Hook
        okay_btn = findViewById(R.id.okay_btn);
        payment_counter = findViewById(R.id.payment_counter);
        payment_amount = findViewById(R.id.payment_amount);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // getContent from Intent
        album_list = getIntent().getStringArrayListExtra("album_list");
        album_count = album_list.size();


        okay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoteAlbum();
            }
        });

        count = album_count*20000;
        payment_amount.setText("You should pay : Rp. "+count);

        startTimer();
    }

    private void promoteAlbum() {
        for(String s:album_list){
            savetoDatabase(s);
        }
        finish();
    }

    private void savetoDatabase(String s) {
        DatabaseReference mPromoted = mDatabase.child("Promoted");
        mPromoted.child(s).setValue(true);
    }

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                payment_counter.setText("Seconds remaining : " + millisUntilFinished/1000);
            }
            public void onFinish() {
                return;
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        cancelTimer();
        nMgr.cancelAll();
        super.onDestroy();
    }
}