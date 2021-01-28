package com.example.mazzika.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mazzika.Activities.MainActivity;
import com.example.mazzika.R;
import com.example.mazzika.Track.Song;
import com.example.mazzika.SongsAdapter;

import java.util.ArrayList;

public class SongsFragment extends Fragment implements SongsAdapter.OnSongClickListener {

    private static final int PERMISSION_REQUEST = 1;
    private ArrayList<Song> songsArrayList;
    private TextView noSongsText;
    private RecyclerView songsRecyclerView;
    private SongsAdapter songsAdapter;
    private MediaPlayer mediaPlayer;
    private MainActivity mainActivity;
    private Context context;
    boolean initializedOnce;
    private MediaPlayer.OnCompletionListener completionListener = mp -> manageNextSong();

    public SongsFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        noSongsText = view.findViewById(R.id.text_no_songs_available);
        songsRecyclerView = view.findViewById(R.id.recycler_view_songs);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager
                (view.getContext(), LinearLayoutManager.VERTICAL, false));
        songsArrayList = new ArrayList<>();
        context = view.getContext();
        requestUserPermission(view);
        if (songsArrayList.isEmpty())
            noSongsText.setVisibility(View.VISIBLE);
        else
            noSongsText.setVisibility(View.GONE);

        if (!initializedOnce && songsArrayList.size() > 0){
            // put the first song in the player track and initially pause it
            manageSongState();
            mainActivity.songIsPaused(true);
            initializedOnce = true;
        }
        return view;
    }

    private void requestUserPermission(View view){
        if (ActivityCompat.checkSelfPermission(view.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            setRecyclerView();
        else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(context, "Storage permission is needed to play music.",
                        Toast.LENGTH_LONG).show();
            }
            requestPermissions
                    (new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "Permission Granted !", Toast.LENGTH_SHORT).show();
                noSongsText.setVisibility(View.GONE);
                setRecyclerView();
            }
            else
                Toast.makeText(context, "No Permission Granted !", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRecyclerView() {
        setSongs();
        songsAdapter = new SongsAdapter(songsArrayList, this, getActivity(), mainActivity);
        songsRecyclerView.setAdapter(songsAdapter);
    }

    private void setSongs(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = { MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA};
        Cursor cursor = contentResolver.query(songUri, proj,
                MediaStore.Audio.Media.DURATION + ">= 30000", null, null);

        if (cursor != null && cursor.moveToFirst()){
            int songName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                Song song = new Song(cursor.getString(songName), cursor.getString(songAlbum),
                        cursor.getString(songDuration), cursor.getString(songPath));
                songsArrayList.add(song);
            } while(cursor.moveToNext());
        }
    }

    @Override
    public void onSongClick(int position) {
        try {
            Song song = songsArrayList.get(position);
            String path = song.getPath();
            releaseMedia();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(completionListener);
            mainActivity.setInPlayerSongInfo(song);
            songsAdapter.notifyDataSetChanged(); // make effect to recycler view's adapter
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "No Songs To Show Or To Play !", Toast.LENGTH_LONG).show();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void manageSongState(){
        if (mediaPlayer==null){
            songsAdapter.SELECTED_IDX = 0;
            onSongClick(0);
        }
        else
            if(mediaPlayer.isPlaying())
                mediaPlayer.pause();
            else
                mediaPlayer.start();
    }

    public void manageNextSong(){
        songsAdapter.SELECTED_IDX = (songsAdapter.SELECTED_IDX + 1)%songsAdapter.getItemCount();
        onSongClick(songsAdapter.SELECTED_IDX);
    }

    public void managePreviousSong(){
        if (songsAdapter.SELECTED_IDX < 1)
            songsAdapter.SELECTED_IDX = songsAdapter.getItemCount() - 1;
        else
            songsAdapter.SELECTED_IDX -= 1;
        onSongClick(songsAdapter.SELECTED_IDX);
    }

    private void releaseMedia(){
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMedia();
    }
}