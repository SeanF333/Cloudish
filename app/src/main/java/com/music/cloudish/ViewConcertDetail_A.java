package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapter.SongRecylerAdapter;
import Adapter.UserRecyclerAdapter;
import Else.Song;
import Else.User;
import Listener.SongListener;
import Listener.UserListener;

public class ViewConcertDetail_A extends AppCompatActivity implements UserListener, SongListener {

    private static String concertId;
    private ImageView concertImage;
    private TextView concertNameText, concertTimeText, concertDateText, concertDurationText, concertDetailText;
    private String concertName, concertTime, concertDate, concertDuration, concertDetail, concertMainGenre, concertImageUrl,maderId;
    private FloatingActionButton btnLove;
    private RecyclerView artistRecyclerView, songRecyclerView;
    private DatabaseReference mDatabase;
    private ArrayList<User> artistArrayList = new ArrayList<>();
    private ArrayList<Song> songArrayList = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    UserRecyclerAdapter userRecyclerAdapter;
    SongRecylerAdapter songRecyclerAdapter;
    ProgressDialog pd;
    Map<String, ArrayList<String>> performanceList = new HashMap<String, ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_concert_detail_);

        // Hook
        concertId = getIntent().getStringExtra("concertId");
        maderId = getIntent().getStringExtra("concertMadeBy");
        concertNameText = findViewById(R.id.txtConcertName);
        concertDateText = findViewById(R.id.txtDate);
        concertDurationText = findViewById(R.id.txtDuration);
        concertTimeText = findViewById(R.id.txtTime);
        concertDetailText = findViewById(R.id.txtConcertDetail);
        concertImage = findViewById(R.id.imgConcertImage);
        artistRecyclerView = findViewById(R.id.recyclerViewArtist);
        songRecyclerView = findViewById(R.id.recyclerViewSong);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait");

        loadConcertInformation();
    }

    private void loadConcertInformation() {

        DatabaseReference mConcert = mDatabase.child("Concerts").child(maderId).child(concertId);
        mConcert.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>)snapshot.getValue();

                    // ambil info dari db
                    concertName = map.get("name").toString();
                    concertTime = map.get("time").toString();
                    concertDetail = map.get("description").toString();
                    concertDuration = map.get("duration").toString();
                    concertMainGenre = map.get("main_genre").toString();
                    concertDate = map.get("date").toString();
                    concertImageUrl = map.get("imageurl").toString();

                    // masukin info ke komponen
                    concertNameText.setText(concertName);
                    concertDetailText.setText(concertDetail);
                    concertDateText.setText(concertDate);
                    concertTimeText.setText(concertTime);
                    concertDurationText.setText(concertDuration);

                    if(concertImageUrl != null){
                        Glide.with(getApplication()).load(concertImageUrl).into(concertImage);
                    }

                    userRecyclerAdapter = new UserRecyclerAdapter(artistArrayList, ViewConcertDetail_A.this);
                    artistRecyclerView.setHasFixedSize(true);
                    artistRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
                    artistRecyclerView.setAdapter(userRecyclerAdapter);

                    songRecyclerAdapter = new SongRecylerAdapter(songArrayList, ViewConcertDetail_A.this);
                    songRecyclerView.setHasFixedSize(true);
                    songRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
                    songRecyclerView.setAdapter(songRecyclerAdapter);

                    loadPlaylist();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });


    }

    private void loadPlaylist() {

        DatabaseReference mPlaylist = mDatabase.child("Playlists").child(concertId);
        mPlaylist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getKey();
                        userIdList.add(id);
                        performanceList.put(id,new ArrayList<String>());
                        for(DataSnapshot s : ds.child("tracklist").getChildren()){
                            String songid = s.getKey();
                            performanceList.get(id).add(songid);
                        }
                    }
                    loadArtist();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadArtist(){

            for(String userId : userIdList){
                DatabaseReference mArtist = mDatabase.child("Users").child(userId);
                mArtist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            DataSnapshot ds = snapshot;
                            String id = ds.getKey();
                            String email = ds.child("email").getValue().toString();
                            String fullname = ds.child("fullname").getValue().toString();
                            String imageurl = ds.child("imageurl").getValue().toString();
                            String phone = ds.child("phone").getValue().toString();
                            String isprivate = ds.child("private").getValue().toString();
                            String username = ds.child("username").getValue().toString();
                            User u = new User(id, email, fullname, imageurl, phone, isprivate, username);
                            artistArrayList.add(u);
                            userRecyclerAdapter.notifyDataSetChanged();

                            ArrayList<String> currentSongList = performanceList.get(userId);
                            for(String song_string : currentSongList){
                                Log.d("ViewConcertDetail_A", "id lagu : "+song_string+"id penyanyi"+userId);
                                DatabaseReference mSong = mDatabase.child("Songs").child(userId).child(song_string);
                                mSong.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            Log.d("Lagu ketemu ini", snapshot.getKey());
                                            String album_name, artist, imgLink, songDuration, songLink, songTitle, songCategory;
                                            album_name = snapshot.child("album_name").getValue().toString();
                                            artist = snapshot.child("artist").getValue().toString();
                                            imgLink = snapshot.child("imgLink").getValue().toString();
                                            songDuration = snapshot.child("songDuration").getValue().toString();
                                            songLink = snapshot.child("songLink").getValue().toString();
                                            songTitle = snapshot.child("songTitle").getValue().toString();
                                            songCategory = snapshot.child("songsCategory").getValue().toString();

                                            Song current_song = new Song(songCategory, songTitle, artist, album_name, songDuration, songLink, imgLink);
                                            songArrayList.add(current_song);
                                            songRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        return;
                    }
                });
            }
    }

    @Override
    public void onUserAction(Boolean isSelected) {

    }

    public static String getConcertId() {
        return concertId;
    }

    @Override
    public void onSongAction(Boolean isSelected) {

    }
}