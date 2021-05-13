package com.music.cloudish;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link See_Follower_Following_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class See_Follower_Following_F extends Fragment {

    TextView tv;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView iv;

    public See_Follower_Following_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see__follower__following_, container, false);
        String uname = getArguments().getString("username");
        int mode = getArguments().getInt("mode");
        tv=view.findViewById(R.id.fillusername);
        tabLayout=view.findViewById(R.id.tab_list);
        viewPager=view.findViewById(R.id.pager_follow);
        iv=view.findViewById(R.id.back);
        tv.setText(uname);
        tabLayout.addTab(tabLayout.newTab().setText("Following"));
        tabLayout.addTab(tabLayout.newTab().setText("Follower"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.main_purple), getResources().getColor(R.color.abuabu));
        tabLayout.setupWithViewPager(viewPager);

        final Follow_Ad adapter = new Follow_Ad(getActivity().getSupportFragmentManager(), getActivity(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (mode==1){
            tabLayout.getTabAt(0).select();
        }else {
            tabLayout.getTabAt(1).select();
        }




        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile_F ldf = new Profile_F();
                getFragmentManager().beginTransaction().replace(R.id.mainC, ldf).commit();
            }
        });

        return view;
    }
}