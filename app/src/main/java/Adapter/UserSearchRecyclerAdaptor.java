package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.music.cloudish.R;

import java.util.List;

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
                FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(a.getId()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follower").child(a.getId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                notifyDataSetChanged();
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
