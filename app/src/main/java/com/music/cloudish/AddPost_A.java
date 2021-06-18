package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AddPost_A extends AppCompatActivity {

    ImageView image;
    TextInputLayout caption_layout;
    TextInputEditText caption_editText;
    Button btn_post, btn_cancel;
    Uri imageUri;
    String caption, userId;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_);

        // Hook
        image = findViewById(R.id.image);
        caption_layout = findViewById(R.id.caption_layout);
        btn_post = findViewById(R.id.btn_post);
        caption_editText = findViewById(R.id.caption_editText);
        btn_cancel = findViewById(R.id.btn_cancel);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // isi image
        Glide.with(AddPost_A.this).load("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/no_image_post.png?alt=media&token=0cac54d8-698e-4eee-8f29-4232a6e42601").into(image);
        imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/no_image_post.png?alt=media&token=0cac54d8-698e-4eee-8f29-4232a6e42601");

        // Listener
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.RECTANGLE).start(AddPost_A.this);
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePost();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPost_A.this, ViewMyPost_A.class);
                startActivity(i);
                finish();
            }
        });

        caption_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateCaption(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void validateCaption(CharSequence s) {
        Log.d("TextWatcherNih", String.valueOf(s.length()));
        if(s.length() > 100){
            caption_layout.setError("Max 100 Character");
        }else{
            caption_layout.setError(null);
        }
    }

    private void validatePost() {
        caption = caption_editText.getText().toString();
        if(caption.length() == 0){
            caption_layout.setError("Caption can't be empty!");
            return;
        }else if(caption.length() > 100){
            caption_layout.setError("Max 100 Character");
            return;
        }

        if(imageUri.equals(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/no_image_post.png?alt=media&token=0cac54d8-698e-4eee-8f29-4232a6e42601"))){
            caption_layout.setError(null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure want to post it without image?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    savePost();
                }
            });
            builder.setNegativeButton("Choose Image", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.RECTANGLE).start(AddPost_A.this);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            savePost();
        }
    }

    private void savePost() {
        StorageReference sr = FirebaseStorage.getInstance().getReference("PostImage");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String concertKey = mDatabase.child("Posts").push().getKey();
        DatabaseReference mPost = FirebaseDatabase.getInstance().getReference().child("Posts").child(concertKey);

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading Posts");
        pd.show();

        HashMap<String, Object> hm = new HashMap<>();
        if(imageUri.equals(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/no_image_post.png?alt=media&token=0cac54d8-698e-4eee-8f29-4232a6e42601"))){
            hm.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/no_image_post.png?alt=media&token=0cac54d8-698e-4eee-8f29-4232a6e42601");
            hm.put("caption", caption);
            hm.put("poster_id",currentUserId);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String datetime = dtf.format(now);
            hm.put("datetime",datetime);
            mPost.updateChildren(hm);
            pd.dismiss();
        }else{
            pd.dismiss();
            StorageReference storageReference = sr.child(System.currentTimeMillis()+"."+getExtension(imageUri));
            storageReference.putFile(imageUri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Uri downloaduri = (Uri)task.getResult();
                    String myuri = downloaduri.toString();;
                    hm.put("imageurl", myuri);
                    hm.put("caption",caption);
                    hm.put("poster_id",currentUserId);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String datetime = dtf.format(now);
                    hm.put("datetime",datetime);
                    mPost.updateChildren(hm);

                }
            });
        }

        goToViewPost();

    }

    private void goToViewPost() {
        Intent i = new Intent(AddPost_A.this, ViewMyPost_A.class);
        startActivity(i);
        finish();
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            imageUri=ar.getUri();
            Glide.with(AddPost_A.this).load(imageUri.toString()).into(image);
        }
    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }
}