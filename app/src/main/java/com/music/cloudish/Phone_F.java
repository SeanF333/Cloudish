package com.music.cloudish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Phone_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Phone_F extends Fragment {

    EditText notelp;
    Button login2;

    String no;
    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog pd;

    public Phone_F() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_, container, false);

        notelp=view.findViewById(R.id.phoneText);
        login2=view.findViewById(R.id.login2);
        auth=FirebaseAuth.getInstance();

        login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no=notelp.getText().toString();
                Intent in = new Intent(getActivity(), OTPActivity.class);
                int tipe=2;
                in.putExtra("phone", no);
                in.putExtra("tipe", tipe);
                startActivity(in);
                getActivity().finishAffinity();
            }
        });

        return view;
    }
}