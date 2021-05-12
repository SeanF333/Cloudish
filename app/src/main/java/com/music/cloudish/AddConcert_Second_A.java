package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.SongRecylerAdapter;
import Adapter.UserRecyclerAdapter;
import Else.Performance;
import Else.Song;
import Else.User;
import Listener.SongListener;
import Listener.UserListener;

public class AddConcert_Second_A extends AppCompatActivity implements UserListener, SongListener {

    Button buttonDone, buttonAdd;
    RecyclerView recyclerViewTrack, recyclerViewUser;
    TextView textViewSeePlaylist;
    Dialog dialog;
    SearchView searchViewUser, searchViewTrack;
    Uri imageUri;
    String concertName, concertMainGenre, concertDescription, concertDuration, concertDate, concertTime;
    ProgressDialog pd;
    StorageReference sr;

    UserRecyclerAdapter userRecyclerAdapter;
    SongRecylerAdapter songRecyclerAdapter;
    DatabaseReference mDatabase;

    ArrayList<User> userSearchList = new ArrayList<>();
    ArrayList<Song> songSearchList = new ArrayList<>();
    ArrayList<Song> songlist = new ArrayList<>();
    ArrayList<User> userlist = new ArrayList<>();
    ArrayList<Performance> performances = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concert__second_);

        // Hook
        buttonDone = findViewById(R.id.button_done);
        buttonAdd = findViewById(R.id.buttonAdd);
        textViewSeePlaylist = findViewById(R.id.textViewPlaylist);
        recyclerViewUser = findViewById(R.id.recylcerViewUser);
        recyclerViewTrack = findViewById(R.id.recyclerViewTrack);
        searchViewUser = findViewById(R.id.searchViewUser);
        searchViewTrack = findViewById(R.id.searchViewTrack);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);

        // Get Information from Previous Intent
        concertName = getIntent().getStringExtra("concertName");
        concertMainGenre = getIntent().getStringExtra("concertMainGenre");
        concertDescription = getIntent().getStringExtra("concertDescription");
        concertDuration = getIntent().getStringExtra("concertDuration");
        concertDate = getIntent().getStringExtra("concertDate");
        concertTime = getIntent().getStringExtra("concertTime");
        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        // Listener
        searchViewUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchUser(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchUser(newText);
                return true;
            }
        });

        searchViewTrack.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchSong(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchSong(newText);
                return false;
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPerformances();
            }
        });

        textViewSeePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPerformances();
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveConcertInformation();
            }
        });

        // Isi dulu RecyclerView

        pd.setMessage("Please Wait");
        pd.show();

        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){

                        String id = ds.getKey();
                        String email = ds.child("email").getValue().toString();
                        String fullname = ds.child("fullname").getValue().toString();
                        String imageurl = ds.child("imageurl").getValue().toString();
                        String phone = ds.child("phone").getValue().toString();
                        String isprivate = ds.child("private").getValue().toString();
                        String username = ds.child("username").getValue().toString();

                        User u = new User(id, email, fullname, imageurl, phone, isprivate, username);

                        userSearchList.add(u);
                    }

                    userRecyclerAdapter = new UserRecyclerAdapter(userSearchList, AddConcert_Second_A.this);
                    recyclerViewUser.setHasFixedSize(true);
                    recyclerViewUser.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
                    recyclerViewUser.setAdapter(userRecyclerAdapter);

                    pd.dismiss();

                }else{
                    pd.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("AddConcertSecond", error.getMessage());
            }
        });
    }

    private void SaveConcertInformation() {

        // Validation

        // Jika belum ada performance, harus masukkin performance dlu
        if(performances.size() == 0){
            Toast.makeText(AddConcert_Second_A.this, "Your playlist is empty.", Toast.LENGTH_SHORT).show();
            return;
        }else if(performances.size() > 0){

            // Save concert Information

            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String concertKey = mDatabase.child("Concerts").child(currentUserId).push().getKey();

            pd.setMessage("Uploading");
            pd.show();

            sr = FirebaseStorage.getInstance().getReference("ConcertImages");
            DatabaseReference mConcert = FirebaseDatabase.getInstance().getReference().child("Concerts").child(currentUserId).child(concertKey);

            if(imageUri!=null){
                StorageReference storageReference = sr.child(System.currentTimeMillis()+"."+getExtension(imageUri));
                storageReference.putFile(imageUri).continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloaduri = task.getResult();
                            String myuri = downloaduri.toString();
                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("imageurl", ""+myuri);
                            hm.put("name", concertName);
                            hm.put("date", concertDate);
                            hm.put("description", concertDescription);
                            hm.put("duration", concertDuration);
                            hm.put("main_genre", concertMainGenre);
                            hm.put("time", concertTime);

                            mConcert.updateChildren(hm);
                            pd.dismiss();

                            savePlaylistInformation(concertKey);

                            Intent i = new Intent(AddConcert_Second_A.this, ViewConcert_A.class);
                            startActivity(i);
                            finishAffinity();
                        }else {
                            Toast.makeText(AddConcert_Second_A.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddConcert_Second_A.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(AddConcert_Second_A.this, "No Image Selected", Toast.LENGTH_SHORT).show();
            }

            return;
        }
    }

    private void savePlaylistInformation(String concertKey) {

        DatabaseReference mPlayList = FirebaseDatabase.getInstance().getReference().child("Playlists").child(concertKey);

        DatabaseReference mPlaylist = mDatabase.child("Playlists").child(concertKey);

        for(Performance p : performances){
            String user_id = p.getUser().getId();
            List<Song> songlist= p.getSongList();
            for(Song song : songlist){
                String musicId = song.getmKey();
                mPlaylist.child(user_id).child("tracklist").child(musicId).setValue("true");
            }
        }

    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    private void ShowPerformances() {

        // Dibuat List baru untuk menampilkannya di dalam ArrayList
        ArrayList<String> dialogList = new ArrayList();

        // Mengisi dialog dengan String
        for(Performance p : performances){
            String artistName = p.getUser().getFullname();
            List<Song> currentSongList = p.getSongList();
            for(Song s : currentSongList){
                dialogList.add(s.getSongTitle() + " - " + artistName);
            }
        }

        // inisialisasi dialog
        dialog = new Dialog(AddConcert_Second_A.this);

        // kustomisasi dialog
        dialog.setContentView(R.layout.dialog_searchable_spinner);
        dialog.getWindow().setLayout(1000,1500);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // show dialog
        dialog.show();

        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView listView = dialog.findViewById(R.id.listView);

        // Initialize array adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (AddConcert_Second_A.this, android.R.layout.simple_list_item_1, dialogList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);

                // Generate ListView Item using TextView
                return view;
            }
        };

        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter array List
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return;
    }

    private void AddPerformances() {

        // Validasi user apakah sudah mencari lagu dan user
        if(userRecyclerAdapter == null && songRecyclerAdapter == null){
            Toast.makeText(AddConcert_Second_A.this, "You haven't search any artist and songs yet", Toast.LENGTH_SHORT).show();
            return;
        }else if(userRecyclerAdapter == null && !(songRecyclerAdapter == null)){
            Toast.makeText(AddConcert_Second_A.this, "Search and Choose any artist", Toast.LENGTH_SHORT).show();
            return;
        }else if(userRecyclerAdapter != null && songRecyclerAdapter == null){
            Toast.makeText(AddConcert_Second_A.this, "You haven't search any songs yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi user apakah sudah memilih user
        List<User> selectedUser = userRecyclerAdapter.getSelectedUser();
        Integer selectedUserCount = selectedUser.size();

        // Kalo 0 berarti blm milih, kalo lebih dari 1 berarti milih lebih dari 1 artist
        if(selectedUserCount == 0){
            Toast.makeText(AddConcert_Second_A.this, "Please choose one of the artist.", Toast.LENGTH_SHORT).show();
            return;
        }else if(selectedUserCount > 1){
            Toast.makeText(AddConcert_Second_A.this, "You can only choose on artist at a time!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sampai sini berarti sudah milih 1 user secara benar
        User chosenUser = selectedUser.get(0);

        // Validasi user apakah sudah memilih lagu
        List<Song> selectedSong = songRecyclerAdapter.getSelectedSong();
        Integer selectedSongCount = selectedSong.size();

        // Jika selectedSong = 0 berarti user belum milih sama sekali lagu apapun
        if(selectedSongCount == 0){
            Toast.makeText(AddConcert_Second_A.this, "Please choose any song", Toast.LENGTH_SHORT).show();
            return;
        }

        // kalau belum ada performance sama sekali, tambahin yang baru, kalao sudah ada cek dulu samain artisnya
        if(performances.size() == 0){
            Performance p = new Performance(chosenUser, selectedSong);
            performances.add(p);
        }else{
            // cek ada yang sama ato engga artisnya
            int isArtistExist = -1;
            for(Performance performance : performances){
                if(performance.getUser().getId().equals(chosenUser.getId())){
                    isArtistExist = 1;
                    performance.addListOfSong(selectedSong);
                }
            }

            if(isArtistExist == -1){
                performances.add(new Performance(chosenUser, selectedSong));
            }
        }

        Toast.makeText(AddConcert_Second_A.this, "Successfully Added.", Toast.LENGTH_SHORT).show();
        return;
    }

    private void SearchUser(String query) {

        pd.show();

        ArrayList<User> current_user_search_list = new ArrayList<>();

        for(User user : userSearchList){
            if(user.getFullname().toLowerCase().contains(query.toLowerCase())){
                current_user_search_list.add(user);
            }
        }

        userRecyclerAdapter = new UserRecyclerAdapter(current_user_search_list, AddConcert_Second_A.this);
        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
        recyclerViewUser.setAdapter(userRecyclerAdapter);

        pd.dismiss();

    }

    private void SearchSong(String query) {

        List<User> selectedUser = new ArrayList<>();
        selectedUser = userRecyclerAdapter.getSelectedUser();

        // Kalo blm milih harus disuruh milih

        Integer selectedUserCount = selectedUser.size();

        if(selectedUserCount == 0){
            Toast.makeText(this, "Please choose artist first!", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.show();

        String currentUserId = "";

        for(User u : selectedUser){
            currentUserId = u.getId();
        }

        // Kalo user yang dipilih udah tepat satu, kita akan cari musiknya
        DatabaseReference mSong = FirebaseDatabase.getInstance().getReference().child("Songs").child(currentUserId);

        // Sebelum mencari yang baru, di clear dulu supaya ga nambah terus
        songSearchList.clear();


        // ini proses untuk mengambil semua lagu untuk suatu user
        mSong.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){

                    String songId, album_name, artist, songDuration, songLink, songTitle, songCategory;

                    songTitle = ds.child("songTitle").getValue().toString();
                    songId = ds.getKey();
                    album_name = ds.child("album_name").getValue().toString();
                    artist = ds.child("artist").getValue().toString();
                    songDuration = ds.child("songDuration").getValue().toString();
                    songLink = ds.child("songLink").getValue().toString();

                    songCategory = ds.child("songsCategory").getValue().toString();

                    if(songTitle.toLowerCase().contains(query.toLowerCase())){
                        Song s = new Song(songCategory, songTitle, artist, album_name, songDuration, songLink);
                        s.setmKey(songId);
                        songSearchList.add(s);
                    }
                }

                songRecyclerAdapter = new SongRecylerAdapter(songSearchList, AddConcert_Second_A.this);
                recyclerViewTrack.setHasFixedSize(true);
                recyclerViewTrack.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL));
                recyclerViewTrack.setAdapter(songRecyclerAdapter);

                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddConcert_Second_A.this, "Error in Text Searching", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onSongAction(Boolean isSelected) {

    }

    @Override
    public void onUserAction(Boolean isSelected) {

    }
}