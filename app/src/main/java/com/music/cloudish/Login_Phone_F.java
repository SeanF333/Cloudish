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
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class Login_Phone_F extends Fragment {

    CountryCodePicker ccp;
    TextInputLayout phone_number;
    String _phone_number;
    Button go;
    FirebaseAuth auth;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login__phone_, container, false);

        // Hook
        ccp = root.findViewById(R.id.ccp);
        phone_number = root.findViewById(R.id.phone_number);
        go = root.findViewById(R.id.go);

        auth = FirebaseAuth.getInstance();

        // click listener
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getContext());
                pd.setMessage("Please Wait");
                pd.show();
                validatePhoneNumber();
            }
        });

        return root;
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
                    Intent in = new Intent(getActivity(), OTPActivity.class);
                    int tipe=2;
                    in.putExtra("phone", _phone_number);
                    in.putExtra("tipe", tipe);
                    startActivity(in);
                    getActivity().finishAffinity();
                }
                else{
                    pd.dismiss();
                    phone_number.setError("This number is not registered.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}