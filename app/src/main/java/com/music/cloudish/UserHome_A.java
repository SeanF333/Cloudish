package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UserHome_A extends AppCompatActivity {

    ImageView artist_img;
    Toolbar name_toolbar;
    TextView post_count, follower_count, following_count;
    TabLayout tabLayout;
    ViewPager viewPager;
    String _artistid, _followercount, _followingcount;
    ProgressDialog pd;
    DatabaseReference df;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_);

        //Hook
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        name_toolbar = findViewById(R.id.name_toolbar);
        artist_img = findViewById(R.id.artist_img);
        post_count = findViewById(R.id.post_count);
        follower_count = findViewById(R.id.followers_count);
        following_count = findViewById(R.id.following_count);
        nestedScrollView = findViewById(R.id.nestedscrollview);
        _artistid = getIntent().getStringExtra("artist_id");

        // Mengisi Tab Layout

        tabLayout.addTab(tabLayout.newTab().setText("Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));
        tabLayout.addTab(tabLayout.newTab().setText("Concert"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.main_purple), getResources().getColor(R.color.pop_orange));

        // Optional
        nestedScrollView.setFillViewport (true);

        final UserHome_Ad adapter = new UserHome_Ad(getSupportFragmentManager(), this, tabLayout.getTabCount(), _artistid);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

        fillArtistInformation();
    }

    private void fillArtistInformation() {
        pd = new ProgressDialog(UserHome_A.this);
        pd.setMessage("Please Wait");
        pd.show();

        Log.d("UserHomeActivity", _artistid);

        df = FirebaseDatabase.getInstance().getReference().child("Users").child(_artistid);
        df.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().child("imageurl").getValue().toString();
                    Log.d("UserHomeActivityURL", url);
                    Glide.with(getApplication()).load(url).into(artist_img);
                    name_toolbar.setTitle('@' + task.getResult().child("username").getValue().toString());
                    DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Follower").child(_artistid);
                    dff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot ds = task.getResult();
                                long count1 = ds.getChildrenCount();
                                follower_count.setText(String.valueOf(count1));
                                Query dfff = FirebaseDatabase.getInstance().getReference().child("Following").child(_artistid);
                                dfff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DataSnapshot dss = task.getResult();
                                            long count2 = 0;
                                            for (DataSnapshot snapshot1 : task.getResult().getChildren()) {
                                                if ((boolean) snapshot1.getValue() == true) {
                                                    count2++;
                                                }
                                            }
                                            following_count.setText(String.valueOf(count2));
                                            pd.dismiss();
                                        } else {
                                            pd.dismiss();
                                            Toast.makeText(UserHome_A.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                pd.dismiss();
                                Toast.makeText(UserHome_A.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    pd.dismiss();
                } else {
                    Toast.makeText(UserHome_A.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}