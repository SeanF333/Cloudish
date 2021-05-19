package com.music.cloudish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.concurrent.Semaphore;

import Adapter.AlbumSearchRecyclerAdaptor;
import Adapter.UserSearchRecyclerAdaptor;
import Else.Album;
import Else.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Album_Result_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Album_Result_F extends Fragment {

    RecyclerView rv;
    DatabaseReference df;
    List<String> liu;
    List<Pair<Album,String>> lia;
    AlbumSearchRecyclerAdaptor asra;
    EditText search;

    public Album_Result_F() {
        // Required empty public constructor
    }

    public AlbumSearchRecyclerAdaptor getAsra() {
        return asra;
    }

    public void setAsra(AlbumSearchRecyclerAdaptor asra) {
        this.asra = asra;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album__result_, container, false);
        rv=v.findViewById(R.id.album_result);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),2));

        liu=new ArrayList<>();
        lia=new ArrayList<>();
        asra= new AlbumSearchRecyclerAdaptor(getContext(),lia);
        rv.setAdapter(asra);
        search=getActivity().findViewById(R.id.search_columm);

        DatabaseReference rrr = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rrr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    liu.clear();
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if ((boolean)snapshot1.getValue()==true){
                            String userid;
                            userid=snapshot1.getKey().toString();
                            liu.add(userid);
                        }

                    }
                    if (search.getText().toString().equals("")){
                        readAlbums();
                    }else {
                        searchAlbums(search.getText().toString());
                    }

                }


            }
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAlbums(s.toString());



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void searchAlbums(String s){

        if (s.equals("")){
            readAlbums();
            return;
        }



        Query q = FirebaseDatabase.getInstance().getReference("Album");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lia.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                        String albumname, category, imageurl,userid;
                        albumname=snapshot2.child("albumname").getValue().toString();
                        category=snapshot2.child("category").getValue().toString();
                        imageurl=snapshot2.child("imageurl").getValue().toString();
                        userid=snapshot1.getKey().toString();
                        if (liu.contains(userid)){
                            if (albumname.toLowerCase().contains(s.toLowerCase())){
                                Album a = new Album(albumname,category,imageurl);
                                Pair<Album,String> p = new Pair<>(a,userid);
                                lia.add(p);
                            }

                        }

                    }



                }

                asra.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void readAlbums(){
        Query q = FirebaseDatabase.getInstance().getReference("Album");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search.getText().toString().equals("")){
                    lia.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()){
                            String albumname, category, imageurl,userid;
                            albumname=snapshot2.child("albumname").getValue().toString();
                            category=snapshot2.child("category").getValue().toString();
                            imageurl=snapshot2.child("imageurl").getValue().toString();
                            userid=snapshot1.getKey().toString();
                            if (liu.contains(userid)){

                                Album a = new Album(albumname,category,imageurl);
                                Pair<Album,String> p = new Pair<>(a,userid);
                                lia.add(p);


                            }

                        }



                    }

                    asra.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}