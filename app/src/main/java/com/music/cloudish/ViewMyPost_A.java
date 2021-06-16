package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import Adapter.PostRecyclerAdapter;
import Else.Post;

public class ViewMyPost_A extends AppCompatActivity {

    RecyclerView postRecycler;
    FloatingActionButton floating_btn;
    String userid;
    DatabaseReference mDatabase;
    PostRecyclerAdapter postRecyclerAdapter;
    TextView no_post;
    ArrayList<Post> postlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_my_post);

        // Hook
        postRecycler = findViewById(R.id.recyclerView);
        floating_btn = findViewById(R.id.add_btn);
        no_post = findViewById(R.id.no_post);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        postRecyclerAdapter = new PostRecyclerAdapter(postlist);
        postRecycler.setHasFixedSize(true);
        postRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
        postRecycler.setAdapter(postRecyclerAdapter);

        // Listener
        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewMyPost_A.this, AddPost_A.class);
                startActivity(i);
                finish();
            }
        });

        loadPost();
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