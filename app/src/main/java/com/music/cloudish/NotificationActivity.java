package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Adapter.NotificationRecyclerAdaptor;
import Else.Notification;

public class NotificationActivity extends AppCompatActivity {

    ImageView iv;
    RecyclerView rv;
    NotificationRecyclerAdaptor adaptor;
    List<Pair<Notification,String>> li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        iv=findViewById(R.id.closenotif);
        rv=findViewById(R.id.recNotif);
        rv.setLayoutManager(new LinearLayoutManager(this));
        li=new ArrayList<>();
        adaptor=new NotificationRecyclerAdaptor(this, li);
        rv.setAdapter(adaptor);

        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                li.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String userid,publisherid,album_name,text,mode;
                    userid=snapshot1.child("userid").getValue().toString();
                    publisherid=snapshot1.child("publisherid").getValue().toString();
                    album_name=snapshot1.child("album_name").getValue().toString();
                    text=snapshot1.child("text").getValue().toString();
                    mode=snapshot1.child("mode").getValue().toString();
                    Notification n = new Notification(userid,publisherid,text,album_name,mode);
                    String id = snapshot1.getKey();
                    Pair<Notification,String> p = new Pair<>(n,id);
                    li.add(p);
                }
                Collections.reverse(li);
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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