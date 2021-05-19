package Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.music.cloudish.R;

import java.util.ArrayList;
import java.util.List;

import Else.Album;
import Else.Song;
import Listener.AlbumListener;


public class AlbumRecylerAdapter extends RecyclerView.Adapter<AlbumRecylerAdapter.AlbumViewHolder>{

    ArrayList<Album> albumArrayList;
    AlbumListener albumListener;
    View view;

    public AlbumRecylerAdapter(ArrayList<Album> albumArrayList, AlbumListener albumListener) {
        this.albumArrayList = albumArrayList;
        this.albumListener = albumListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_cardview, parent, false);
        AlbumRecylerAdapter.AlbumViewHolder aViewHolder = new AlbumRecylerAdapter.AlbumViewHolder(view);
        return aViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album a = albumArrayList.get(position);
        holder.bindAlbum(a);
    }

    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

    public List<Album> getSelectedAlbum(){
        List<Album> selected = new ArrayList<>();

        for(Album a : albumArrayList){
            if(a.isSelected){
                selected.add(a);
            }
        }

        return selected;

    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView imageSelected;
        LinearLayout album_layout;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.albummm_name);
            this.imageSelected = itemView.findViewById(R.id.imageSelected);
            this.album_layout = itemView.findViewById(R.id.album_layout);
        }

        void bindAlbum(final Album a){

            String albumname = a.getAlbumname();
            name.setText(albumname);

            if(a.isSelected){
                imageSelected.setVisibility(View.VISIBLE);
            }else{
                imageSelected.setVisibility(View.GONE);
            }

            album_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("albumClicked", albumname);
                    if(a.isSelected){
                        imageSelected.setVisibility(View.GONE);
                        a.isSelected = false;

                        if(getSelectedAlbum().size() == 0){
                            albumListener.onAlbumAction(false);
                        }

                    }else if(!a.isSelected){
                        imageSelected.setVisibility(View.VISIBLE);
                        a.isSelected = true;
                        albumListener.onAlbumAction(true);
                    }
                }
            });


        }
    }

}
