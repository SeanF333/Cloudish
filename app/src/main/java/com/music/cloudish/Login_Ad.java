package com.music.cloudish;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Login_Ad extends FragmentPagerAdapter {

    private String[] tabTitles = new String[]{"Email","Phone"};
    private Context context;
    int totalTabs;

    @Override
    public int getCount() {
        return totalTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public Login_Ad(FragmentManager fm, Context context, int totalTabs){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                Login_Email_F login_email_f = new Login_Email_F();
                return login_email_f;
            case 1:
                Login_Phone_F login_phone_f = new Login_Phone_F();
                return login_phone_f;
            default:
                return null;
        }
    }

}
