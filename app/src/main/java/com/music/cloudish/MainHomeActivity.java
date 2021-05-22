package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import Else.Global;

public class MainHomeActivity extends AppCompatActivity {

    BottomNavigationView bn;
    Fragment selected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.mainC, new Home_F()).commit();

        bn=findViewById(R.id.bottom_nav);
        bn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_ic:
                        selected  = new Home_F();
                        break;
                    case R.id.search_ic:
                        selected = new Search_F();
                        break;
                    case R.id.lib_ic:
                        selected = new Library_F();
                        Global.flib=(Library_F) selected;
                        break;
                    case R.id.profile_ic:
                        selected=new Profile_F();
                        break;
                }
                if (selected!=null){
                    fm.beginTransaction().replace(R.id.mainC, selected).commit();
                }
                return true;
            }
        });


    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if (Global.jcpg!=null && Global.jcpg.isPaused()){
                Global.jcpg.kill();
            }
            moveTaskToBack(true);
        }else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to push app to background", Toast.LENGTH_SHORT).show();
        }



        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }



    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }
}