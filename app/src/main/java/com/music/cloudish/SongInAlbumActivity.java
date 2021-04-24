package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.AlbumRecyclerAdaptor;
import Adapter.SongRecyclerAdaptor;
import Else.Album;
import Else.Global;
import Else.Song;

public class SongInAlbumActivity extends AppCompatActivity {

    TextView an,cat,count;
    ImageView back,del;
    Button addnew;
    ProgressDialog pd;
    DatabaseReference df;
    RecyclerView rv;
    SongRecyclerAdaptor adapter;
    List<Song> li;
    ProgressBar pbar;
    Boolean checkin=false;
    JcPlayerView jcp;
    ArrayList<JcAudio> jclist = new ArrayList<>();
    private int curr;
    ValueEventListener val;
    String albumname, cate;
    boolean isplay=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_in_album);

        albumname = getIntent().getStringExtra("an");
        cate = getIntent().getStringExtra("cat");
        if (Global.curAlbum.equals(albumname)){
            isplay=true;
        }
        an=findViewById(R.id.altit);
        cat=findViewById(R.id.cattext);
        count=findViewById(R.id.counttext);
        back=findViewById(R.id.backfromsong);
        del=findViewById(R.id.hapus);
        addnew=findViewById(R.id.songaddbutt);
        rv=findViewById(R.id.songrecycler);
        pbar=findViewById(R.id.pbarsong);
        jcp=findViewById(R.id.jcPlayer);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        li=new ArrayList<>();
        rv.setAdapter(adapter);
        adapter=new SongRecyclerAdaptor(getApplicationContext(), li, new SongRecyclerAdaptor.RecyclerItemClickListener() {
            @Override
            public void OnClickListener(Song s, int pos) {
                changeSelectedSong(pos);
                jcp.playAudio(jclist.get(pos));
                jcp.setVisibility(View.VISIBLE);
                jcp.createNotification();
            }
        });

        df= FirebaseDatabase.getInstance().getReference().child("Songs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        val=df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                li.clear();
                for (DataSnapshot dss: snapshot.getChildren()){
                    try {
//                        Toast.makeText(SongInAlbumActivity.this, dss.getKey(), Toast.LENGTH_LONG).show();
                        String album_name = dss.child("album_name").getValue().toString();
                        String artist = dss.child("artist").getValue().toString();
                        String songDuration = dss.child("songDuration").getValue().toString();
                        String songLink = dss.child("songLink").getValue().toString();
                        String songTitle = dss.child("songTitle").getValue().toString();
                        String songsCategory = dss.child("songsCategory").getValue().toString();
                        Song s = new Song(songsCategory, songTitle, artist,album_name,songDuration,songLink);
                        s.setmKey(dss.getKey());
                        curr=0;
                        if (album_name.equals(albumname)){
                            li.add(s);
                            checkin = true;
                            jclist.add(JcAudio.createFromURL(s.getSongTitle(),s.getSongLink()));
                        }


                    }catch (Exception e){
                        Toast.makeText(SongInAlbumActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                adapter.setSelectedPos(0);
                count.setText(String.valueOf(li.size()));
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                pbar.setVisibility(View.GONE);
                if (checkin){
                    jcp.initPlaylist(jclist, null);

                }else {
                    Toast.makeText(SongInAlbumActivity.this, "There is no song", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbar.setVisibility(View.GONE);
            }
        });



        an.setText(albumname);
        cat.setText(cate);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplay){
                    Intent i = new Intent(SongInAlbumActivity.this, MainHomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }else {
                    finish();
                }

            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SongInAlbumActivity.this, UploadSongActivity.class);
                i.putExtra("an", albumname);
                startActivity(i);
            }
        });


    }

    public void changeSelectedSong(int index){
        adapter.notifyItemChanged(adapter.getSelectedPos());
        curr=index;
        adapter.setSelectedPos(curr);
        adapter.notifyItemChanged(curr);
        Global.curAlbum=albumname;
        isplay=true;
    }

    @Override
    public void onBackPressed() {
        if (isplay){
            Intent i = new Intent(SongInAlbumActivity.this, MainHomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }else {
            finish();
        }

    }

}