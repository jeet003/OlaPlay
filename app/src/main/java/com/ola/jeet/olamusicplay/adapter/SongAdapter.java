package com.ola.jeet.olamusicplay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ola.jeet.olamusicplay.R;
import com.ola.jeet.olamusicplay.models.SongItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeet on 18/12/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> implements Filterable{

    private ArrayList<SongItem> songDTOs,filtersongDTOs;
    private Context context;
    private SongsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView song_name, song_url, song_artist;
        public ImageView song_image;
        public ImageButton imageButton;
        public MyViewHolder(View view) {
            super(view);
            song_name=(TextView) view.findViewById(R.id.song_name);
            song_artist=(TextView) view.findViewById(R.id.song_artist);
            song_image=(ImageView) view.findViewById(R.id.song_image);
            imageButton=(ImageButton) view.findViewById(R.id.btnPlay);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSongSelected(filtersongDTOs.get(getAdapterPosition()));
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSongSelected(filtersongDTOs.get(getAdapterPosition()));
                }
            });

        }
    }

    public SongAdapter(ArrayList<SongItem> songDTOs,Context context,SongsAdapterListener listener)
    {
        this.songDTOs=songDTOs;
        this.context=context;
        this.filtersongDTOs = songDTOs;
        this.listener=listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SongItem songItem = filtersongDTOs.get(position);
        holder.song_name.setText(songItem.getSongName());
        holder.song_artist.setText(songItem.getSongArtist());

        Glide.with(context)
                .load(songItem.getSongImage())
                .into(holder.song_image);
    }

    @Override
    public int getItemCount() {
        return filtersongDTOs.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtersongDTOs = songDTOs;
                } else {
                    ArrayList<SongItem> filteredList = new ArrayList<>();
                    for (SongItem row : songDTOs) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSongName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtersongDTOs = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtersongDTOs;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtersongDTOs = (ArrayList<SongItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface SongsAdapterListener {
        void onSongSelected(SongItem songItem);
    }

}
