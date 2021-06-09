package com.music.cloudish;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import Adapter.PostRecyclerAdapter;
import Else.Post;

public class UserHome_Post_F extends Fragment {

    RecyclerView postRecycler;
    DatabaseReference mPost;
    String userid;
    ArrayList<Post> postlist = new ArrayList<>();
    PostRecyclerAdapter postRecyclerAdapter;

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

        // Set Adapter
        postRecyclerAdapter = new PostRecyclerAdapter(postlist);
        postRecycler.setHasFixedSize(true);
        postRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        postRecycler.setAdapter(postRecyclerAdapter);

        return v;
    }
}