package com.music.cloudish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class Login_A extends AppCompatActivity {

    ImageView logo;
    TextView title, tv_sign_up;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__c);

        // Hook
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        logo = findViewById(R.id.logo);
        title = findViewById(R.id.title);
        tv_sign_up = findViewById(R.id.tv_sign_up);

        // Mengisi Tab Layout

        tabLayout.addTab(tabLayout.newTab().setText("Email"));
        tabLayout.addTab(tabLayout.newTab().setText("Phone"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.main_purple), getResources().getColor(R.color.abuabu));
        tabLayout.setupWithViewPager(viewPager);

        final Login_Ad adapter = new Login_Ad(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Go to SignUp Screen Listener
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUpScreen();
            }
        });
    }

    private void goToSignUpScreen() {
        Intent i = new Intent(Login_A.this, SignUp_A.class);
        startActivity(i);
    }
}