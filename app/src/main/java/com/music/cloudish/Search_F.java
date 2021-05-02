package com.music.cloudish;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search_F extends Fragment {


    TabLayout tabLayout;
    ViewPager viewPager;

    public Search_F() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_, container, false);

        tabLayout=v.findViewById(R.id.tab_search);
        viewPager=v.findViewById(R.id.pager_search);

        tabLayout.addTab(tabLayout.newTab().setText("User"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Song"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.main_purple), getResources().getColor(R.color.abuabu));
        tabLayout.setupWithViewPager(viewPager);

        final Search_Ad adapter = new Search_Ad(getActivity().getSupportFragmentManager(), getActivity(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        return v;
    }
}