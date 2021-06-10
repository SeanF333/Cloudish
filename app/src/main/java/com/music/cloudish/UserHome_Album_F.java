package com.music.cloudish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.AlbumRecyclerAdaptor;
import Else.Album;

public class UserHome_Album_F extends Fragment {

    private String userid;
    RecyclerView albumRecycler;
    AlbumRecyclerAdaptor albumRecyclerAdaptor;
    ArrayList<Album> albumlist = new ArrayList<>();
    DatabaseReference mDatabase;
    TextView no_album;;


    public UserHome_Album_F(String userid) {
        this.userid = userid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user_home__album_, container, false);

        //Hook
        albumRecycler = v.findViewById(R.id.result_album);
        no_album = v.findViewById(R.id.no_album);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set Adapter
        albumRecycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        albumRecyclerAdaptor = new AlbumRecyclerAdaptor(getContext(), albumlist);
        albumRecycler.setAdapter(albumRecyclerAdaptor);

        // Load album info from database
        DatabaseReference mAlbum =  mDatabase.child("Album").child(userid);
        mAlbum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    albumRecycler.setVisibility(View.VISIBLE);
                    no_album.setVisibility(View.GONE);
                    albumlist.clear();
                    for(DataSnapshot s : snapshot.getChildren()){
                        Album album = s.getValue(Album.class);
                        album.setAlbumcategory(s.child("category").getValue().toString());
                        albumlist.add(album);
                        albumRecyclerAdaptor.notifyDataSetChanged();
                    }
                }else{
                    no_album.setVisibility(View.VISIBLE);
                    albumRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }
}