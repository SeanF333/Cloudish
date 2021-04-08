package com.music.cloudish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    ProgressBar pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pd=findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.setVisibility(View.GONE);
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    Intent i = new Intent(SplashScreen.this, MainHomeActivity.class);
                    startActivity(i);
                    finishAffinity();
                }else{
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }

            }
        }, 3500L);
    }
}