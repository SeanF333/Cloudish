 package Adapter;

import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.cloudish.R;
import com.music.cloudish.ViewConcertDetail_A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import Else.Concert;

public class ConcertRecyclerAdapter extends RecyclerView.Adapter<ConcertRecyclerAdapter.ConcertViewHolder> {

    ArrayList<Concert> concertList;
    View view;

    public ConcertRecyclerAdapter(ArrayList<Concert> concertList) {
        this.concertList = concertList;
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.concert_cardview, parent, false);
        ConcertViewHolder concertViewHolder = new ConcertViewHolder(view);
        return concertViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, int position) {
        Concert concert = concertList.get(position);
        holder.bindConcert(concert);
    }

    @Override
    public int getItemCount() {
        return concertList.size();
    }

    public class ConcertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RelativeLayout layout;
        ImageView image;
        TextView title, detail, date;

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.layout);
            this.image = itemView.findViewById(R.id.concert_image);
            this.title = itemView.findViewById(R.id.concert_title);
            this.detail = itemView.findViewById(R.id.concert_detail);
            this.date = itemView.findViewById(R.id.concert_date);
        }

        public void bindConcert(Concert concert) {
            String concert_name = concert.getName();
            String concert_detail = concert.getDescription();
            String image_url = concert.getImageurl();
            Date concert_date = concert.getDate();

            title.setText(concert_name);
            detail.setText(concert_detail);
            date.setText(String.valueOf(concert_date));

            if(image_url!=null){
                Glide.with(view.getContext()).load(image_url).into(image);
            }

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(view.getContext(), ViewConcertDetail_A.class);
                    i.putExtra("concertId", concert.getId());
                    i.putExtra("concertMadeBy", concert.getUserId());
                    view.getContext().startActivity(i);
                }
            });

        }

        @Override
        public void onClick(View v) {
            Log.d("clicked", "clicked");
        }
    }
}
