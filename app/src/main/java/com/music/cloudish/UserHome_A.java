package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Else.Notification;

public class UserHome_A extends AppCompatActivity {

    ImageView artist_img;
    Toolbar name_toolbar;
    TextView post_count, follower_count, following_count;
    TabLayout tabLayout;
    ViewPager viewPager;
    String _artistid, _followercount, _followingcount, currentuserid, isPrivate;
    ProgressDialog pd;
    DatabaseReference df;
    Button follow_btn;
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
        follow_btn = findViewById(R.id.follow_btn);
        nestedScrollView = findViewById(R.id.nestedscrollview);
        _artistid = getIntent().getStringExtra("artist_id");
        isPrivate = getIntent().getStringExtra("artist_private");
        currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("isUserPrivate", isPrivate +" "+_artistid);

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
        isFollowing();

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followBtnPressed();
            }
        });


        fillArtistInformation();
    }

    private void followBtnPressed() {
        String follow_btn_str = follow_btn.getText().toString();
        if(follow_btn_str.equals("Follow")){
            if(isPrivate.equals("true")){
                FirebaseDatabase.getInstance().getReference().child("Following").child(currentuserid).child(_artistid).setValue(false);
                Notification n = new Notification(_artistid,currentuserid,"","","0");
                DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Notification").child(_artistid);
                String uploadid=dff.push().getKey();
                dff.child(uploadid).setValue(n);
                follow_btn.setText("Requested");
            }else{

                FirebaseDatabase.getInstance().getReference().child("Following").child(currentuserid).child(_artistid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference().child("Follower").child(_artistid).child(currentuserid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
                                        d.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    String uname = task.getResult().child("username").getValue().toString();
                                                    Notification n = new Notification(_artistid,currentuserid,uname+" has starting to follow you.","","2");
                                                    DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Notification").child(_artistid);
                                                    String uploadid=dff.push().getKey();
                                                    dff.child(uploadid).setValue(n);
                                                    refreshPages();
                                                }else {
                                                    Toast.makeText(UserHome_A.this,"Error",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(UserHome_A.this,"Error, Please Try Again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(UserHome_A.this,"Error, Please Try Again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else if(follow_btn_str.equals("Followed")){
            AlertDialog.Builder builder = new AlertDialog.Builder(UserHome_A.this);
            builder.setCancelable(true);
            builder.setTitle("Confirmation");
            builder.setMessage("You may have to sent follow request again if you Unfollow this user, are you sure?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDialog pd = new ProgressDialog(UserHome_A.this);
                            pd.show();
                            DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Following").child(currentuserid).child(_artistid);
                            df.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Follower").child(_artistid).child(currentuserid);
                                        dff.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Query q = FirebaseDatabase.getInstance().getReference().child("Notification").child(_artistid).orderByChild("publisherid").equalTo(currentuserid);
                                                    q.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                for(DataSnapshot dsnap : task.getResult().getChildren()){
                                                                    String messagee = dsnap.child("text").getValue().toString();
                                                                    if (!messagee.endsWith("starting to follow you.")){
                                                                        continue;
                                                                    }
                                                                    dsnap.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            pd.dismiss();
                                                                            follow_btn.setText("Follow");
                                                                            refreshPages();
                                                                        }
                                                                    });
                                                                }
                                                            }else {
                                                                pd.dismiss();
                                                                Toast.makeText(UserHome_A.this,"Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }else {
                                                    pd.dismiss();
                                                    Toast.makeText(UserHome_A.this,"Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(UserHome_A.this,"Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Following").child(currentuserid).child(_artistid);
            df.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Query q = FirebaseDatabase.getInstance().getReference().child("Notification").child(_artistid).orderByChild("publisherid").equalTo(currentuserid);
                        q.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful())     {
                                    for(DataSnapshot dsnap:task.getResult().getChildren()){
                                        String mode = dsnap.child("mode").getValue().toString();
                                        if(mode.equals("0")){
                                            continue;
                                        }
                                        dsnap.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                follow_btn.setText("Follow");
                                                refreshPages();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }else{
                        pd.dismiss();
                    }
                }
            });
        }
    }

    public void isFollowing(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Following").child(currentuserid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child(_artistid).exists()){
                    follow_btn.setText("Follow");
                }else if ((boolean)snapshot.child(_artistid).getValue()==false){
                    follow_btn.setText("Requested");
                }else {
                    follow_btn.setText("Followed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
//                  Log.d("UserHomeActivityURL", url);
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
                                            DatabaseReference mPost = FirebaseDatabase.getInstance().getReference().child("Posts");
                                            mPost.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        DataSnapshot posts = task.getResult();
                                                        Integer counter = 0;
                                                        for(DataSnapshot st : posts.getChildren()){
                                                            if(st.child("poster_id").getValue().toString().equals(_artistid)){
                                                                counter++;
                                                            }
                                                        }
                                                        post_count.setText(String.valueOf(counter));
                                                        pd.dismiss();
                                                    }else{
                                                        pd.dismiss();
                                                    }
                                                }
                                            });

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
                    pd.dismiss();
                    Toast.makeText(UserHome_A.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }

    private void refreshPages(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}