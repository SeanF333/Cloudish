package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
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
    private List<Song> arrsong = new ArrayList<>();
    private List<String> arrno = new ArrayList<>();
    private RecyclerItemClickListener listener;
    private int mode=0;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    public void resetarr(){
        arrno.clear();
    }

    public void resetarrsong(){
        arrsong.clear();
    }

    public List<Song> getArrsong() {
        return arrsong;
    }

    public void setArrsong(List<Song> arrsong) {
        this.arrsong = arrsong;
    }

    public List<String> getArrno() {
        return arrno;
    }

    public void setArrno(List<String> arrno) {
        this.arrno = arrno;
    }

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

        if (mode==0){
            if (s!=null){
                holder.status.setVisibility(View.VISIBLE);
                if (selectedPos == position){
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.abuabu));
                    holder.status.setVisibility(View.VISIBLE);
                }else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.oren));
                    holder.status.setVisibility(View.INVISIBLE);
                }
            }

            holder.duration.setVisibility(View.VISIBLE);
            holder.cb.setVisibility(View.INVISIBLE);


        }else {
            holder.status.setVisibility(View.INVISIBLE);
            holder.duration.setVisibility(View.INVISIBLE);
            holder.cb.setVisibility(View.VISIBLE);

        }

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    arrno.add(li.get(position).getSongLink());
                    arrsong.add(li.get(position));
                } else {
                    arrno.remove(li.get(position).getSongLink());
                    arrsong.remove(li.get(position));
                }
            }
        });

        if (holder.cb.isChecked()){
            arrno.add(li.get(position).getSongLink());
            arrsong.add(li.get(position));
        }else {
            arrno.remove(li.get(position).getSongLink());
            arrsong.remove(li.get(position));
        }

        holder.title.setText(s.getSongTitle());
        holder.artist.setText(s.getArtist());
        String temp = Utility.convertDur(Long.parseLong(s.getSongDuration()));
        holder.duration.setText(temp);
        holder.bind(s,listener);
        Glide.with(context).load(s.getImgLink()).into(holder.img);

    }

    @Override
    public int getItemCount() {
        return li.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        TextView title, duration, artist;
        ImageView status,img;
        CheckBox cb;
        RelativeLayout rl;


        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            title= itemView.findViewById(R.id.mTitle);
            duration=itemView.findViewById(R.id.mDuration);
            artist=itemView.findViewById(R.id.mArtist);
            status=itemView.findViewById(R.id.musicstatus);
            cb=itemView.findViewById(R.id.checkboxx);
            img=itemView.findViewById(R.id.dummyimagemusic);
            rl =itemView.findViewById(R.id.rlsong);

        }

        public void bind(Song s, RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode==0){
                        listener.OnClickListener(s, getAdapterPosition());
                    }

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
