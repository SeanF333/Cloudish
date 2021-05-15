package com.music.cloudish;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import Else.Global;

public class Search_Ad extends FragmentStatePagerAdapter {

    private String[] tabTitles = new String[]{"User","Album","Song"};
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


    public Search_Ad(FragmentManager fm, Context context, int totalTabs){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0:
                User_Result_F urf = new User_Result_F();
                return urf;
            case 1:
                Album_Result_F arf = new Album_Result_F();
                Global.arf=arf;
                return arf;
            case 2:
                Song_Result_F srf = new Song_Result_F();
                return srf;
            default:
                return null;
        }
    }

}
