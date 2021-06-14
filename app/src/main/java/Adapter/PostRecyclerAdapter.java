package Adapter;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.music.cloudish.R;
import com.music.cloudish.SongInAlbumActivity;
import com.music.cloudish.UserHome_A;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import Else.Global;
import Else.Notification;
import Else.Post;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {

    Context context;
    ArrayList<Post> postlist;
    View view;

    public PostRecyclerAdapter(ArrayList<Post> postlist, Context c) {
        this.postlist = postlist;
        this.context=c;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_cardview, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postlist.get(position);
        holder.bindPost(post);
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class PostViewHolder extends  RecyclerView.ViewHolder{

        ConstraintLayout main_layout;
        ImageView post_image, poster_image, love_btn;
        TextView poster_name, date, caption, count_likes;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            this.main_layout = itemView.findViewById(R.id.main_layout);
            this.poster_image = itemView.findViewById(R.id.profile_img);
            this.post_image = itemView.findViewById(R.id.post_img);
            this.love_btn = itemView.findViewById(R.id.love_btn);
            this.count_likes = itemView.findViewById(R.id.count_likes);
            this.date = itemView.findViewById(R.id.post_date);
            this.poster_name = itemView.findViewById(R.id.poster_name);
            this.caption = itemView.findViewById(R.id.caption);
        }

        public void bindPost(Post post){

            String postid = post.getPost_id();
            String posterimageurl = post.getPoster_imageurl();
            String postername = post.getPoster_name();
            String imageUrl = post.getImageurl();
            LocalDateTime dates = post.getDatetime();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss");
            String datetime = dtf.format(dates);
            String _caption = post.getCaption();
            String posterid = post.getPoster_id();

            setLoveButon(postid);
            poster_name.setText(postername);
            date.setText(datetime);
            caption.setText(_caption);

            if(posterimageurl!=null){
                Glide.with(view.getContext()).load(posterimageurl).into(poster_image);
                poster_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), UserHome_A.class);
                        i.putExtra("artist_id", posterid);
                        v.getContext().startActivity(i);
                    }
                });
            }
            if(imageUrl!=null){
                Glide.with(view.getContext()).load(imageUrl).into(post_image);
            }

            poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), UserHome_A.class);
                    i.putExtra("artist_id", posterid);
                    v.getContext().startActivity(i);
                }
            });

            love_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likePost(postid,posterid);
                }
            });


        }

        public void likePost(String postid, String posterid){
            ProgressDialog pd = new ProgressDialog(context);
            pd.show();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mPostLike = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).child("likedBy");
            mPostLike.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child(uid).exists()){
                            mPostLike.child(uid).removeValue();
                            love_btn.setImageDrawable(ContextCompat.getDrawable(view.getContext().getApplicationContext(), R.drawable.circle_love));
                            Query dbrf = FirebaseDatabase.getInstance().getReference().child("Notification").child(posterid).orderByChild("publisherid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            dbrf.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DataSnapshot dsp : task.getResult().getChildren()) {
                                            String message = dsp.child("text").getValue().toString();
                                            String postidd = dsp.child("album_name").getValue().toString();
                                            if (!message.endsWith("like your post.") && !postidd.equals(postid)){
                                                continue;
                                            }
                                            dsp.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    pd.dismiss();

                                                }
                                            });
                                            break;
                                        }
                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else if(!snapshot.child(uid).exists()){
                            love_btn.setImageDrawable(ContextCompat.getDrawable(view.getContext().getApplicationContext(), R.drawable.heart128));
                            mPostLike.child(uid).setValue(true);
                            DatabaseReference dfz = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            dfz.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String uname = task.getResult().child("username").getValue().toString();
                                        Notification n = new Notification(posterid,FirebaseAuth.getInstance().getCurrentUser().getUid(),uname+" like your post.",postid,"10");
                                        DatabaseReference dffz = FirebaseDatabase.getInstance().getReference().child("Notification").child(posterid);
                                        String uploadid=dffz.push().getKey();
                                        dffz.child(uploadid).setValue(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }else if(!snapshot.exists()){
                        love_btn.setImageDrawable(ContextCompat.getDrawable(view.getContext().getApplicationContext(), R.drawable.heart128));
                        mPostLike.child(uid).setValue(true);
                        DatabaseReference dfz = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        dfz.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()){
                                    String uname = task.getResult().child("username").getValue().toString();
                                    Notification n = new Notification(posterid,FirebaseAuth.getInstance().getCurrentUser().getUid(),uname+" like your post.",postid,"10");
                                    DatabaseReference dffz = FirebaseDatabase.getInstance().getReference().child("Notification").child(posterid);
                                    String uploadid=dffz.push().getKey();
                                    dffz.child(uploadid).setValue(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    setLoveButon(postid);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public void setLoveButon(String postid){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mPostLike = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).child("likedBy");
            mPostLike.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child(uid).exists()){
                            love_btn.setImageDrawable(ContextCompat.getDrawable(view.getContext().getApplicationContext(), R.drawable.heart128));
                        }else if(!snapshot.child(uid).exists()){
                            love_btn.setImageDrawable(ContextCompat.getDrawable(view.getContext().getApplicationContext(), R.drawable.circle_love));
                        }
                        count_likes.setText(String.valueOf(snapshot.getChildrenCount()) + " likes");
                    }else if(!snapshot.exists()){
                        count_likes.setText("0 likes");
                        love_btn.setImageDrawable(ContextCompat.getDrawable(view.getContext().getApplicationContext(), R.drawable.circle_love));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
