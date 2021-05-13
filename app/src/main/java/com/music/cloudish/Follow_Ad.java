package com.music.cloudish;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class Follow_Ad extends FragmentStatePagerAdapter {
    private String[] tabTitles = new String[]{"Following","Follower"};
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


    public Follow_Ad(FragmentManager fm, Context context, int totalTabs){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                Following_Result_F frag = new Following_Result_F();
                return frag;
            case 1:
                Follower_Result_F frag1 = new Follower_Result_F();
                return frag1;
            default:
                return null;
        }
    }

}
