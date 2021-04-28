package com.music.cloudish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ViewConcert_A extends AppCompatActivity {

    private Integer currentChoosen;
    private SearchView searchView;
    private ImageView backButton, addButton;
    private RecyclerView concertRecyclerByName, concertRecyclerByGenre, concertRecyclerByArtist, concertRecyclerByCity;
    private DatabaseReference mDatabase;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_concert_);


        // Hook
        searchView = findViewById(R.id.searchView);
        concertRecyclerByName = findViewById(R.id.recyclerViewName);
        concertRecyclerByGenre = findViewById(R.id.recyclerViewGenre);
        concertRecyclerByArtist = findViewById(R.id.recyclerViewArtist);
        concertRecyclerByCity = findViewById(R.id.recyclerViewCity);
        backButton = findViewById(R.id.back_btn);
        addButton = findViewById(R.id.button_add);

        // OnClickListener
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



    }
}