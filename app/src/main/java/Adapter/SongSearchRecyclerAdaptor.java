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
import com.music.cloudish.R;

import java.util.List;

import Else.Song;
import Else.Utility;


public class SongSearchRecyclerAdaptor extends RecyclerView.Adapter<SongSearchRecyclerAdaptor.MyViewHolder>{

    private Context context;
    private List<Song> lis;

    public SongSearchRecyclerAdaptor(Context context, List<Song> lis) {
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
        Song s = lis.get(position);
        holder.tv.setText(s.getSongTitle());
        holder.tv2.setText(s.getArtist());
        holder.tv3.setText(s.getAlbum_name());
        String temp = Utility.convertDur(Long.parseLong(s.getSongDuration()));
        holder.tv4.setText(temp);
        Glide.with(context).load(s.getImgLink()).into(holder.iv);

    }

    @Override
    public int getItemCount() {
        return lis.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv,tv2,tv3,tv4;
        ImageView iv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.sTitle);
            tv2=itemView.findViewById(R.id.sArtist);
            tv3=itemView.findViewById(R.id.sAlbum);
            tv4=itemView.findViewById(R.id.sDuration);
            iv=itemView.findViewById(R.id.dummusicpics);




        }
    }

}
