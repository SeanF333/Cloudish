package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import Adapter.UserRecyclerAdapter;
import Else.Performance;
import Else.Song;
import Else.User;
import Listener.SongListener;
import Listener.UserListener;

public class AddConcert_Second_A extends AppCompatActivity implements UserListener, SongListener {

    Button buttonDone, buttonAdd;
    RecyclerView recyclerViewTrack, recyclerViewUser;
    TextView textViewSeePlaylist;
    Dialog dialog;
    SearchView searchViewUser, searchViewTrack;
    Uri imageUri;
    String concertName, concertMainGenre, concertDescription, concertDuration, concertDate, concertTime;
    ProgressDialog pd;

    ArrayList<User> userSearchList = new ArrayList<>();
    ArrayList<Song> songlist = new ArrayList<>();
    ArrayList<User> userlist = new ArrayList<>();
    ArrayList<Performance> performances = new ArrayList<>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concert__second_);

        // Hook
        buttonDone = findViewById(R.id.button_done);
        buttonAdd = findViewById(R.id.buttonAdd);
        textViewSeePlaylist = findViewById(R.id.textViewPlaylist);
        recyclerViewUser = findViewById(R.id.recylcerViewUser);
        recyclerViewTrack = findViewById(R.id.recyclerViewTrack);
        searchViewUser = findViewById(R.id.searchViewUser);
        searchViewTrack = findViewById(R.id.searchViewTrack);
        pd = new ProgressDialog(this);


        // Get Information from Previous Intent
        concertName = getIntent().getStringExtra("concertName");
        concertName = getIntent().getStringExtra("concertName");
        concertMainGenre = getIntent().getStringExtra("concertMainGenre");
        concertDescription = getIntent().getStringExtra("concertDescription");
        concertDuration = getIntent().getStringExtra("concertDuration");
        concertDate = getIntent().getStringExtra("concertDate");
        concertTime = getIntent().getStringExtra("concertTime");
        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        // Listener
        searchViewUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchUser(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchUser(newText);
                return true;
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddtoPlaylist();
            }
        });

        textViewSeePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPlaylist();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveConcertInformation();
            }
        });

        // Isi dulu RecyclerView

        pd.setMessage("Please Wait");
        pd.show();


        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){

                        String id = ds.getKey();
                        String email = ds.child("email").getValue().toString();
                        String fullname = ds.child("fullname").getValue().toString();
                        String imageurl = ds.child("imageurl").getValue().toString();
                        String phone = ds.child("phone").getValue().toString();
                        String isprivate = ds.child("private").getValue().toString();
                        String username = ds.child("username").getValue().toString();

                        User u = new User(id, email, fullname, imageurl, phone, isprivate, username);

                        userSearchList.add(u);
                    }

                    UserRecyclerAdapter userRecyclerAdapter = new UserRecyclerAdapter(userSearchList, AddConcert_Second_A.this);
                    recyclerViewUser.setHasFixedSize(true);
                    recyclerViewUser.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
                    recyclerViewUser.setAdapter(userRecyclerAdapter);

                    pd.dismiss();

                }else{
                    pd.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("AddConcertSecond", error.getMessage());
            }
        });
    }

    private void SaveConcertInformation() {
    }

    private void ShowPlaylist() {
    }

    private void AddtoPlaylist() {

    }

    private void SearchUser(String query) {

        pd.show();

        ArrayList<User> current_user_search_list = new ArrayList<>();

        for(User user : userSearchList){
            if(user.getFullname().toLowerCase().contains(query.toLowerCase())){
                current_user_search_list.add(user);
            }
        }

        UserRecyclerAdapter userRecyclerAdapter = new UserRecyclerAdapter(current_user_search_list, AddConcert_Second_A.this);
        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
        recyclerViewUser.setAdapter(userRecyclerAdapter);

        pd.dismiss();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onSongAction(Boolean isSelected) {

    }

    @Override
    public void onUserAction(Boolean isSelected) {

    }
}