package com.music.cloudish;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class UserHome_Ad extends FragmentPagerAdapter {

    private String[] tabTitle = new String[]{"Post","Album","Concert"};
    private Context context;
    int totalTabs;
    private String userid;

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }

    public UserHome_Ad(@NonNull FragmentManager fm,  Context context, int totalTabs, String userid) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.totalTabs = totalTabs;
        this.userid = userid;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                UserHome_Post_F userHome_post_f = new UserHome_Post_F(userid);
                return userHome_post_f;
            case 1:
                UserHome_Album_F userHome_album_f = new UserHome_Album_F();
                return userHome_album_f;
            case 2:
                UserHome_Concert_F userHome_concert_f = new UserHome_Concert_F();
                return userHome_concert_f;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
