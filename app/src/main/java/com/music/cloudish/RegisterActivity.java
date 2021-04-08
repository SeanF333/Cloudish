package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText email,pass,notelp,uname,fname;
    Button regis1;
    String em,no,pas,un,fn;
    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        uname=findViewById(R.id.username);
        fname=findViewById(R.id.fullName);
        email=findViewById(R.id.emailRegis);
        pass=findViewById(R.id.passRegis);
        notelp=findViewById(R.id.notelpRegis);
        regis1=findViewById(R.id.regis1);


        auth = FirebaseAuth.getInstance();

        regis1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait");
                pd.show();

                em = email.getText().toString();
                pas = pass.getText().toString();
                no = notelp.getText().toString();
                un=uname.getText().toString();
                fn=fname.getText().toString();


                RegisterWEmail(pas,em,un,fn);

            }
        });



    }

    public void toSignIn(View v){
        Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(in);
    }

    public void RegisterWEmail(String pas, String em, String unames, String fnames){

        auth.createUserWithEmailAndPassword(em, pas).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser us = auth.getCurrentUser();
                    String uid = us.getUid();
                    ref= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("email",em);
                    hm.put("username", unames);
                    hm.put("fullname", fnames);
                    hm.put("private", "false");
                    hm.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/user-default.jpg?alt=media&token=302aba9b-185e-438e-98a7-a14c7ec896f5");
                    ref.setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                Intent in = new Intent(RegisterActivity.this, OTPActivity.class);
                                int tipe=1;
                                in.putExtra("phone", no);
                                in.putExtra("tipe", tipe);
                                startActivity(in);
                                finishAffinity();
                            }else{
                                pd.dismiss();
                                Toast.makeText(RegisterActivity.this, "Error Making Account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}