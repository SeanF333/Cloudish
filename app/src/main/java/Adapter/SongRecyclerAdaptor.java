package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.music.cloudish.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import Else.Song;
import Else.Utility;

public class SongRecyclerAdaptor extends RecyclerView.Adapter<SongRecyclerAdaptor.SongViewHolder> {

    private int selectedPos;
    private Context context;
    private List<Song> li;
    private RecyclerItemClickListener listener;

    public SongRecyclerAdaptor(Context context, List<Song> li, RecyclerItemClickListener listener) {
        this.context = context;
        this.li = li;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.song_row_item, parent, false);

        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        Song s = li.get(position);

        if (s!=null){
            if (selectedPos == position){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.abuabu));
                holder.status.setVisibility(View.VISIBLE);
            }else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.oren));
                holder.status.setVisibility(View.INVISIBLE);
            }
        }

        holder.title.setText(s.getSongTitle());
        holder.artist.setText(s.getArtist());
        String temp = Utility.convertDur(Long.parseLong(s.getSongDuration()));
        holder.duration.setText(temp);
        holder.bind(s,listener);

    }

    @Override
    public int getItemCount() {
        return li.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        TextView title, duration, artist;
        ImageView status;


        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            title= itemView.findViewById(R.id.mTitle);
            duration=itemView.findViewById(R.id.mDuration);
            artist=itemView.findViewById(R.id.mArtist);
            status=itemView.findViewById(R.id.musicstatus);
        }

        public void bind(Song s, RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClickListener(s, getAdapterPosition());
                }
            });
        }
    }

    public interface RecyclerItemClickListener {
        void OnClickListener (Song s, int pos);

    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }
}
