package com.music.cloudish;

import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Adapter.ConcertRecyclerAdapter;
import Else.Concert;


public class UserHome_Concert_F extends Fragment {

    RecyclerView concertRecycler;
    DatabaseReference mConcert;
    String userid, currentuserid;
    ArrayList<Concert> concertlist = new ArrayList<>();
    ConcertRecyclerAdapter concertRecyclerAdapter;
    DatabaseReference mDatabase;
    String concert_id, concert_imageurl, concert_main_genre, concert_name, concert_description, concert_time;
    Integer concert_duration;
    TextView no_concert;

    public UserHome_Concert_F(String userid) {
        this.userid = userid;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_home__concert_, container, false);

        // Hook
        concertRecycler = v.findViewById(R.id.result_concert);
        no_concert = v.findViewById(R.id.no_concert);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set Adapter
        concertRecyclerAdapter = new ConcertRecyclerAdapter(concertlist);
        concertRecycler.setHasFixedSize(true);
        concertRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        concertRecycler.setAdapter(concertRecyclerAdapter);

        checkPrivate();

        return v;
    }

    private void checkPrivate() {

        DatabaseReference mUser = mDatabase.child("Users").child(userid);
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){{
                    String isPrivate = snapshot.child("private").getValue().toString();
                    if(isPrivate.equals("true")){
                        checkFollowing();
                    }else{
                        loadConcert();
                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkFollowing() {
        DatabaseReference mFollowing = mDatabase.child("Following").child(currentuserid);
        mFollowing.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(userid).exists()){
                    notValid();
                }else if((boolean)snapshot.child(userid).getValue()==false){
                    waitingToBeAcc();
                }else{
                    loadConcert();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void waitingToBeAcc(){
        no_concert.setVisibility(View.VISIBLE);
        no_concert.setText("Waiting for accepted");
        concertRecycler.setVisibility(View.GONE);
    }

    private void notValid(){
        no_concert.setVisibility(View.VISIBLE);
        no_concert.setText("Follow this account first");
        concertRecycler.setVisibility(View.GONE);
    }

    private void loadConcert() {

        DatabaseReference mConcert = mDatabase.child("Concerts").child(userid);
        concertlist.clear();
        mConcert.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    if(snapshot.getChildrenCount() == 0){
                        no_concert.setVisibility(View.VISIBLE);
                        concertRecycler.setVisibility(View.GONE);
                        return;
                    }
                    concertRecycler.setVisibility(View.VISIBLE);
                    for(DataSnapshot s : snapshot.getChildren()){
                        concert_id = s.getKey();
                        concert_description = s.child("description").getValue().toString();
                        concert_duration = Integer.valueOf(s.child("duration").getValue().toString());
                        concert_imageurl = s.child("imageurl").getValue().toString();
                        concert_main_genre = s.child("main_genre").getValue().toString();
                        concert_name = s.child("name").getValue().toString();
                        concert_time = s.child("time").getValue().toString();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                        Date concert_date = null;
                        try {
                            concert_date = dateFormat.parse(s.child("date").getValue().toString()+ " " +concert_time);
                        } catch (ParseException e) {
                            Log.d("Error parse date", "Error parse date");
                            e.printStackTrace();
                        }

                        Concert concert = new Concert(concert_id, concert_imageurl, concert_main_genre, concert_name, concert_description, concert_date, concert_time, userid, concert_duration);
                        concertlist.add(concert);
                        concertRecyclerAdapter.notifyDataSetChanged();
                    }
                }else{
                    no_concert.setVisibility(View.VISIBLE);
                    concertRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}