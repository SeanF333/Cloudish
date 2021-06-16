package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.music.cloudish.R;
import com.music.cloudish.SongInAlbumActivity;

import java.util.List;

import Else.Album;
import Else.Global;
import Else.User;


public class AlbumSearchRecyclerAdaptor extends RecyclerView.Adapter<AlbumSearchRecyclerAdaptor.MyViewHolder> {

    private Context context;
    private List<Pair<Album,String>> lia;

    public AlbumSearchRecyclerAdaptor(Context context, List<Pair<Album,String>> lia) {
        this.context = context;
        this.lia = lia;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater li = LayoutInflater.from(context);
        view=li.inflate(R.layout.album_item_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pair<Album,String> tup = lia.get(position);
        Album a = tup.first;
        DatabaseReference dfff = FirebaseDatabase.getInstance().getReference().child("Like").child(tup.second).child(a.getAlbumname());
        dfff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    holder.count.setText(String.valueOf(task.getResult().getChildrenCount()));
                    Glide.with(context).load(a.getImageurl()).into(holder.iv);
                    holder.tv.setText(a.getAlbumname());

                    if (Global.extAlbum.equals(a.getAlbumname())){
                        holder.ll.setBackgroundColor(Color.YELLOW);
                    }else {
                        holder.ll.setBackgroundColor(Color.WHITE);
                    }
                    holder.cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Global.extAlbum.equals(a.getAlbumname()) && Global.ownerUser.equals(tup.second)){
                                Intent i = new Intent(v.getContext(), SongInAlbumActivity.class);
                                Global.album=a.getAlbumname();
                                Global.cat=a.getAlbumcategory();
                                Global.modealbum=1;
                                i.putExtra("kode","1");
                                i.putExtra("albummode","1");
                                i.putExtra("uid", tup.second);
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                v.getContext().startActivity(i);
                            }else {
                                Intent i = new Intent(v.getContext(), SongInAlbumActivity.class);
                                Global.album=a.getAlbumname();
                                Global.cat=a.getAlbumcategory();
                                Global.modealbum=1;
                                i.putExtra("kode","1");
                                i.putExtra("albummode","1");
                                i.putExtra("uid", tup.second);
                                i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                v.getContext().startActivity(i);
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return lia.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv,count;
        ImageView iv;
        CardView cv;
        LinearLayout ll;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.titlealbum);

            iv=itemView.findViewById(R.id.albumimgsearch);
            cv=itemView.findViewById(R.id.albumitem_search);
            ll=itemView.findViewById(R.id.bckg_album_search);
            count=itemView.findViewById(R.id.countlike_s);


        }
    }
}
