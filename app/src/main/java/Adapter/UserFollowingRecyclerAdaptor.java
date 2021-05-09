package Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.music.cloudish.R;

import java.util.List;

import Else.User;

public class UserFollowingRecyclerAdaptor extends RecyclerView.Adapter<UserFollowingRecyclerAdaptor.MyViewHolder>{

    private Context context;
    private List<Pair<User,String>> liu;

    public UserFollowingRecyclerAdaptor(Context context, List<Pair<User,String>> liu) {
        this.context = context;
        this.liu = liu;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater li = LayoutInflater.from(context);
        view=li.inflate(R.layout.user_following_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pair<User,String> p = liu.get(position);
        User a = p.first;
        holder.tv.setText(a.getUsername());
        holder.tv2.setText(a.getFullname());
        Glide.with(context).load(a.getImageurl()).into(holder.iv);
        holder.btn.setVisibility(View.VISIBLE);
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Confirmation");
                builder.setMessage("You may have to sent follow request again if you Unfollow this user, are you sure?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialog pd = new ProgressDialog(context);
                                pd.show();
                                DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(p.second);
                                df.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Follower").child(p.second).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            dff.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        liu.remove(position);
                                                        pd.dismiss();
                                                        notifyDataSetChanged();
                                                    }else {
                                                        pd.dismiss();
                                                        Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }else {
                                            pd.dismiss();
                                            Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

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
        Button btn,btn2;
        RelativeLayout rl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.usernameS);
            tv2=itemView.findViewById(R.id.fullnameS);
            iv=itemView.findViewById(R.id.userpics);
            btn=itemView.findViewById(R.id.btnUnfol);
            btn2=itemView.findViewById(R.id.btnFollow);



        }
    }
}
