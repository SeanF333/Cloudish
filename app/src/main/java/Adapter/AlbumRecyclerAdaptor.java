package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.cloudish.R;
import com.music.cloudish.SongInAlbumActivity;

import java.util.List;

import Else.Album;
import Else.Global;

public class AlbumRecyclerAdaptor extends RecyclerView.Adapter<AlbumRecyclerAdaptor.MyViewHolder>{

    private Context context;
    private List<Album> li;

    public AlbumRecyclerAdaptor(Context context, List<Album> li) {
        this.context = context;
        this.li = li;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater li = LayoutInflater.from(context);
        view=li.inflate(R.layout.album_card_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Album a = li.get(position);
        holder.tv.setText(a.getAlbumname());
        Glide.with(context).load(a.getImageurl()).into(holder.iv);
        if (Global.curAlbum.equals(a.getAlbumname())){
            holder.ll.setBackgroundColor(Color.YELLOW);
        }else {
            holder.ll.setBackgroundColor(Color.WHITE);
        }
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.curAlbum.equals(a.getAlbumname())){
                    Intent i = new Intent(v.getContext(), SongInAlbumActivity.class);
                    i.putExtra("an",a.getAlbumname());
                    i.putExtra("cat", a.getAlbumcategory());
                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    v.getContext().startActivity(i);
                }else {
                    Intent i = new Intent(v.getContext(), SongInAlbumActivity.class);
                    i.putExtra("an",a.getAlbumname());
                    i.putExtra("cat", a.getAlbumcategory());
                    i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    v.getContext().startActivity(i);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return li.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            ImageView iv;
            CardView cv;
            LinearLayout ll;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tv= itemView.findViewById(R.id.titlealbum);
                iv=itemView.findViewById(R.id.albumimg);
                cv=itemView.findViewById(R.id.albumitem);
                ll=itemView.findViewById(R.id.bckg_album);
            }
        }


}
