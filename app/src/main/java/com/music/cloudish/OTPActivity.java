package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    String email, password, username, fullname, phoneNo;
    EditText[] otpETs = new EditText[6];
    Button send;
    ProgressBar pd;
    String verificationCodeBySystem;
    FirebaseAuth auth;
    int tipe;
    DatabaseReference ref;


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == 7 || keyCode == 8 ||
                keyCode == 9 || keyCode == 10 ||
                keyCode == 11 || keyCode == 12 ||
                keyCode == 13 || keyCode == 14 ||
                keyCode == 15 || keyCode == 16 ||
                keyCode == 67) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    int index = checkWhoHasFocus();
                    if (index != 123) {
                        if (Helpers.rS(otpETs[index]).equals("")) {
                            if (index != 0) {
                                otpETs[index - 1].requestFocus();
                            }
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    }
                } else {
                    int index = checkWhoHasFocus();
                    if (index != 123) {
                        if (Helpers.rS(otpETs[index]).equals("")) {
                            return super.dispatchKeyEvent(event);
                        } else {
                            if (index != 5) {
                                otpETs[index + 1].requestFocus();
                            }
                        }
                    }
                    return super.dispatchKeyEvent(event);
                }
            }
        } else {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    private int checkWhoHasFocus() {
        for (int i = 0; i < otpETs.length; i++) {
            EditText tempET = otpETs[i];
            if (tempET.hasFocus()) {
                return i;
            }
        }
        return 123;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        // Hook
        auth = FirebaseAuth.getInstance();
        otpETs[0] = findViewById(R.id.otpET1);
        otpETs[1] = findViewById(R.id.otpET2);
        otpETs[2] = findViewById(R.id.otpET3);
        otpETs[3] = findViewById(R.id.otpET4);
        otpETs[4] = findViewById(R.id.otpET5);
        otpETs[5] = findViewById(R.id.otpET6);

        send=findViewById(R.id.sendBtn);
        pd=findViewById(R.id.PD);
        pd.setVisibility(View.GONE);

        // Get item from previous activity
        phoneNo = getIntent().getStringExtra("phone");
        tipe=getIntent().getIntExtra("tipe",1);

        if(tipe == 1){
            phoneNo = getIntent().getStringExtra("phone");
            fullname = getIntent().getStringExtra("fullname");
            username = getIntent().getStringExtra("username");
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
        }

        sendVerificationCodeToUser(phoneNo);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpbyuser = Helpers.rS(otpETs[0]) + Helpers.rS(otpETs[1]) +
                        Helpers.rS(otpETs[2]) + Helpers.rS(otpETs[3]) + Helpers.rS(otpETs[4])
                        + Helpers.rS(otpETs[5]);

                if(otpbyuser.isEmpty() || otpbyuser.length()<6){
                    Toast.makeText(OTPActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.setVisibility(View.VISIBLE);
                verifyCode(otpbyuser, tipe);
            }
        });

    }

    private void sendVerificationCodeToUser(String phoneNo) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+"+phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code!=null){
                pd.setVisibility(View.VISIBLE);
                verifyCode(code,tipe);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            pd.setVisibility(View.GONE);
        }
    };

    private void verifyCode(String verificationCodeByUser, int tipe){
        PhoneAuthCredential cr = PhoneAuthProvider.getCredential(verificationCodeBySystem, verificationCodeByUser);
        if (tipe==1){
            pd.setVisibility(View.GONE);
            RegisterWEmail();
        }else if (tipe==2){
            signInWithCredential(cr);
        }
    }

    private void signInWithCredential(PhoneAuthCredential cr) {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        fa.signInWithCredential(cr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pd.setVisibility(View.GONE);
                    Intent i = new Intent(OTPActivity.this, MainHomeActivity.class);
                    startActivity(i);
                    finishAffinity();
                }else{
                    pd.setVisibility(View.GONE);
                    Toast.makeText(OTPActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void RegisterWEmail(){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if (task.isSuccessful()){
                    FirebaseUser us = auth.getCurrentUser();
                    String uid = us.getUid();
                    ref= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, Object> hm = new HashMap<>();
                    hm.put("email",email);
                    hm.put("username", username);
                    hm.put("fullname", fullname);
                    hm.put("private", "false");
                    hm.put("phone",phoneNo);
                    hm.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/user-default.jpg?alt=media&token=302aba9b-185e-438e-98a7-a14c7ec896f5");
                    ref.setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent i = new Intent(OTPActivity.this, MainHomeActivity.class);
                                startActivity(i);
                                finishAffinity();
                            }else{
                                Toast.makeText(OTPActivity.this, "Error Making Account Inside", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(OTPActivity.this, "Error Making Account Outside", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

