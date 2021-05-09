package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Adapter.AlbumRecyclerAdaptor;
import Adapter.SongRecyclerAdaptor;
import Else.Album;
import Else.Global;
import Else.Song;

public class SongInAlbumActivity extends AppCompatActivity {

    TextView an,cat,count;
    ImageView back,del,yes,no;
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
    int mode=0;

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
        yes=findViewById(R.id.hapusfix);
        no=findViewById(R.id.backfromdelete);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        li=new ArrayList<>();
        rv.setAdapter(adapter);
        adapter=new SongRecyclerAdaptor(getApplicationContext(), li, new SongRecyclerAdaptor.RecyclerItemClickListener() {
            @Override
            public void OnClickListener(Song s, int pos) {
                if (mode==0){
                    changeSelectedSong(pos);
                    jcp.playAudio(jclist.get(pos));
                    jcp.setVisibility(View.VISIBLE);
                    jcp.createNotification();
                }else {

                }

            }
        });

        df= FirebaseDatabase.getInstance().getReference().child("Songs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        val=df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                li.clear();
                jclist.clear();
                for (DataSnapshot dss: snapshot.getChildren()){
                    try {
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
                            jclist.add(JcAudio.createFromURL(songTitle,s.getSongLink()));
                        }


                    }catch (Exception e){
                        Toast.makeText(SongInAlbumActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                adapter.setSelectedPos(-1);
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

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SongInAlbumActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Delete");
                builder.setMessage("What do you want to do?");
                builder.setPositiveButton("Delete Music",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                jcp.pause();
                                mode = 1;
                                adapter.resetarr();
                                adapter.setMode(1);
                                adapter.notifyDataSetChanged();
                                yes.setVisibility(View.VISIBLE);
                                no.setVisibility(View.VISIBLE);
                                del.setVisibility(View.GONE);
                                back.setVisibility(View.INVISIBLE);
                            }
                        });
                builder.setNegativeButton("Delete Entire Album", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder b = new AlertDialog.Builder(SongInAlbumActivity.this);
                        b.setCancelable(true);
                        b.setTitle("Confirmation");
                        b.setMessage("Are you sure want to delete "+albumname+" album?");
                        b.setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        jcp.pause();
                                        ProgressDialog pdd = new ProgressDialog(SongInAlbumActivity.this);
                                        pdd.setMessage("Please Wait");
                                        pdd.show();
                                        DatabaseReference musicRef = FirebaseDatabase.getInstance().getReference("Songs").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                        musicRef.orderByChild("album_name").equalTo(albumname).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getChildrenCount()>0){
                                                    for(DataSnapshot dataSnapshot :  snapshot.getChildren())
                                                    {
                                                        String link = dataSnapshot.child("songLink").getValue().toString();
                                                        dataSnapshot.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    StorageReference fs = FirebaseStorage.getInstance().getReferenceFromUrl(link);
                                                                    fs.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){

                                                                                DatabaseReference albumRef = FirebaseDatabase.getInstance().getReference("Album").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                                                                albumRef.orderByChild("albumname").equalTo(albumname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for(DataSnapshot dataSnapshot :  snapshot.getChildren())
                                                                                        {
                                                                                            dataSnapshot.getRef().setValue(null);
                                                                                        }
                                                                                        pdd.dismiss();
                                                                                        finish();
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }else {
                                                                    pdd.dismiss();
                                                                    Toast.makeText(SongInAlbumActivity.this, "Error deleting music", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                    }
                                                }else {
                                                    DatabaseReference albumRef = FirebaseDatabase.getInstance().getReference("Album").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                                    albumRef.orderByChild("albumname").equalTo(albumname).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot dataSnapshot :  snapshot.getChildren())
                                                            {
                                                                dataSnapshot.getRef().setValue(null);
                                                            }
                                                            pdd.dismiss();
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                        b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog d = b.create();
                        d.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode=0;
                adapter.setMode(0);
                adapter.notifyDataSetChanged();
                yes.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                del.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
                List<String> a = adapter.getArrno();
                if (a.size()>0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SongInAlbumActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure want to delete these "+a.size()+" music(s)?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ProgressDialog pdd = new ProgressDialog(SongInAlbumActivity.this);
                                    pdd.setMessage("Please Wait");
                                    pdd.show();
                                    for (String s: a) {
                                        DatabaseReference musicRef = FirebaseDatabase.getInstance().getReference("Songs").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                        musicRef.orderByChild("songLink").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot :  snapshot.getChildren())
                                                {
                                                    dataSnapshot.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                StorageReference fs = FirebaseStorage.getInstance().getReferenceFromUrl(s);
                                                                fs.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            pdd.dismiss();
                                                                            finish();
                                                                            overridePendingTransition(0, 0);
                                                                            startActivity(getIntent());
                                                                            overridePendingTransition(0, 0);
                                                                        }
                                                                    }
                                                                });
                                                            }else {
                                                                pdd.dismiss();
                                                                Toast.makeText(SongInAlbumActivity.this, "Error deleting music", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }

                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Toast.makeText(SongInAlbumActivity.this, "No music selected", Toast.LENGTH_SHORT).show();
                }
                mode=0;
                adapter.setMode(0);
                adapter.notifyDataSetChanged();
                yes.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                del.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
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
            Global.flib.getAdapter().notifyDataSetChanged();
            Intent i = new Intent(SongInAlbumActivity.this, MainHomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }else {
            finish();
        }

    }

}