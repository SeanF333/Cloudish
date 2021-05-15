package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.music.cloudish.R;
import com.music.cloudish.SongInAlbumActivity;

import java.util.List;

import Else.Global;
import Else.Song;
import Else.Utility;


public class SongSearchRecyclerAdaptor extends RecyclerView.Adapter<SongSearchRecyclerAdaptor.MyViewHolder>{

    private Context context;
    private List<Pair<Pair<Song,String>,String>> lis;

    public SongSearchRecyclerAdaptor(Context context, List<Pair<Pair<Song,String>,String>> lis) {
        this.context = context;
        this.lis = lis;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater li = LayoutInflater.from(context);
        view=li.inflate(R.layout.music_search_item_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pair<Pair<Song,String>,String> p1 = lis.get(position);
        Pair<Song,String> p2 = p1.first;
        String songid = p2.second;
        Song s = p2.first;
        String uid = p1.second;
        holder.tv.setText(s.getSongTitle());
        holder.tv2.setText(s.getArtist());
        holder.tv3.setText(s.getAlbum_name());
        String temp = Utility.convertDur(Long.parseLong(s.getSongDuration()));
        holder.tv4.setText(temp);
        Glide.with(context).load(s.getImgLink()).into(holder.iv);
        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SongInAlbumActivity.class);
                Global.album=s.getAlbum_name();
                Global.modealbum=1;
                Query dfff = FirebaseDatabase.getInstance().getReference().child("Album").child(uid).orderByChild("albumname").equalTo(s.getAlbum_name());
                dfff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            DataSnapshot ds = task.getResult();
                            for (DataSnapshot ds2 : ds.getChildren()) {
                                if (ds2.child("albumname").getValue().toString().equals(s.getAlbum_name())){
                                    Global.cat=ds2.child("category").getValue().toString();
                                    break;
                                }
                            }

                            i.putExtra("kode","1");
                            i.putExtra("albummode","1");
                            i.putExtra("uid", uid);
                            i.putExtra("songid",songid);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            v.getContext().startActivity(i);
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return lis.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv,tv2,tv3,tv4;
        ImageView iv;
        RelativeLayout rl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.sTitle);
            tv2=itemView.findViewById(R.id.sArtist);
            tv3=itemView.findViewById(R.id.sAlbum);
            tv4=itemView.findViewById(R.id.sDuration);
            iv=itemView.findViewById(R.id.dummusicpics);
            rl=itemView.findViewById(R.id.search_song_item);



        }
    }

}
