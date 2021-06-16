package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
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

import Adapter.AlbumSearchRecyclerAdaptor;
import Else.Album;

public class Liked_Album_A extends AppCompatActivity {

    ImageView back;
    RecyclerView rv;
    List<Pair<Album,String>> lia;
    List<Pair<String,String>> lal;
    AlbumSearchRecyclerAdaptor asra;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked__album_);

        back=findViewById(R.id.backfromalbumtype);
        rv=findViewById(R.id.recLoved);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        lia=new ArrayList<>();
        lal=new ArrayList<>();
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
                    lal.clear();
                    for (DataSnapshot ds1 : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : ds1.getChildren()) {
                            if (ds2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue()!=null){
                                Pair<String,String> pss = new Pair<>(ds1.getKey(),ds2.getKey());
                                lal.add(pss);
                            }
                        }
                    }

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
                                        Pair<String,String> tocheck = new Pair<>(userid,albumname);
                                        if (lal.contains(tocheck)){
                                            Album a = new Album(albumname,category,imageurl);
                                            Pair<Album,String> p = new Pair<>(a,userid);
                                            lia.add(p);
                                        }


                                    }



                                }
                                pd.dismiss();
                                asra.notifyDataSetChanged();
                            }else {
                                pd.dismiss();
                                Toast.makeText(Liked_Album_A.this,"Error",Toast.LENGTH_SHORT).show();
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