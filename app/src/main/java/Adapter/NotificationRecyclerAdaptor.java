package Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

import Else.Album;
import Else.Notification;

public class NotificationRecyclerAdaptor extends RecyclerView.Adapter<NotificationRecyclerAdaptor.MyViewHolder>{

    private Context context;
    private List<Pair<Notification,String>> li;
    ProgressDialog pd;

    public NotificationRecyclerAdaptor(Context context, List<Pair<Notification,String>> li) {
        this.context = context;
        this.li = li;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater li = LayoutInflater.from(context);
        view=li.inflate(R.layout.notification_item_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pair<Notification,String> p = li.get(position);
        Notification n = p.first;


        if (n.getMode().equals("0")){
            holder.iv2.setVisibility(View.GONE);
            holder.tv2.setVisibility(View.GONE);
            getUser(n.getPublisherid(), holder.iv1, holder.tv);
            holder.ll.setVisibility(View.VISIBLE);
            holder.b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(p.second);
                    df.setValue(null);
                    DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Following").child(n.getPublisherid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    dff.setValue(null);
                    li.remove(position);
                    notifyDataSetChanged();
                }
            });

            holder.b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(context);
                    pd.setMessage("Please Wait");
                    pd.show();
                    getUser2(n.getPublisherid(), n, p.second);
                    holder.ll.setVisibility(View.GONE);
                    DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Following").child(n.getPublisherid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    df.setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(n.getPublisherid()).setValue(true);
                    sendNotificationBack(n.getPublisherid());

                }
            });
        }else if (n.getMode().equals("2")){
            holder.iv2.setVisibility(View.GONE);
            holder.tv2.setVisibility(View.GONE);
            holder.ll.setVisibility(View.GONE);
            setup(n.getPublisherid(), holder.iv1, 1);
            holder.tv.setText(n.getText());
        }else {
            holder.iv2.setVisibility(View.VISIBLE);
            holder.tv2.setVisibility(View.VISIBLE);
            holder.ll.setVisibility(View.GONE);
            holder.tv.setVisibility(View.GONE);
        }

    }

    private void sendNotificationBack(String userid){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot ds = task.getResult();
                    String username;
                    username=ds.child("username").getValue().toString();
                    Notification n = new Notification(userid,FirebaseAuth.getInstance().getCurrentUser().getUid(),username+" has accepted your following request","","2");
                    DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Notification").child(userid);
                    String uploadid=dff.push().getKey();
                    dff.child(uploadid).setValue(n);
                }
            }
        });
    }

    private void setup(String publisherid, ImageView iv, int mode){
        if (mode==1){
            DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
            df.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String imageurl,username;
                    imageurl=snapshot.child("imageurl").getValue().toString();
                    username=snapshot.child("username").getValue().toString();
                    Glide.with(context).load(imageurl).into(iv);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getUser(String publisherid, ImageView iv, TextView tv){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageurl,username;
                imageurl=snapshot.child("imageurl").getValue().toString();
                username=snapshot.child("username").getValue().toString();
                Glide.with(context).load(imageurl).into(iv);
                tv.setText(username+" has requested to follow you.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser2(String publisherid, Notification n, String idnotif){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        df.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot snapshot=task.getResult();
                    String username;
                    username=snapshot.child("username").getValue().toString();
                    n.setText(username+" has starting to follow you.");
                    n.setMode("2");
                    FirebaseDatabase.getInstance().getReference().child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(idnotif).setValue(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            notifyDataSetChanged();
                            pd.dismiss();
                        }
                    });


                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return li.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv,tv2;
        ImageView iv1,iv2;
        LinearLayout ll;
        Button b1,b2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.textnotif);
            tv2=itemView.findViewById(R.id.dummytext);
            iv1=itemView.findViewById(R.id.userimg);
            iv2=itemView.findViewById(R.id.albumpostimg);
            ll=itemView.findViewById(R.id.ll_confirm);
            b1=itemView.findViewById(R.id.delrequest);
            b2=itemView.findViewById(R.id.accrequest);

        }
    }


}
