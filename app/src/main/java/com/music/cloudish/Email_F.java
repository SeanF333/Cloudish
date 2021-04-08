package com.music.cloudish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Email_F#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Email_F extends Fragment {

    EditText ema,pass;
    Button login1;
    String em,pas;
    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog pd;


    public Email_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email_, container, false);

        ema=view.findViewById(R.id.emailText);
        pass=view.findViewById(R.id.passText);
        login1=view.findViewById(R.id.login1);

        auth=FirebaseAuth.getInstance();

        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(getActivity());
                pd.setMessage("Please Wait...");
                pd.show();

                em=ema.getText().toString();
                pas=pass.getText().toString();

                signInWithEmail(em,pas);

            }
        });

        return view;
    }

    private void signInWithEmail(String em, String pas) {
        auth.signInWithEmailAndPassword(em,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Intent i = new Intent(getActivity(), MainHomeActivity.class);
                    startActivity(i);
                    getActivity().finishAffinity();
                }else{
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}