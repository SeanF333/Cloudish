package com.music.cloudish;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import Adapter.PostRecyclerAdapter;
import Else.Post;

public class Home_F extends Fragment {

    LinearLayout liked,featured,hits;
    RecyclerView rv;
    PostRecyclerAdapter postRecyclerAdapter;
    ArrayList<Post> postlist;
    List<String> liu2;
    DatabaseReference mDatabase;
    TextView nopost;
    ProgressBar pbf;

    public Home_F() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        liked = v.findViewById(R.id.liked);
        featured = v.findViewById(R.id.featured);
        hits = v.findViewById(R.id.hits);
        nopost=v.findViewById(R.id.no_post_feed);
        pbf=v.findViewById(R.id.pbfeeds);
        rv=v.findViewById(R.id.recPostFeeds);
        postlist=new ArrayList<>();
        liu2=new ArrayList<>();
        postRecyclerAdapter = new PostRecyclerAdapter(postlist,getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        rv.setAdapter(postRecyclerAdapter);

        DatabaseReference rrr = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rrr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    liu2.clear();
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if ((boolean)snapshot1.getValue()==true){
                            String userid;
                            userid=snapshot1.getKey().toString();
                            liu2.add(userid);
                        }

                    }

                    loadPost();
                }
            }
        });



        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Liked_Album_A.class);
                startActivity(i);
            }
        });

        featured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Featured_Album_A.class);
                startActivity(i);
            }
        });

        hits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Hits_Album_A.class);
                startActivity(i);
            }
        });

        return v;
    }

    private void loadPost() {
        DatabaseReference mPost = mDatabase.child("Posts");
        mPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    postlist.clear();
                    nopost.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
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

                        if (liu2.contains(poster_id)){

                            loadPosterData(post_id, post_url, caption, datetime, poster_id);
                        }

                    }
                    pbf.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    nopost.setVisibility(View.GONE);
                }else{
                    pbf.setVisibility(View.GONE);
                    nopost.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
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
                    Collections.sort(postlist, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return o2.getDatetime().compareTo(o1.getDatetime());
                        }
                    });
                    postRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}