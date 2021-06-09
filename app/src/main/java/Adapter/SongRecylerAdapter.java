package Adapter;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.music.cloudish.AddConcert_Second_A;
import com.music.cloudish.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Else.Song;
import Listener.SongListener;

public class SongRecylerAdapter extends RecyclerView.Adapter<SongRecylerAdapter.SongViewHolder>{

    ArrayList<Song> songArrayList;
    SongListener songListener;
    View view;

    public SongRecylerAdapter(ArrayList<Song> songArrayList, SongListener songListener) {
        this.songArrayList = songArrayList;
        this.songListener = songListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_cardview, parent, false);
        SongViewHolder songViewHolder = new SongViewHolder(view);
        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        Song song = songArrayList.get(position);
        holder.bindSong(song);

    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public List<Song> getSelectedSong(){
        List<Song> selectedSong = new ArrayList<>();

        for(Song s : songArrayList){
            if(s.isSelected){
                selectedSong.add(s);
            }
        }

        return selectedSong;

    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView imageSelected;
        LinearLayout song_layout;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.track_name);
            this.imageSelected = itemView.findViewById(R.id.imageSelected);
            this.song_layout = itemView.findViewById(R.id.track_layout);
        }

        void bindSong(final Song song){

            String songtitle = song.getSongTitle();
            name.setText(songtitle);

            if(song.isSelected){
                imageSelected.setVisibility(View.VISIBLE);
            }else{
                imageSelected.setVisibility(View.GONE);
            }

            song_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("songClicked", songtitle);
                    if(v.getContext().getClass().equals(AddConcert_Second_A.class)){
                        if(song.isSelected){
                            imageSelected.setVisibility(View.GONE);
                            song.isSelected = false;

                            if(getSelectedSong().size() == 0){
                                songListener.onSongAction(false);
                            }

                        }else if(!song.isSelected){
                            imageSelected.setVisibility(View.VISIBLE);
                            song.isSelected = true;
                            songListener.onSongAction(true);
                        }
                    }
                }
            });
        }
    }
}
