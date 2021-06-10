package com.music.cloudish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Adapter.ConcertRecyclerAdapter;
import Adapter.SongSearchRecyclerAdaptor;
import Else.Concert;
import Else.Song;

public class Concert_Result_F extends Fragment {

    RecyclerView rv;
    DatabaseReference df;
    List<Concert> lic;
    ConcertRecyclerAdapter cra;
    EditText search;

    public Concert_Result_F() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_concert__result_, container, false);
        rv=v.findViewById(R.id.concert_search_result);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));

        lic=new ArrayList<>();
        cra=new ConcertRecyclerAdapter((ArrayList<Concert>) lic);
        rv.setAdapter(cra);
        search=getActivity().findViewById(R.id.search_columm);

        if (search.getText().toString().equals("")){
            readConcert();
        }else {
            searchConcert(search.getText().toString());
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchConcert(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void searchConcert(String s){
        if (s.equals("")){
            readConcert();
            return;
        }

        Query q = FirebaseDatabase.getInstance().getReference("Concerts");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lic.clear();
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            continue;
                        }
                        for (DataSnapshot dss : ds.getChildren()){
                            String id = dss.getKey();
                            Log.d("ViewConcert_A", "ada konser" + " " + id);
                            String description = dss.child("description").getValue().toString();
                            String image_url = dss.child("imageurl").getValue().toString();
                            String main_genre = dss.child("main_genre").getValue().toString();
                            String name = dss.child("name").getValue().toString();
                            if (!name.toLowerCase().contains(s.toLowerCase())){
                                continue;
                            }
                            String time = dss.child("time").getValue().toString();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                            Date date = null;
                            Integer duration = Integer.valueOf(dss.child("duration").getValue().toString());
                            try {
                                date = dateFormat.parse(dss.child("date").getValue().toString()+ " " +time);
                                Calendar c = Calendar.getInstance();
                                c.setTime(date);
                                c.add(Calendar.MINUTE,duration);
                                if (c.getTime().compareTo(new Date())<0){
                                    continue;
                                }
                            } catch (ParseException e) {
                                Log.d("Error parse date", "Error parse date");
                                e.printStackTrace();
                            }

                            Concert concert = new Concert(id, image_url,  main_genre, name, description, date, time, ds.getKey(), duration);
                            lic.add(concert);

                        }
                    }
                    cra.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readConcert(){
        Query q = FirebaseDatabase.getInstance().getReference("Concerts");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search.getText().toString().equals("")){
                    lic.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                continue;
                            }
                            for (DataSnapshot dss : ds.getChildren()){
                                String id = dss.getKey();
                                Log.d("ViewConcert_A", "ada konser" + " " + id);
                                String description = dss.child("description").getValue().toString();
                                String image_url = dss.child("imageurl").getValue().toString();
                                String main_genre = dss.child("main_genre").getValue().toString();
                                String name = dss.child("name").getValue().toString();
                                String time = dss.child("time").getValue().toString();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                                Date date = null;
                                Integer duration = Integer.valueOf(dss.child("duration").getValue().toString());
                                try {
                                    date = dateFormat.parse(dss.child("date").getValue().toString()+ " " +time);
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(date);
                                    c.add(Calendar.MINUTE,duration);
                                    if (c.getTime().compareTo(new Date())<0){
                                        continue;
                                    }
                                } catch (ParseException e) {
                                    Log.d("Error parse date", "Error parse date");
                                    e.printStackTrace();
                                }

                                Concert concert = new Concert(id, image_url,  main_genre, name, description, date, time, ds.getKey(), duration);
                                lic.add(concert);
                            }

                        }
                        cra.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}