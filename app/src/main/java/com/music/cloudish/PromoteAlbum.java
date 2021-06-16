package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.PromoteAlbumRecyclerAdapter;
import Else.Album;
import Listener.AlbumListener;

public class PromoteAlbum extends AppCompatActivity implements AlbumListener {

    TextView no_album;
    RecyclerView album_rv;
    Button promote_btn;
    PromoteAlbumRecyclerAdapter albumRecyclerAdapter;
    ArrayList<Album> userAlbumList = new ArrayList<>();
    ArrayList<Album> selectedAlbumList = new ArrayList<>();
    String userid;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote_album);

        // Hook
        album_rv = findViewById(R.id.album_rv);
        promote_btn = findViewById(R.id.promote_btn);
        no_album = findViewById(R.id.no_album);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tombol promote akan muncul apabila ada album yang dipilih
        promote_btn.setVisibility(View.GONE);

        // RecyclerView Adapter
        albumRecyclerAdapter = new PromoteAlbumRecyclerAdapter(userAlbumList, this);
        album_rv.setHasFixedSize(true);
        album_rv.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        album_rv.setAdapter(albumRecyclerAdapter);

        // onClick Listener
        promote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoteSelectedAlbum();
            }
        });

        loadMyAlbum();
    }

    private void promoteSelectedAlbum() {
        List<Album> selectedAlbum = albumRecyclerAdapter.getSelectedAlbum();
        Integer selectedAlbumCount = selectedAlbum.size();



    }

    private void loadMyAlbum() {
        DatabaseReference mAlbum =  mDatabase.child("Album").child(userid);
        mAlbum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    album_rv.setVisibility(View.VISIBLE);
                    no_album.setVisibility(View.GONE);
                    userAlbumList.clear();
                    for(DataSnapshot s : snapshot.getChildren()){
                        Album album = s.getValue(Album.class);
                        album.setAlbumid(s.getKey());
                        album.setSelected(false);
                        album.setAlbumcategory(s.child("category").getValue().toString());
                        userAlbumList.add(album);
                        albumRecyclerAdapter.notifyDataSetChanged();
                    }
                }else{
                    no_album.setVisibility(View.VISIBLE);
                    album_rv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAlbumAction(Boolean isSelected) {
        if(isSelected){
            promote_btn.setVisibility(View.VISIBLE);
        }else{
            promote_btn.setVisibility(View.GONE);
        }
    }
}