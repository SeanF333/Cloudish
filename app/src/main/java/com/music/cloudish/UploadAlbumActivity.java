package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Else.Album;
import Else.Global;

public class UploadAlbumActivity extends AppCompatActivity {

    ImageView iv,close,done;
    Button select;
    Spinner sp;
    EditText name;
    String selectedcat;
    DatabaseReference df,y;
    StorageReference sr;
    ProgressDialog pd;
    Uri mImageUri;
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_album);

        pd=new ProgressDialog(UploadAlbumActivity.this);
        iv=findViewById(R.id.albumpp);
        close=findViewById(R.id.closeUA);
        done=findViewById(R.id.doneeUA);
        select=findViewById(R.id.selectC);
        name=findViewById(R.id.unnnnUA);
        sp=findViewById(R.id.spin);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        df= FirebaseDatabase.getInstance().getReference().child("Album").child(uid);

        sr= FirebaseStorage.getInstance().getReference("AlbumCover");

        Glide.with(UploadAlbumActivity.this).load("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/musicalbum.jpg?alt=media&token=5e66a85d-dec1-48a0-836b-61ad71f1fb0c").into(iv);

        mImageUri=Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/musicalbum.jpg?alt=media&token=5e66a85d-dec1-48a0-836b-61ad71f1fb0c");

        List<String> ctgr = new ArrayList<>();
        ctgr.add("Country");
        ctgr.add("Electronic dance music");
        ctgr.add("Hip-hop");
        ctgr.add("Indie");
        ctgr.add("Rock");
        ctgr.add("Jazz");
        ctgr.add("K-Pop");
        ctgr.add("Metal");
        ctgr.add("Custom");

        ArrayAdapter<String> da = new ArrayAdapter<>(UploadAlbumActivity.this, android.R.layout.simple_spinner_item, ctgr);
        da.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp.setAdapter(da);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedcat=parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                Toast.makeText(UploadAlbumActivity.this, "Selected "+selectedcat, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.RECTANGLE).start(UploadAlbumActivity.this);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Saving");
                pd.show();
                Query q = FirebaseDatabase.getInstance().getReference().child("Album").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("albumname").equalTo(name.getText().toString());
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue()!=null){
                            pd.dismiss();
                            Toast.makeText(ctx, "Album name already exist!", Toast.LENGTH_SHORT).show();
                        }else if (name.getText().toString().equals("")){
                            pd.dismiss();
                            Toast.makeText(ctx, "Album name cannot empty!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("albumname",name.getText().toString());
                            hm.put("category",selectedcat);
                            y = df.child(String.valueOf(System.currentTimeMillis()));
                            y.setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        uploadImage();

                                    }else{
                                        pd.dismiss();
                                        Toast.makeText(UploadAlbumActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            mImageUri=ar.getUri();
            Glide.with(UploadAlbumActivity.this).load(mImageUri.toString()).into(iv);
        }

    }

    private void uploadImage(){

        if(mImageUri!=null){

            if (mImageUri.equals(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/musicalbum.jpg?alt=media&token=5e66a85d-dec1-48a0-836b-61ad71f1fb0c"))){
                HashMap<String, Object> hm = new HashMap<>();
                hm.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/musicalbum.jpg?alt=media&token=5e66a85d-dec1-48a0-836b-61ad71f1fb0c");
                y.updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        try {
                            Album baru = new Album(name.getText().toString(),selectedcat,"https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/musicalbum.jpg?alt=media&token=5e66a85d-dec1-48a0-836b-61ad71f1fb0c");
                            Global.flib.getAdapter().getLi().add(baru);
                            Global.flib.getAdapter().notifyDataSetChanged();
                        }catch (Exception e){}
                        finish();
                    }
                });

            }
            else {
                StorageReference baru = sr.child(System.currentTimeMillis()+"");
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
                            y.updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    try {
                                        Album baru = new Album(name.getText().toString(),selectedcat,myuri);
                                        Global.flib.getAdapter().getLi().add(baru);
                                        Global.flib.getAdapter().notifyDataSetChanged();
                                    }catch (Exception e){}
                                    finish();
                                }
                            });


                        }else {
                            Toast.makeText(UploadAlbumActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadAlbumActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

        }else {
            Toast.makeText(UploadAlbumActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
            finish();
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