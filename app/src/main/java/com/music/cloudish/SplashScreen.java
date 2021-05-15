package com.music.cloudish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    // Time for splash screen to load to another page
    private static int SPLASH_SCREEN = 1800;

    // Variables
    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView title, tagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Hook
        logo = findViewById(R.id.logo);
        title = findViewById(R.id.title);
        tagline = findViewById(R.id.tagline);

//        // Set Screen to Fullscreen
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            final WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.hide(WindowInsets.Type.statusBars());
//            }
//        } else {
//            // FLAG_FULLSCREEN is deprecated in android R
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//            );
//        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    Intent i = new Intent(SplashScreen.this, MainHomeActivity.class);
                    startActivity(i);
                    finishAffinity();
                }else{
                    Intent i = new Intent(SplashScreen.this, Login_A.class);
                    // Pair for animation
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logo, "transition_logo");
                    pairs[1] = new Pair<View, String>(title, "transition_text");

                    // options for animation
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, pairs);
                    startActivity(i, options.toBundle());
                    finish();

                }

            }
        }, 1800);
    }
}