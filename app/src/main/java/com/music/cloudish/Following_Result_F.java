package com.music.cloudish;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import Adapter.AlbumSearchRecyclerAdaptor;
import Adapter.UserFollowingRecyclerAdaptor;
import Adapter.UserSearchRecyclerAdaptor;
import Else.Album;
import Else.User;

public class Following_Result_F extends Fragment {

    RecyclerView rv;
    DatabaseReference df;
    List<String> liuf;
    List<Pair<User,String>> liu;
    UserFollowingRecyclerAdaptor adaptor;
    EditText search;

    public Following_Result_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following__result_, container, false);
        rv=view.findViewById(R.id.following_result);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        liu=new ArrayList<>();
        liuf=new ArrayList<>();
        adaptor=new UserFollowingRecyclerAdaptor(getContext(), liu);
        rv.setAdapter(adaptor);
        search=getActivity().findViewById(R.id.search_uname);

        DatabaseReference rrr = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rrr.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    liuf.clear();
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if ((boolean)snapshot1.getValue()==true){
                            String userid;
                            userid=snapshot1.getKey().toString();
                            liuf.add(userid);
                        }

                    }
                    if (search.getText().toString().equals("")){
                        readUsers();
                    }else {
                      searchUsers(search.getText().toString());
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
                searchUsers(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUsers(String s){
        if (s.equals("")){
            readUsers();
            return;
        }
        Query q = FirebaseDatabase.getInstance().getReference("Users");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                liu.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String email, fullname, imageurl, phone, Private, username, userid;
                    email=snapshot1.child("email").getValue().toString();
                    fullname=snapshot1.child("fullname").getValue().toString();
                    imageurl=snapshot1.child("imageurl").getValue().toString();
                    phone=snapshot1.child("phone").getValue().toString();
                    Private=snapshot1.child("private").getValue().toString();
                    username=snapshot1.child("username").getValue().toString();
                    userid=snapshot1.getKey().toString();
                    if (email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        continue;
                    }
                    if (liuf.contains(userid)){
                        if (username.toLowerCase().startsWith(s.toLowerCase()) || fullname.toLowerCase().startsWith(s.toLowerCase())){
                            User user = new User(userid,email,fullname,imageurl,phone,Private,username);
                            Pair<User,String> p = new Pair<>(user,userid);
                            liu.add(p);
                        }
                    }
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readUsers(){
        df=FirebaseDatabase.getInstance().getReference("Users");
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search.getText().toString().equals("")){
                    liu.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String email, fullname, imageurl, phone, Private, username,userid;
                        email=snapshot1.child("email").getValue().toString();
                        fullname=snapshot1.child("fullname").getValue().toString();
                        imageurl=snapshot1.child("imageurl").getValue().toString();
                        phone=snapshot1.child("phone").getValue().toString();
                        Private=snapshot1.child("private").getValue().toString();
                        username=snapshot1.child("username").getValue().toString();
                        userid=snapshot1.getKey().toString();
                        if (email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            continue;
                        }
                        if (liuf.contains(userid)){
                            User user = new User(userid,email,fullname,imageurl,phone,Private,username);
                            Pair<User,String> p = new Pair<>(user,userid);
                            liu.add(p);
                        }

                    }
                    adaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}