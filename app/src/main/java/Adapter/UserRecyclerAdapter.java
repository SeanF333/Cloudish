package Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.cloudish.AddConcert_Second_A;
import com.music.cloudish.Detail_Playlist_A;
import com.music.cloudish.R;
import com.music.cloudish.ViewConcertDetail_A;

import java.util.ArrayList;
import java.util.List;

import Else.User;
import Listener.UserListener;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    ArrayList<User> userArrayList;
    UserListener userListener;
    View view;

    public UserRecyclerAdapter(ArrayList<User> userArrayList, UserListener userListener) {
        this.userArrayList = userArrayList;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cardview, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = userArrayList.get(position);
        holder.bindUser(user);

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public List<User> getSelectedUser(){
        List<User> selectedUser = new ArrayList<>();

        for(User user : userArrayList){
            if(user.isSelected){
                selectedUser.add(user);
            }
        }

        return selectedUser;

    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        LinearLayout user_layout;
        ImageView user_image, image_selected;
        TextView user_name;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            // Hooks
            user_layout = itemView.findViewById(R.id.user_layout);
            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            image_selected = itemView.findViewById(R.id.imageSelected);
        }

        void bindUser(final User user){
            String profile_url = user.getImageurl();

            Glide.with(view.getContext()).load(profile_url).into(user_image);
            user_name.setText(user.getFullname());
            user_name.setGravity(Gravity.CENTER);


            if(user.isSelected){
                image_selected.setVisibility(View.VISIBLE);
            }else{
                image_selected.setVisibility(View.GONE);
            }

            user_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getContext().getClass().equals(AddConcert_Second_A.class)){
                        Log.d("userClicked", user.getFullname());
                        if(user.isSelected){
                            image_selected.setVisibility(View.GONE);
                            user.isSelected = false;
                            if(getSelectedUser().size() == 0){
                                userListener.onUserAction(false);
                            }
                        }else{
                            image_selected.setVisibility(View.VISIBLE);
                            user.isSelected = true;
                            userListener.onUserAction(true);
                        }
                    }
                }
            });

            user_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

        }

    }

}
