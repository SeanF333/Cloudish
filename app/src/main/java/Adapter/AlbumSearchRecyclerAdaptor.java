package Adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.cloudish.R;

import java.util.List;

import Else.Album;
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
        Glide.with(context).load(a.getImageurl()).into(holder.iv);
        holder.tv.setText(a.getAlbumname());

    }

    @Override
    public int getItemCount() {
        return lia.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv= itemView.findViewById(R.id.titlealbum);

            iv=itemView.findViewById(R.id.albumimgsearch);




        }
    }
}
