package com.music.cloudish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.SongSearchRecyclerAdaptor;
import Adapter.UserSearchRecyclerAdaptor;
import Else.Album;
import Else.Song;
import Else.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Song_Result_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Song_Result_F extends Fragment {

    RecyclerView rv;
    DatabaseReference df;
    List<Pair<Pair<Song,String>,String>> lis;
    SongSearchRecyclerAdaptor ssra;
    EditText search;
    List<String> liu;

    public Song_Result_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_song__result_, container, false);
        rv=v.findViewById(R.id.song_search_result);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        lis=new ArrayList<>();
        liu=new ArrayList<>();
        ssra=new SongSearchRecyclerAdaptor(getContext(),lis);
        rv.setAdapter(ssra);
        search=getActivity().findViewById(R.id.search_columm);

        DatabaseReference rrr = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rrr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    liu.clear();
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if ((boolean)snapshot1.getValue()==true){
                            String userid;
                            userid=snapshot1.getKey().toString();
                            liu.add(userid);
                        }

                    }
                    if (search.getText().toString().equals("")){
                        readSongs();
                    }else {
                        searchSongs(search.getText().toString());
                    }
                }

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSongs(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void searchSongs(String s){
        if (s.equals("")){
            readSongs();
            return;
        }

        Query q = FirebaseDatabase.getInstance().getReference("Songs");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lis.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                        String album_name, artist, songDuration,userid, songLink, songTitle, songsCategory,imgLink;
                        album_name=snapshot2.child("album_name").getValue().toString();
                        artist=snapshot2.child("artist").getValue().toString();
                        songDuration=snapshot2.child("songDuration").getValue().toString();
                        songLink=snapshot2.child("songLink").getValue().toString();
                        songTitle=snapshot2.child("songTitle").getValue().toString();
                        songsCategory=snapshot2.child("songsCategory").getValue().toString();
                        imgLink=snapshot2.child("imgLink").getValue().toString();
                        userid=snapshot1.getKey().toString();
                        String sid = snapshot2.getKey().toString();
                        if (liu.contains(userid)){
                            if (songTitle.toLowerCase().startsWith(s.toLowerCase()) || album_name.toLowerCase().startsWith(s.toLowerCase()) || artist.toLowerCase().startsWith(s.toLowerCase())){
                                Song s = new Song(songsCategory,songTitle,artist,album_name,songDuration,songLink,imgLink);
                                Pair<Song,String> p1 = new Pair<>(s,sid);
                                Pair<Pair<Song,String>,String> p2 = new Pair<>(p1,userid);
                                lis.add(p2);
                            }

                        }

                    }



                }

                ssra.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readSongs(){
        Query q = FirebaseDatabase.getInstance().getReference("Songs");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search.getText().toString().equals("")){
                    lis.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                            String album_name, artist, songDuration,userid, songLink, songTitle, songsCategory,imgLink;
                            album_name=snapshot2.child("album_name").getValue().toString();
                            artist=snapshot2.child("artist").getValue().toString();
                            songDuration=snapshot2.child("songDuration").getValue().toString();
                            songLink=snapshot2.child("songLink").getValue().toString();
                            songTitle=snapshot2.child("songTitle").getValue().toString();
                            songsCategory=snapshot2.child("songsCategory").getValue().toString();
                            imgLink=snapshot2.child("imgLink").getValue().toString();
                            userid=snapshot1.getKey().toString();
                            String sid = snapshot2.getKey().toString();
                            if (liu.contains(userid)){
                                Song s = new Song(songsCategory,songTitle,artist,album_name,songDuration,songLink,imgLink);
                                Pair<Song,String> p1 = new Pair<>(s,sid);
                                Pair<Pair<Song,String>,String> p2 = new Pair<>(p1,userid);
                                lis.add(p2);


                            }

                        }



                    }

                    ssra.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}