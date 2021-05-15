package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView pp,ex,sv;
    EditText fn,un;
    Switch oo;
    TextView change;
    DatabaseReference df;
    StorageReference sr;
    ProgressDialog pd;
    Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pp=findViewById(R.id.pppp);
        ex=findViewById(R.id.close);
        sv=findViewById(R.id.donee);
        fn=findViewById(R.id.fnnnn);
        un=findViewById(R.id.unnnn);
        oo=findViewById(R.id.switches);
        change=findViewById(R.id.change);

        pd=new ProgressDialog(EditProfileActivity.this);
        pd.setMessage("Loading Data");
        pd.show();

        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        df= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        loadchange();

        sr= FirebaseStorage.getInstance().getReference("ProfilePics");
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.RECTANGLE).start(EditProfileActivity.this);
            }
        });

        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Saving");
                pd.show();
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("fullname",fn.getText().toString());
                hm.put("username",un.getText().toString());
                String s = "";
                if(oo.isChecked()){
                    s="true";
                }else {
                    s="false";
                }
                hm.put("private",s);
                df.updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            finish();
                        }
                        else {
                            Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }

    private void loadchange(){
        df.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    String url = task.getResult().child("imageurl").getValue().toString();
                    Glide.with(EditProfileActivity.this).load(url).into(pp);
                    un.setText(task.getResult().child("username").getValue().toString());
                    fn.setText(task.getResult().child("fullname").getValue().toString());
                    String priv=task.getResult().child("private").getValue().toString();
                    if (priv.equalsIgnoreCase("true")){
                        oo.setChecked(true);
                    }else {
                        oo.setChecked(false);
                    }
                    pd.dismiss();
                }else{
                    Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            mImageUri=ar.getUri();
            uploadImage();
        }

    }

    private void uploadImage(){
        pd.setMessage("Uploading");
        pd.show();

        if(mImageUri!=null){
            StorageReference baru = sr.child(System.currentTimeMillis()+"."+getExtension(mImageUri));
            baru.putFile(mImageUri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return baru.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloaduri = task.getResult();
                        String myuri = downloaduri.toString();
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("imageurl", ""+myuri);
                        df.updateChildren(hm);
                        pd.dismiss();
                        loadchange();
                    }else {
                        Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(EditProfileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }
}