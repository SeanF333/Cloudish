package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapter.ConcertRecyclerAdapter;
import Else.Concert;

public class ViewConcert_A extends AppCompatActivity {

    ProgressDialog pd;
    SearchView searchView;
    ImageView backButton;
    FloatingActionButton addButton;
    RecyclerView concertRecycler;
    DatabaseReference mDatabase;
    ConcertRecyclerAdapter concertRecyclerAdapter;
    ArrayList<Concert> concertArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_concert_);

        // Hook
        searchView = findViewById(R.id.searchView);
        concertRecycler = findViewById(R.id.concertRV);
        backButton = findViewById(R.id.back_btn);
        addButton = findViewById(R.id.button_add);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(ViewConcert_A.this);

        // Listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewConcert_A.this, AddConcert_First_A.class);
                startActivity(i);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchConcert(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchConcert(newText);
                return true;
            }
        });

        // Load dulu pertama kali seluruh konser ke dalam recyclerView
        pd.setMessage("Please wait");
        pd.show();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mConcert = FirebaseDatabase.getInstance().getReference().child("Concerts").child(currentUserId);

        mConcert.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getKey();
                        Log.d("ViewConcert_A", "ada konser" + " " + id);
                        String description = ds.child("description").getValue().toString();
                        String image_url = ds.child("imageurl").getValue().toString();
                        String main_genre = ds.child("main_genre").getValue().toString();
                        String name = ds.child("name").getValue().toString();
                        String time = ds.child("time").getValue().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                        Date date = null;
                        try {
                            date = dateFormat.parse(ds.child("date").getValue().toString()+ " " +time);
                        } catch (ParseException e) {
                            Log.d("Error parse date", "Error parse date");
                            e.printStackTrace();
                        }
                        Integer duration = Integer.valueOf(ds.child("duration").getValue().toString());
                        Concert concert = new Concert(id, image_url,  main_genre, name, description, date, time, currentUserId, duration);
                        concertArrayList.add(concert);
                    }
                    ConcertRecyclerAdapter concertRecyclerAdapter = new ConcertRecyclerAdapter(concertArrayList);
                    concertRecycler.setHasFixedSize(true);
                    concertRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
                    concertRecycler.setAdapter(concertRecyclerAdapter);
                    pd.dismiss();
                }else{
                    Toast.makeText(ViewConcert_A.this, "No Concert", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void searchConcert(String query) {
        List<Concert> userConcertList = new ArrayList<>();
        userConcertList = concertArrayList;

        ArrayList<Concert> searchResultList = new ArrayList<>();

        for(Concert c : userConcertList){
            if(c.getName().toLowerCase().contains(query.toLowerCase()) || c.getMain_genre().toLowerCase().equals(query.toLowerCase())){
                searchResultList.add(c);
            }
        }

        ConcertRecyclerAdapter concertRecyclerAdapter = new ConcertRecyclerAdapter(searchResultList);
        concertRecycler.setHasFixedSize(true);
        concertRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        concertRecycler.setAdapter(concertRecyclerAdapter);
        return;
    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }
}