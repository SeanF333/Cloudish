package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapter.AlbumSearchRecyclerAdaptor;
import Else.Album;
import Else.Global;

public class Hits_Album_A extends AppCompatActivity {

    ImageView back;
    RecyclerView rv;
    List<Pair<Album,String>> lia;
    AlbumSearchRecyclerAdaptor asra;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hits__album_);

        back=findViewById(R.id.backfromalbumtype);
        rv=findViewById(R.id.recHits);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        lia=new ArrayList<>();
        asra=new AlbumSearchRecyclerAdaptor(this, lia);
        rv.setAdapter(asra);
        pd = new ProgressDialog(this);

        readAlbums();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void readAlbums(){
        DatabaseReference dfr = FirebaseDatabase.getInstance().getReference("Like");
        dfr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    pd.show();
                    Query q = FirebaseDatabase.getInstance().getReference("Album");
                    q.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                lia.clear();
                                for (DataSnapshot snapshot1 : task.getResult().getChildren()) {
                                    for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                                        String albumname, category, imageurl,userid;
                                        albumname=snapshot2.child("albumname").getValue().toString();
                                        category=snapshot2.child("category").getValue().toString();
                                        imageurl=snapshot2.child("imageurl").getValue().toString();
                                        userid=snapshot1.getKey().toString();
                                        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            continue;
                                        }
                                        DatabaseReference dfff = FirebaseDatabase.getInstance().getReference().child("Like").child(userid).child(albumname);
                                        dfff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                   int count = (int) task.getResult().getChildrenCount();
                                                    Album a = new Album(albumname,category,imageurl);
                                                    a.setLikecount(count);
                                                    Pair<Album,String> p = new Pair<>(a,userid);
                                                    lia.add(p);
                                                    Collections.sort(lia, new Comparator<Pair<Album, String>>() {
                                                        @Override
                                                        public int compare(Pair<Album, String> o1, Pair<Album, String> o2) {
                                                            return o2.first.getLikecount()-o1.first.getLikecount();
                                                        }
                                                    });
                                                    int upperbound = Math.min(lia.size(),9);
                                                    lia=lia.subList(0,upperbound);
                                                    asra.notifyDataSetChanged();
                                                }else {
                                                    pd.dismiss();
                                                    Toast.makeText(Hits_Album_A.this,"Error",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });






                                    }



                                }
                                pd.dismiss();

                            }else {
                                pd.dismiss();
                                Toast.makeText(Hits_Album_A.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    pd.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}