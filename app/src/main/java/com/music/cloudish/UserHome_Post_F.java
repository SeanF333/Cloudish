package com.music.cloudish;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import Adapter.PostRecyclerAdapter;
import Else.Post;

public class UserHome_Post_F extends Fragment {

    RecyclerView postRecycler;
    DatabaseReference mPost;
    String userid, currentuserid;
    ArrayList<Post> postlist = new ArrayList<>();
    PostRecyclerAdapter postRecyclerAdapter;
    DatabaseReference mDatabase;
    TextView no_post;

    public UserHome_Post_F(String userid) {
        this.userid = userid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_home__post_, container, false);

        //Hook
        postRecycler = v.findViewById(R.id.result_post);
        no_post = v.findViewById(R.id.no_post);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set Adapter
        postRecyclerAdapter = new PostRecyclerAdapter(postlist,getContext());
        postRecycler.setHasFixedSize(true);
        postRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        postRecycler.setAdapter(postRecyclerAdapter);

        checkFollowing();

        return v;
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
                    loadPost();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void waitingToBeAcc(){
        no_post.setVisibility(View.VISIBLE);
        no_post.setText("Waiting for accepted");
        postRecycler.setVisibility(View.GONE);
    }

    private void notValid(){
        no_post.setVisibility(View.VISIBLE);
        no_post.setText("Follow this account first");
        postRecycler.setVisibility(View.GONE);
    }

    private void loadPost() {
        DatabaseReference mPost = mDatabase.child("Posts");
        mPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    postlist.clear();
                    no_post.setVisibility(View.VISIBLE);
                    postRecycler.setVisibility(View.GONE);
                    for(DataSnapshot s : snapshot.getChildren()){
                        String post_url, caption, datetime, poster_id, post_id;
                        Map<String, Object> map = (Map<String, Object>)s.getValue();
                        // ambil info dari db
                        post_id = s.getKey();
                        Log.d("postKeynih1",post_id);
                        post_url = map.get("imageurl").toString();
                        caption = map.get("caption").toString();
                        datetime = map.get("datetime").toString();
                        poster_id = map.get("poster_id").toString();

                        if(poster_id.equals(userid)){
                            postRecycler.setVisibility(View.VISIBLE);
                            no_post.setVisibility(View.GONE);
                            loadPosterData(post_id, post_url, caption, datetime, poster_id);
                        }
                    }
                }else{
                    no_post.setVisibility(View.VISIBLE);
                    postRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadPosterData(String post_id, String post_url, String caption, String datetime, String poster_id) {

        DatabaseReference mUser = mDatabase.child("Users").child(poster_id);
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String poster_name, poster_imageurl;
                    poster_name = snapshot.child("username").getValue().toString();
                    poster_imageurl = snapshot.child("imageurl").getValue().toString();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime localDateTime = LocalDateTime.parse(datetime, dtf);
                    Post post = new Post(post_id, post_url, caption, poster_id, localDateTime, poster_imageurl, poster_name);
                    postlist.add(post);
                    postRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}