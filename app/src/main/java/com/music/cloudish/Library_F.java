package com.music.cloudish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.AlbumRecyclerAdaptor;
import Else.Album;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Library_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Library_F extends Fragment {

    FloatingActionButton add;
    RecyclerView rv;
    AlbumRecyclerAdaptor adapter;
    DatabaseReference df;
    List<Album> li;
    ProgressDialog pd;

    public AlbumRecyclerAdaptor getAdapter() {
        return adapter;
    }

    public void setAdapter(AlbumRecyclerAdaptor adapter) {
        this.adapter = adapter;
    }

    public Library_F() {
        // Required empty public constructor
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library_, container, false);

        add=v.findViewById(R.id.addfab);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UploadAlbumActivity.class);
                startActivity(i);
            }
        });

        rv=v.findViewById(R.id.albumrecycler);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),2));
        pd= new ProgressDialog(getActivity());
        pd.setMessage("Please Wait");
        pd.show();
        li=new ArrayList<>();

        df= FirebaseDatabase.getInstance().getReference().child("Album").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                li.clear();
                for(DataSnapshot sn : snapshot.getChildren()){
                    Album bum = sn.getValue(Album.class);
                    bum.setAlbumcategory(sn.child("category").getValue().toString());
                    li.add(bum);
                }
                adapter=new AlbumRecyclerAdaptor(getContext(), li);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });

        return v;
    }
}