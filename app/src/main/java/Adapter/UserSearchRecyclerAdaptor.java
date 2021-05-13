package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.music.cloudish.R;

import java.util.List;

import Else.Notification;
import Else.User;

public class UserSearchRecyclerAdaptor extends RecyclerView.Adapter<UserSearchRecyclerAdaptor.MyViewHolder>{

    private Context context;
    private List<User> liu;

    public UserSearchRecyclerAdaptor(Context context, List<User> liu) {
        this.context = context;
        this.liu = liu;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater li = LayoutInflater.from(context);
        view=li.inflate(R.layout.user_item_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User a = liu.get(position);
        holder.tv.setText(a.getUsername());
        holder.tv2.setText(a.getFullname());
        Glide.with(context).load(a.getImageurl()).into(holder.iv);
        holder.btn.setVisibility(View.INVISIBLE);
        isFollowing(a.getId(), holder.btn);

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.getIsprivate().equals("true")){
                    FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(a.getId()).setValue(false);
                    Notification n = new Notification(a.getId(),FirebaseAuth.getInstance().getCurrentUser().getUid(),"","","0");
                    DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Notification").child(a.getId());
                    String uploadid=dff.push().getKey();
                    dff.child(uploadid).setValue(n);
                    holder.btn.setText("Requested");
                    holder.btn.setVisibility(View.VISIBLE);
                    holder.btn.setClickable(false);
                }else {
                    DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    d.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                String uname = task.getResult().child("username").getValue().toString();
                                Notification n = new Notification(a.getId(),FirebaseAuth.getInstance().getCurrentUser().getUid(),uname+" has starting to follow you.","","2");
                                DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Notification").child(a.getId());
                                String uploadid=dff.push().getKey();
                                dff.child(uploadid).setValue(n);

                            }else {
                                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(a.getId()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child("Follower").child(a.getId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            notifyDataSetChanged();
                                        }else {
                                            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }else {
                                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });



    }

    public void isFollowing(String userid, Button btn){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child(userid).exists()){
                    btn.setVisibility(View.VISIBLE);
                    btn.setText("Follow");
                }else if ((boolean)snapshot.child(userid).getValue()==false){
                    btn.setText("Requested");
                    btn.setVisibility(View.VISIBLE);
                    btn.setClickable(false);
                }else {
                    btn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return liu.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv,tv2;
        ImageView iv;
        Button btn;
        RelativeLayout rl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.usernameS);
            tv2=itemView.findViewById(R.id.fullnameS);
            iv=itemView.findViewById(R.id.userpics);
            btn=itemView.findViewById(R.id.btnFol);



        }
    }
}
