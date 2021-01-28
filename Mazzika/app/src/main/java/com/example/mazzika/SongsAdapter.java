package com.example.mazzika;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mazzika.Activities.MainActivity;
import com.example.mazzika.Track.Song;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder>{

    public static int SELECTED_IDX = -1; // initially none is selected
    private int songItems;
    private ArrayList<Song> songs;
    private OnSongClickListener onSongClickListener;
    private Activity songsFragmentActivity;
    private MainActivity mainActivity;

    public SongsAdapter(ArrayList<Song> songs, OnSongClickListener onSongClickListener,
                        Activity songsFragmentActivity, MainActivity mainActivity){
        this.songs = songs;
        this.songItems = songs.size();
        this.onSongClickListener = onSongClickListener;
        this.songsFragmentActivity = songsFragmentActivity;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_songs_list_item,
                parent, false);
        SongViewHolder viewHolder = new SongViewHolder(view, onSongClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song.getName(), song.getAlbum());

        // highlight the selected song
        if (SELECTED_IDX == position){
            holder.songNameView.setTextColor(ContextCompat.getColor(songsFragmentActivity,
                    R.color.colorAccent));
            holder.songNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    songsFragmentActivity.getResources().getDimension(R.dimen.text_size_medium));
            holder.songAlbumView.setTextColor(ContextCompat.getColor(songsFragmentActivity,
                    R.color.colorAccent));
            holder.songMoreOptionsButton.setColorFilter(ContextCompat.getColor(songsFragmentActivity,
                    R.color.colorAccent));
        }
        else {
            holder.songNameView.setTextColor(ContextCompat.getColor(songsFragmentActivity,
                    R.color.colorFont));
            holder.songNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    songsFragmentActivity.getResources().getDimension(R.dimen.text_size_small));
            holder.songAlbumView.setTextColor(ContextCompat.getColor(songsFragmentActivity,
                    R.color.colorFont2));
            holder.songMoreOptionsButton.setColorFilter(ContextCompat.getColor(songsFragmentActivity,
                    R.color.colorFont));
        }
    }

    @Override
    public int getItemCount() {
        return songItems;
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView songNameView;
        private TextView songAlbumView;
        private ImageButton songMoreOptionsButton;
        private OnSongClickListener onSongClickListener;

        public SongViewHolder(@NonNull View itemView, OnSongClickListener onSongClickListener) {
            super(itemView);
            songNameView = itemView.findViewById(R.id.text_song_item_name);
            songAlbumView = itemView.findViewById(R.id.text_song_item_album);
            songMoreOptionsButton = itemView.findViewById(R.id.image_button_item_more_options);
            songMoreOptionsButton.setOnClickListener(v -> mainActivity.manageContextMenu(false));
            itemView.setOnClickListener(this);
            this.onSongClickListener = onSongClickListener;
        }

        public void bind(String songName, String songAlbum){
            songNameView.setText(songName);
            songAlbumView.setText(songAlbum);
        }

        @Override
        public void onClick(View v) {
            SELECTED_IDX = getAdapterPosition();
            onSongClickListener.onSongClick(SELECTED_IDX);
        }
    }

    public interface OnSongClickListener{
        void onSongClick(int position);
    }
}
