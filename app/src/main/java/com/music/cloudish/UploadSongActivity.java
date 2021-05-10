package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import Else.Song;
import Else.Utility;

public class UploadSongActivity extends AppCompatActivity {

    ImageView close,sv,simg;
    Button select,img;
    TextView filen;
    EditText stit,salb,sart,sdat,sdur;
    ProgressBar pbb;
    Spinner sp;
    LinearLayout ll;
    String albumname="",selectedcat="";
    Uri uri,sImgUri;
    StorageReference sr;
    DatabaseReference df;
    StorageTask task;
    MediaMetadataRetriever mmdr;
    byte [] art;
    String titlesong="",artistsong="",albumart1 = "", durasi="";
    int flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        close=findViewById(R.id.closeUS);
        sv=findViewById(R.id.doneeUS);
        simg=findViewById(R.id.songimg);
        filen=findViewById(R.id.file);
        stit=findViewById(R.id.stitle);
        salb=findViewById(R.id.salbum);
        sart=findViewById(R.id.sartist);
        sdat=findViewById(R.id.sdata);
        sdur = findViewById(R.id.sduration);
        pbb=findViewById(R.id.pb);
        sp=findViewById(R.id.spinUS);
        ll=findViewById(R.id.show);
        ll.setVisibility(View.GONE);
        select=findViewById(R.id.selsong);
        img=findViewById(R.id.sel_img);
        albumname=getIntent().getStringExtra("an");

        mmdr=new MediaMetadataRetriever();
        df= FirebaseDatabase.getInstance().getReference().child("Songs").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        sr= FirebaseStorage.getInstance().getReference().child("Songs");

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(i,101);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.RECTANGLE).start(UploadSongActivity.this);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filen.equals("No File Selected")){
                    Toast.makeText(UploadSongActivity.this, "Please Select The File", Toast.LENGTH_SHORT).show();
                }else {
                    if (task!=null && task.isInProgress()){
                        Toast.makeText(UploadSongActivity.this, "Song Upload Already In Progress", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        uploadFiles();
                        finish();
                    }
                }
            }
        });

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

        ArrayAdapter<String> da = new ArrayAdapter<>(UploadSongActivity.this, android.R.layout.simple_spinner_item, ctgr);
        da.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp.setAdapter(da);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedcat=parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(16);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void uploadFiles() {
        if (uri!=null){
            Toast.makeText(UploadSongActivity.this, "Please Wait, Uploading", Toast.LENGTH_SHORT).show();
            pbb.setVisibility(View.VISIBLE);
            StorageReference baru = sr.child(System.currentTimeMillis()+"");
            task=baru.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (flag==0){
                        baru.getDownloadUrl().addOnSuccessListener((OnSuccessListener) (uri2) -> {
                            try {
                                Song s = new Song(selectedcat, stit.getText().toString(), sart.getText().toString(), albumname, durasi, uri2.toString(),"https://firebasestorage.googleapis.com/v0/b/cloudish-89d6b.appspot.com/o/music-placeholder.png?alt=media&token=3b8a9fb5-faf9-4210-8035-4acab21a8c9b");
                                String uploadid=df.push().getKey();
                                df.child(uploadid).setValue(s);
                            }catch (Exception e){
                                Toast.makeText(UploadSongActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }


                        });
                    }else {
                        StorageReference baru2 = FirebaseStorage.getInstance().getReference().child("SongPics").child(System.currentTimeMillis()+"");
                        StorageTask st1;
                        st1=baru2.putFile(sImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                baru.getDownloadUrl().addOnSuccessListener((OnSuccessListener) (uri2) -> {
                                    try {
                                        baru2.getDownloadUrl().addOnSuccessListener((OnSuccessListener) (uri) -> {
                                            try {
                                                Song s = new Song(selectedcat, stit.getText().toString(), sart.getText().toString(), albumname, durasi, uri2.toString(),uri.toString());
                                                String uploadid=df.push().getKey();
                                                df.child(uploadid).setValue(s);
                                            }catch (Exception e){
                                                Toast.makeText(UploadSongActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }catch (Exception e){
                                        Toast.makeText(UploadSongActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }


                                });
                            }
                        });
                    }

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double pro = (100.0* snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    pbb.setProgress((int) pro);
                }
            });
        }else {
            Toast.makeText(UploadSongActivity.this, "Please Select The File", Toast.LENGTH_SHORT).show();;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101 && resultCode==RESULT_OK && data.getData()!=null){
            try {
                uri=data.getData();
                String filename = getFilename(uri);
                filen.setText(filename);

                mmdr.setDataSource(this,uri);
                art=mmdr.getEmbeddedPicture();
                if (art!=null){
                    Bitmap bm = BitmapFactory.decodeByteArray(art,0,art.length);
                    simg.setImageBitmap(bm);
                }

                salb.setText(albumname);
                stit.setText(mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                sart.setText(mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                sdat.setText(mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
                sdur.setText(Utility.convertDur(Long.parseLong(mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));

                titlesong=mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                artistsong=mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                durasi=mmdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                ll.setVisibility(View.VISIBLE);
            }catch (Exception e){
                Toast.makeText(UploadSongActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }else if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            sImgUri=ar.getUri();
            Glide.with(this).load(sImgUri).into(simg);
        }

    }

    private String getFilename(Uri uri){
        String res = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor!=null && cursor.moveToFirst()){
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }

        if (res==null){
            res = uri.getPath();
            int cut = res.lastIndexOf('/');
            if (cut!=-1){
                res=res.substring(cut+1);
            }
        }
        return res;

    }

    @Override
    protected void onDestroy() {
        NotificationManager nMgr = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        super.onDestroy();
    }

}