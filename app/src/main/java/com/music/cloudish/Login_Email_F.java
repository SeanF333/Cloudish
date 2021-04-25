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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Email_F extends Fragment {

    TextView title;
    TextInputLayout email, password;
    Button forget_password, go;
    FirebaseAuth auth;
    ProgressDialog pd;
    float v = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login__email_, container, false);

        auth=FirebaseAuth.getInstance();

        title = root.findViewById(R.id.tv_title);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        forget_password = root.findViewById(R.id.forget_password);
        go = root.findViewById(R.id.go);

        title.setTranslationX(800);
        email.setTranslationX(800);
        password.setTranslationX(800);
        forget_password.setTranslationX(800);
        go.setTranslationX(800);

        title.setAlpha(v);
        email.setAlpha(v);
        password.setAlpha(v);
        forget_password.setAlpha(v);
        go.setAlpha(v);

        title.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        forget_password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        go.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Authenticating..");
                pd.show();
                goToMainActivity();
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Sending..");
                pd.show();
                Boolean a=validateEmail();
                if (!a){
                    pd.dismiss();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String em = email.getEditText().getText().toString();
                auth.sendPasswordResetEmail(em)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),
                                            "Reset password email sent to " + em,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),
                                            "Failed to send reset password email.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return root;
    }

    private void goToMainActivity() {

        Boolean a,b;
        a = validateEmail();
        b = validatePassword();

        if(!a || !b){
            pd.dismiss();
            return;
        }

        String _email = email.getEditText().getText().toString();
        String _password = password.getEditText().getText().toString();

        signInWithEmail(_email, _password);

    }

    private void signInWithEmail(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    Intent i = new Intent(getActivity(), MainHomeActivity.class);
                    startActivity(i);
                    getActivity().finishAffinity();
                }else{
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Your email or password is wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Boolean validatePassword() {

        String val = password.getEditText().getText().toString().trim();
        if(val.isEmpty()){
            password.setError("Field can not be empty");
            return false;
        }else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }

    private Boolean validateEmail() {

        String val = email.getEditText().getText().toString().trim();
        if(val.isEmpty()){
            email.setError("Field can not be empty");
            return false;
        }else{
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }
}