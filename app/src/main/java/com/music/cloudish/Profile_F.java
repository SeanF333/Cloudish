package com.music.cloudish;

import android.app.NotificationManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import Else.Global;

public class Profile_F extends Fragment {


    LinearLayout ll, ed, layout_post, layout_concert;
    ImageView iv;
    TextView uname, fname, email, telp, following, follower;
    DatabaseReference df;
    ProgressDialog pd;
    Button verify;
    LinearLayout l1, l2, notif;

    public Profile_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        iv = view.findViewById(R.id.profilePics);
        uname = view.findViewById(R.id.profileUsername);
        fname = view.findViewById(R.id.profilefullname);
        email = view.findViewById(R.id.emailprof);
        telp = view.findViewById(R.id.telpprof);
        verify = view.findViewById(R.id.verify);
        layout_concert = view.findViewById(R.id.layout_concert);
        layout_post = view.findViewById(R.id.layout_post);
        ll = view.findViewById(R.id.logoutll);
        ed = view.findViewById(R.id.edprof);
        notif = view.findViewById(R.id.layout_notif);
        follower = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        l1 = view.findViewById(R.id.ll_following);
        l2 = view.findViewById(R.id.ll_follower);
        FirebaseUser us = FirebaseAuth.getInstance().getCurrentUser();
        us.reload();
        if (us.isEmailVerified()) {
            verify.setVisibility(View.GONE);
        }

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading Data");
        pd.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        df = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        df.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().child("imageurl").getValue().toString();
                    Glide.with(getContext()).load(url).into(iv);
                    uname.setText('@' + task.getResult().child("username").getValue().toString());
                    fname.setText(task.getResult().child("fullname").getValue().toString());
                    email.setText(task.getResult().child("email").getValue().toString());
                    telp.setText(task.getResult().child("phone").getValue().toString());
                    DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Follower").child(uid);
                    dff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot ds = task.getResult();
                                long count1 = ds.getChildrenCount();
                                follower.setText(String.valueOf(count1));
                                Query dfff = FirebaseDatabase.getInstance().getReference().child("Following").child(uid);
                                dfff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DataSnapshot dss = task.getResult();
                                            long count2 = 0;
                                            for (DataSnapshot snapshot1 : task.getResult().getChildren()) {
                                                if ((boolean) snapshot1.getValue() == true) {
                                                    count2++;
                                                }
                                            }
                                            following.setText(String.valueOf(count2));
                                            pd.dismiss();
                                        } else {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    pd.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll = view.findViewById(R.id.logoutll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.jcpg != null) {
                    Global.jcpg.kill();
                }
                Global.curCat = "";
                Global.curAlbum = "";
                Global.album = "";
                Global.cat = "";
                NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                nMgr.cancelAll();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), Login_A.class);
                startActivity(i);
                getActivity().finishAffinity();
            }
        });

        ed = view.findViewById(R.id.edprof);
        ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);

            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify.setEnabled(false);
                us.sendEmailVerification()
                        .addOnCompleteListener(getActivity(), new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                // Re-enable button
                                verify.setEnabled(true);

                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(),
                                            "Verification email sent to " + us.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(),
                                            "Failed to send verification email.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });


        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NotificationActivity.class);
                startActivity(i);
            }
        });

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                See_Follower_Following_F ldf = new See_Follower_Following_F();
                Bundle args = new Bundle();
                args.putString("username", uname.getText().toString());
                args.putInt("mode", 1);
                ldf.setArguments(args);

                getFragmentManager().beginTransaction().replace(R.id.mainC, ldf).commit();
            }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                See_Follower_Following_F ldf = new See_Follower_Following_F();
                Bundle args = new Bundle();
                args.putString("username", uname.getText().toString());
                args.putInt("mode", 2);
                ldf.setArguments(args);

                getFragmentManager().beginTransaction().replace(R.id.mainC, ldf).commit();
            }
        });

        layout_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddPost_A.class);
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