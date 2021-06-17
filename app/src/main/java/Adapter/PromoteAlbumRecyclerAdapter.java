package Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.music.cloudish.R;

import java.util.ArrayList;
import java.util.List;

import Else.Album;
import Listener.AlbumListener;

public class PromoteAlbumRecyclerAdapter extends RecyclerView.Adapter<PromoteAlbumRecyclerAdapter.PromoteAlbumViewHolder>{

    ArrayList<Album> albumArrayList;
    AlbumListener albumListener;
    View view;

    public PromoteAlbumRecyclerAdapter(ArrayList<Album> albumArrayList, AlbumListener albumListener) {
        this.albumArrayList = albumArrayList;
        this.albumListener = albumListener;
    }

    @NonNull
    @Override
    public PromoteAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card_item, parent, false);
        PromoteAlbumViewHolder promoteAlbumViewHolder = new PromoteAlbumViewHolder(view);
        return promoteAlbumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PromoteAlbumViewHolder holder, int position) {
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

    public class PromoteAlbumViewHolder extends RecyclerView.ViewHolder{

        TextView tv, count;
        ImageView iv,love;
        CardView cv;
        LinearLayout ll;

        public PromoteAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            tv= itemView.findViewById(R.id.titlealbum);
            iv=itemView.findViewById(R.id.albumimg);
            cv=itemView.findViewById(R.id.albumitem);
            ll=itemView.findViewById(R.id.bckg_album);
            count=itemView.findViewById(R.id.countlike);
            love = itemView.findViewById(R.id.love);
        }

        public void bindAlbum(final Album a){
            DatabaseReference mAlbum = FirebaseDatabase.getInstance().getReference().child("Like").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(a.getAlbumname());
            mAlbum.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        count.setText(String.valueOf(task.getResult().getChildrenCount()));
                        tv.setText(a.getAlbumname());
                        Glide.with(view.getContext()).load(a.getImageurl()).into(iv);
                        ll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(a.isSelected){
                                    ll.setBackgroundColor(Color.WHITE);
                                    love.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.pop_orange));
                                    tv.setTextColor(Color.BLACK);
                                    count.setTextColor(ContextCompat.getColor(view.getContext(), R.color.grey_out));
                                    a.isSelected = false;
                                    if(getSelectedAlbum().size() == 0){
                                        albumListener.onAlbumAction(false);
                                    }
                                }else if(!a.isSelected){
                                    ll.setBackgroundColor(Color.parseColor("#3B117B"));
                                    love.setColorFilter(Color.WHITE);
                                    tv.setTextColor(Color.WHITE);
                                    count.setTextColor(Color.WHITE);
                                    a.isSelected = true;
                                    albumListener.onAlbumAction(true);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
