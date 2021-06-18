package com.music.cloudish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
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

import java.util.ArrayList;
import java.util.List;

import Adapter.AlbumRecyclerAdaptor;
import Adapter.AlbumSearchRecyclerAdaptor;
import Else.Album;

public class UserHome_Album_F extends Fragment {

    private String userid, currentuserid;
    RecyclerView albumRecycler;
    AlbumSearchRecyclerAdaptor albumRecyclerAdaptor;
    List<Pair<Album,String>> albumlist = new ArrayList<>();
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
        currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set Adapter
        albumRecycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        albumRecyclerAdaptor = new AlbumSearchRecyclerAdaptor(getContext(), albumlist);
        albumRecycler.setAdapter(albumRecyclerAdaptor);

        checkPrivate();

        return v;
    }

    private void loadAlbumData(){
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
                        albumlist.add(new Pair<>(album,userid));
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
                        loadAlbumData();
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
                    loadAlbumData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void waitingToBeAcc(){
        no_album.setVisibility(View.VISIBLE);
        no_album.setText("Waiting for accepted");
        albumRecycler.setVisibility(View.GONE);
    }

    private void notValid(){
        no_album.setVisibility(View.VISIBLE);
        no_album.setText("Follow this account first");
        albumRecycler.setVisibility(View.GONE);
    }

}