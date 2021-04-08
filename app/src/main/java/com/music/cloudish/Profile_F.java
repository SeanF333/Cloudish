package com.music.cloudish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_F extends Fragment {


    LinearLayout ll,ed;
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

        iv=view.findViewById(R.id.profilePics);
        uname=view.findViewById(R.id.profileUsername);
        fname=view.findViewById(R.id.profilefullname);
        email=view.findViewById(R.id.emailprof);
        telp=view.findViewById(R.id.telpprof);

        pd=new ProgressDialog(getActivity());
        pd.setMessage("Loading Data");
        pd.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        ll=view.findViewById(R.id.logoutll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().finishAffinity();
            }
        });

        ed=view.findViewById(R.id.edprof);
        ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);

            }
        });

        return view;
    }


}