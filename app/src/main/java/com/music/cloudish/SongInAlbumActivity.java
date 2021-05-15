package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.jcplayer.JcPlayerManager;
import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import Adapter.AlbumRecyclerAdaptor;
import Adapter.SongRecyclerAdaptor;
import Else.Album;
import Else.Global;
import Else.Notification;
import Else.Song;

public class SongInAlbumActivity extends AppCompatActivity {

    LinearLayout ll;
    TextView an,cat,count,own;
    ImageView back,del,yes,no,love,unlove;
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
    int modedisplay=0;
    String uidowner;

    public ViewGroup getParent(View view) {
        return (ViewGroup)view.getParent();
    }

    public void removeView(View view) {
        ViewGroup parent = getParent(view);
        if(parent != null) {
            parent.removeView(view);
        }
    }

    public void replaceView(View currentView, View newView) {
        ViewGroup parent = getParent(currentView);
        if(parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_in_album);

        if (getIntent().getStringExtra("albummode")!=null){
            modedisplay=Global.modealbum;
        }else {
            modedisplay=Global.modealbumglobal;
        }
        ll=findViewById(R.id.owneralbum);
        an=findViewById(R.id.altit);
        cat=findViewById(R.id.cattext);
        count=findViewById(R.id.counttext);
        own=findViewById(R.id.ownertext);
        love=findViewById(R.id.btnLove);
        unlove=findViewById(R.id.btnUnlove);
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


        if (modedisplay==1){
            if (getIntent().getStringExtra("kode")!=null){
                albumname = Global.album;
                cate = Global.cat;
            }
            else {
                albumname = Global.extAlbum;
                cate = Global.extCat;
            }

            if (getIntent().getStringExtra("uid")!=null){
                uidowner=getIntent().getStringExtra("uid");
            }else {
                uidowner=Global.ownerUser;
            }

            if (getIntent().getStringExtra("songid")!=null){
                Global.songid=getIntent().getStringExtra("songid");
                Global.innermode=1;
            }else {
                Global.innermode=0;
            }

            DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("Like").child(uidowner).child(albumname).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            fd.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            love.setVisibility(View.GONE);
                            unlove.setVisibility(View.VISIBLE);
                        }else {
                            love.setVisibility(View.VISIBLE);
                            unlove.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(SongInAlbumActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            del.setVisibility(View.GONE);
            addnew.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);

            DatabaseReference uref = FirebaseDatabase.getInstance().getReference().child("Users").child(uidowner);
            uref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()){
                        String uname;
                        uname=task.getResult().child("username").getValue().toString();
                        own.setText(uname);
                        if (Global.extAlbum.equals(albumname) && Global.innermode!=1){
                            isplay=true;
                            pbar.setVisibility(View.GONE);
                            replaceView(jcp,Global.jcpg);
                            jcp.setVisibility(View.VISIBLE);
                            li=Global.li;
                            adapter=Global.adapter;
                            rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else {
                            adapter=new SongRecyclerAdaptor(getApplicationContext(), li, new SongRecyclerAdaptor.RecyclerItemClickListener() {
                                @Override
                                public void OnClickListener(Song s, int pos) {
                                    if (mode==0){
                                        changeSelectedSong(pos);
                                        Global.jcpg=jcp;
                                        Global.adapter=adapter;
                                        Global.li=li;
                                        jcp.playAudio(jclist.get(pos));
                                        jcp.setVisibility(View.VISIBLE);
                                        jcp.setJcPlayerManagerListener(new JcPlayerManagerListener() {
                                            @Override
                                            public void onPreparedAudio(@NotNull JcStatus jcStatus) {
                                                List<JcAudio> templi = jcp.getMyPlaylist();
                                                int idx = templi.indexOf(jcp.getCurrentAudio());
                                                adapter.setSelectedPos(idx);
                                                adapter.notifyDataSetChanged();

                                            }

                                            @Override
                                            public void onCompletedAudio() {

                                            }

                                            @Override
                                            public void onPaused(@NotNull JcStatus jcStatus) {

                                            }

                                            @Override
                                            public void onContinueAudio(@NotNull JcStatus jcStatus) {

                                            }

                                            @Override
                                            public void onPlaying(@NotNull JcStatus jcStatus) {

                                            }

                                            @Override
                                            public void onTimeChanged(@NotNull JcStatus jcStatus) {

                                            }

                                            @Override
                                            public void onStopped(@NotNull JcStatus jcStatus) {

                                            }

                                            @Override
                                            public void onJcpError(@NotNull Throwable throwable) {

                                            }
                                        });
                                        jcp.createNotification();

                                    }else {

                                    }

                                }
                            });
                            df= FirebaseDatabase.getInstance().getReference().child("Songs").child(uidowner);
                            val=df.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    li.clear();
                                    jclist.clear();
                                    Global.postoset=0;
                                    Global.idx=0;
                                    for (DataSnapshot dss: snapshot.getChildren()){
                                        try {
                                            String album_name = dss.child("album_name").getValue().toString();
                                            String artist = dss.child("artist").getValue().toString();
                                            String songDuration = dss.child("songDuration").getValue().toString();
                                            String songLink = dss.child("songLink").getValue().toString();
                                            String songTitle = dss.child("songTitle").getValue().toString();
                                            String songsCategory = dss.child("songsCategory").getValue().toString();
                                            String imgLink = dss.child("imgLink").getValue().toString();
                                            Song s = new Song(songsCategory, songTitle, artist,album_name,songDuration,songLink, imgLink);
                                            s.setmKey(dss.getKey());
                                            curr=0;
                                            if (album_name.equals(albumname)){
                                                li.add(s);
                                                checkin = true;
                                                jclist.add(JcAudio.createFromURL(songTitle,s.getSongLink()));
                                                if (Global.innermode==1){
                                                    if (dss.getKey().equals(Global.songid)){
                                                        Global.postoset=Global.idx;
                                                    }
                                                }
                                            }


                                        }catch (Exception e){
                                            Toast.makeText(SongInAlbumActivity.this, "Error", Toast.LENGTH_LONG).show();
                                        }
                                        Global.idx++;
                                    }
                                    if (Global.innermode==1){
                                        adapter.setSelectedPos(Global.postoset);
                                    }else {
                                        adapter.setSelectedPos(-1);
                                    }
                                    count.setText(String.valueOf(li.size()));
                                    rv.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    pbar.setVisibility(View.GONE);
                                    if (checkin){
                                        jcp.initPlaylist(jclist, null);
                                        if (Global.innermode==1){
                                            isplay=true;
                                            Global.jcpg=jcp;
                                            Global.adapter=adapter;
                                            Global.li=li;
                                            Global.extAlbum=albumname;
                                            Global.extCat=cate;
                                            Global.modealbumglobal=1;
                                            Global.ownerUser=uidowner;
                                            Global.curAlbum="";
                                            Global.curCat="";
                                            jcp.playAudio(jclist.get(Global.postoset));
                                            jcp.setVisibility(View.VISIBLE);
                                            jcp.setJcPlayerManagerListener(new JcPlayerManagerListener() {
                                                @Override
                                                public void onPreparedAudio(@NotNull JcStatus jcStatus) {
                                                    List<JcAudio> templi = jcp.getMyPlaylist();
                                                    int idx = templi.indexOf(jcp.getCurrentAudio());
                                                    adapter.setSelectedPos(idx);
                                                    adapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void onCompletedAudio() {

                                                }

                                                @Override
                                                public void onPaused(@NotNull JcStatus jcStatus) {

                                                }

                                                @Override
                                                public void onContinueAudio(@NotNull JcStatus jcStatus) {

                                                }

                                                @Override
                                                public void onPlaying(@NotNull JcStatus jcStatus) {

                                                }

                                                @Override
                                                public void onTimeChanged(@NotNull JcStatus jcStatus) {

                                                }

                                                @Override
                                                public void onStopped(@NotNull JcStatus jcStatus) {

                                                }

                                                @Override
                                                public void onJcpError(@NotNull Throwable throwable) {

                                                }
                                            });
                                            jcp.createNotification();
                                        }


                                    }else {
                                        Toast.makeText(SongInAlbumActivity.this, "There is no song", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pbar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            });
        }else {
            if (getIntent().getStringExtra("kode")!=null){
                albumname = Global.album;
                cate = Global.cat;
            }
            else {
                albumname = Global.curAlbum;
                cate = Global.curCat;
            }
            if (Global.curAlbum.equals(albumname)){
                isplay=true;
                pbar.setVisibility(View.GONE);
                replaceView(jcp,Global.jcpg);
                jcp.setVisibility(View.VISIBLE);
                li=Global.li;
                adapter=Global.adapter;
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else {
                adapter=new SongRecyclerAdaptor(getApplicationContext(), li, new SongRecyclerAdaptor.RecyclerItemClickListener() {
                    @Override
                    public void OnClickListener(Song s, int pos) {
                        if (mode==0){
                            changeSelectedSong(pos);
                            Global.jcpg=jcp;
                            Global.adapter=adapter;
                            Global.li=li;
                            jcp.playAudio(jclist.get(pos));
                            jcp.setVisibility(View.VISIBLE);
                            jcp.setJcPlayerManagerListener(new JcPlayerManagerListener() {
                                @Override
                                public void onPreparedAudio(@NotNull JcStatus jcStatus) {
                                    List<JcAudio> templi = jcp.getMyPlaylist();
                                    int idx = templi.indexOf(jcp.getCurrentAudio());
                                    adapter.setSelectedPos(idx);
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCompletedAudio() {

                                }

                                @Override
                                public void onPaused(@NotNull JcStatus jcStatus) {

                                }

                                @Override
                                public void onContinueAudio(@NotNull JcStatus jcStatus) {

                                }

                                @Override
                                public void onPlaying(@NotNull JcStatus jcStatus) {

                                }

                                @Override
                                public void onTimeChanged(@NotNull JcStatus jcStatus) {

                                }

                                @Override
                                public void onStopped(@NotNull JcStatus jcStatus) {

                                }

                                @Override
                                public void onJcpError(@NotNull Throwable throwable) {

                                }
                            });
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
                                String imgLink = dss.child("imgLink").getValue().toString();
                                Song s = new Song(songsCategory, songTitle, artist,album_name,songDuration,songLink, imgLink);
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
            }
        }

        an.setText(albumname);
        cat.setText(cate);

        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(SongInAlbumActivity.this);
                pd.show();
                DatabaseReference dffff = FirebaseDatabase.getInstance().getReference().child("Like").child(uidowner).child(albumname).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dffff.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            unlove.setVisibility(View.VISIBLE);
                            love.setVisibility(View.GONE);
                            try {
                                Global.flib.getAdapter().notifyDataSetChanged();
                            }catch (Exception e){

                            }
                            try {
                                Global.arf.getAsra().notifyDataSetChanged();
                            }catch (Exception e){

                            }
                            DatabaseReference dfz = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            dfz.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String uname = task.getResult().child("username").getValue().toString();
                                        Notification n = new Notification(uidowner,FirebaseAuth.getInstance().getCurrentUser().getUid(),uname+" like your album '"+albumname+"'.",albumname,"1");
                                        DatabaseReference dffz = FirebaseDatabase.getInstance().getReference().child("Notification").child(uidowner);
                                        String uploadid=dffz.push().getKey();
                                        dffz.child(uploadid).setValue(n);
                                    }
                                }
                            });

                        }else {
                            pd.dismiss();
                            Toast.makeText(SongInAlbumActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        unlove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(SongInAlbumActivity.this);
                pd.show();
                DatabaseReference dffff = FirebaseDatabase.getInstance().getReference().child("Like").child(uidowner).child(albumname).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dffff.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            unlove.setVisibility(View.GONE);
                            love.setVisibility(View.VISIBLE);
                            try {
                                Global.flib.getAdapter().notifyDataSetChanged();
                            }catch (Exception e){

                            }
                            try {
                                Global.arf.getAsra().notifyDataSetChanged();
                            }catch (Exception e){

                            }
                        }else {
                            pd.dismiss();
                            Toast.makeText(SongInAlbumActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isplay){
                    try {
                        Global.flib.getAdapter().notifyDataSetChanged();
                    }catch (Exception e){

                    }
                    try {
                        Global.arf.getAsra().notifyDataSetChanged();
                    }catch (Exception e){

                    }
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
        if (modedisplay==1){
            Global.extAlbum=albumname;
            Global.extCat=cate;
            Global.modealbumglobal=1;
            Global.ownerUser=uidowner;
            Global.curAlbum="";
            Global.curCat="";
        }else {
            Global.curAlbum=albumname;
            Global.curCat=cate;
            Global.modealbumglobal=0;
            Global.extAlbum="";
            Global.extCat="";
        }

        isplay=true;

    }

    @Override
    public void onBackPressed() {
        if (isplay){
            try {
                Global.flib.getAdapter().notifyDataSetChanged();
            }catch (Exception e){

            }
            try {
                Global.arf.getAsra().notifyDataSetChanged();
            }catch (Exception e){

            }
            Intent i = new Intent(SongInAlbumActivity.this, MainHomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        }else {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }

}