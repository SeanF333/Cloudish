package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_A extends AppCompatActivity {

    TextInputLayout email, password, phone_number, username, fullname;
    String _email, _password, _phone_number, _username, _fullname;
    CountryCodePicker ccp;
    Button go;
    FirebaseAuth auth;
    DatabaseReference ref;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);

        // Hook
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone_number = findViewById(R.id.phone_number);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        ccp = findViewById(R.id.ccp);
        go = findViewById(R.id.go);

        //OnCLickListener
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(SignUp_A.this);
                pd.setMessage("Please Wait");
                pd.show();
                gotoMainActivity();
            }
        });
    }

    private void gotoMainActivity() {

        Boolean a,b,c,d,e;

        a = validateEmail();
        b = validateFullname();
        c = validateUsername();
        d = validatePassword();

        if(!a || !b || !c || !d ){
            pd.dismiss();
            return;
        }else {
            validatePhoneNumber();
        }

    }

    private void validatePhoneNumber() {
        String _tempPhoneNumber = phone_number.getEditText().getText().toString().trim();
        _phone_number = ccp.getSelectedCountryCode()+_tempPhoneNumber;

        if(_phone_number.isEmpty()){
            phone_number.setError("Field cannot be empty");
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.orderByChild("phone").equalTo(_phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    pd.dismiss();
                    phone_number.setError("This number already exist");
                }
                else{
                    phone_number.setErrorEnabled(false);

                    Boolean a,b,c,d,e;

                    a = validateEmail();
                    b = validateFullname();
                    c = validateUsername();
                    d = validatePassword();

                    if(!a || !b || !c || !d){
                        pd.dismiss();
                        return;
                    }else{
                        pd.dismiss();
                        Intent in = new Intent(SignUp_A.this, OTPActivity.class);
                        int tipe=1;
                        in.putExtra("phone", _phone_number);
                        in.putExtra("tipe", tipe);
                        in.putExtra("fullname",_fullname);
                        in.putExtra("username",_username);
                        in.putExtra("email",_email);
                        in.putExtra("password",_password);
                        startActivity(in);
                        finishAffinity();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static boolean isValidPassword(String password,String regex)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    private boolean validatePassword() {

        String val = password.getEditText().getText().toString().trim();
        _password = val;

        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,20}$";

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (!isValidPassword(val, regex)) {
            password.setError("Password is too weak!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validateUsername() {
        String val = username.getEditText().getText().toString().trim();
        _username = val;

        String checkspaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else if (val.length() > 20) {
            username.setError("Username is too long!");
            return false;
        } else if (!val.matches(checkspaces)) {
            username.setError("White space is now allowed!");
            return false;
        } else {
            // Check if username exist
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFullname() {
        String val = fullname.getEditText().getText().toString().trim();
        _fullname = val;

        if (val.isEmpty()) {
            fullname.setError("Field cannot be empty");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        _email = val;
        String checkemail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(checkemail)) {
            email.setError("Invalid Email Address");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;

        }
    }

}