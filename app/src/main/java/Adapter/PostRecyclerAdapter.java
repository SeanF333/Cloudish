package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.cloudish.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import Else.Post;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder> {

    ArrayList<Post> postlist;
    View view;

    public PostRecyclerAdapter(ArrayList<Post> postlist) {
        this.postlist = postlist;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_cardview, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postlist.get(position);
        holder.bindPost(post);
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    public class PostViewHolder extends  RecyclerView.ViewHolder{

        ConstraintLayout main_layout;
        ImageView post_image, poster_image, love_btn;
        TextView poster_name, date, caption, count_likes;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            this.main_layout = itemView.findViewById(R.id.main_layout);
            this.poster_image = itemView.findViewById(R.id.profile_img);
            this.post_image = itemView.findViewById(R.id.post_img);
            this.love_btn = itemView.findViewById(R.id.love_btn);
            this.count_likes = itemView.findViewById(R.id.count_likes);
            this.date = itemView.findViewById(R.id.post_date);
            this.poster_name = itemView.findViewById(R.id.poster_name);
            this.caption = itemView.findViewById(R.id.caption);
        }

        public void bindPost(Post post){

            String posterimageurl = post.getPoster_imageurl();
            String postername = post.getPoster_name();
            String imageUrl = post.getImageurl();
            LocalDateTime _date = post.getDatetime();
            String _caption = post.getCaption();
            String posterid = post.getPoster_id();
            Integer countlikes = post.getCountLikes();

            count_likes.setText(String.valueOf(countlikes));
            poster_name.setText(postername);
            caption.setText(_caption);

            if(posterimageurl!=null){
                Glide.with(view.getContext()).load(posterimageurl).into(poster_image);
            }
            if(imageUrl!=null){
                Glide.with(view.getContext()).load(imageUrl).into(post_image);
            }
        }
    }
}
