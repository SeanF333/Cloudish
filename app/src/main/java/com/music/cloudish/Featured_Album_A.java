package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.os.Bundle;
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

public class Featured_Album_A extends AppCompatActivity {

    ImageView back;
    RecyclerView rv;
    List<Pair<Album,String>> lia;
    AlbumSearchRecyclerAdaptor asra;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured__album_);

        back=findViewById(R.id.backfromalbumtype);
        rv=findViewById(R.id.recFeatured);
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
        DatabaseReference dfrl = FirebaseDatabase.getInstance().getReference("Like");
        dfrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    pd.show();
                    DatabaseReference dfr = FirebaseDatabase.getInstance().getReference("Promoted");
                    dfr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                List<String> promoted = new ArrayList<>();
                                for (DataSnapshot ds1 : task.getResult().getChildren()) {
                                    promoted.add(ds1.getKey().toString());
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
                                                    if (promoted.contains(snapshot2.getKey().toString()) && !userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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
                                            Toast.makeText(Featured_Album_A.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else {
                                pd.dismiss();
                                Toast.makeText(Featured_Album_A.this,"Error",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }
}