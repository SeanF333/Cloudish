package com.music.cloudish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile_F extends Fragment {


    LinearLayout ll,ed, layout_post, layout_concert;
    ImageView iv;
    TextView uname,fname,email,telp;
    DatabaseReference df;
    ProgressDialog pd;

    public Profile_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        // Hook
        iv=view.findViewById(R.id.profilePics);
        uname=view.findViewById(R.id.profileUsername);
        fname=view.findViewById(R.id.profilefullname);
        email=view.findViewById(R.id.emailprof);
        telp=view.findViewById(R.id.telpprof);
        layout_concert = view.findViewById(R.id.layout_concert);
        layout_post = view.findViewById(R.id.layout_post);
        ll=view.findViewById(R.id.logoutll);
        ed=view.findViewById(R.id.edprof);

        pd=new ProgressDialog(getActivity());
        pd.setMessage("Loading Data");
        pd.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("profileF", uid);

        df= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        df.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    String url = task.getResult().child("imageurl").getValue().toString();
                    Glide.with(getContext()).load(url).into(iv);
                    uname.setText('@' + task.getResult().child("username").getValue().toString());
                    fname.setText(task.getResult().child("fullname").getValue().toString());
                    email.setText(task.getResult().child("email").getValue().toString());
                    telp.setText(task.getResult().child("phone").getValue().toString());
                    pd.dismiss();
                }else{
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // On Click Listener

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), Login_A.class);
                startActivity(i);
                getActivity().finishAffinity();
            }
        });

        ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);

            }
        });

        layout_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ViewPost_A.class);
                startActivity(i);
            }
        });

        layout_concert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ViewConcert_A.class);
                startActivity(i);
            }
        });

        return view;
    }


}