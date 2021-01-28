package com.example.mazzika.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mazzika.Fragments.SongsFragment;
import com.example.mazzika.Track.MediaNotification;
import com.example.mazzika.R;
import com.example.mazzika.Services.NotificationReceiver;
import com.example.mazzika.Services.OnClearFromRecentService;
import com.example.mazzika.Track.RepeatType;
import com.example.mazzika.TabFragmentAdapter;
import com.example.mazzika.Track.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    private BroadcastReceiver broadcastReceiver;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabFragmentAdapter tabFragmentAdapter;
    private LinearLayout bottomPlayerSheetLayout;
    private RelativeLayout quickSongAccessLayout;
    private RelativeLayout bottomContextMenuLayout;
    private LinearLayout contextMenuContainerLayout;
    private View contextMenuHiddenHelper;
    private BottomSheetBehavior bottomPlayerSheetBehavior;
    private BottomSheetBehavior bottomContextMenuBehavior;
    private SeekBar seekBar;
    private ImageView horizontalRuleImage;
    private ImageView playImage;
    private ImageView nextImage;
    private ImageView previousImage;
    private ImageButton quickPlayImageButton;
    private ImageButton favoriteImageButton;
    private ImageButton repeatImageButton;
    private ImageButton moreOptionsImageButton;
    private TextView quickSongNameText;
    private TextView songNameText;
    private TextView songAlbumText;
    private TextView songCurrentTimeText;
    private TextView songEndTimeText;
    private TextView contextMenuAddToPlaylistText;
    private TextView contextMenuSendSongText;
    private TextView contextMenuSetAsRingtoneText;
    private TextView contextMenuViewDetailsText;
    private TextView contextMenuDeleteSongText;
    private TextView contextMenuCancelText;
    private SongsFragment songsFragment;
    private Song currentSong;
    private RepeatType repeatType;
    private boolean wasPaused;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkViewsById();
        setListeners();
        tabFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager(), MainActivity.this);
        bottomPlayerSheetBehavior = BottomSheetBehavior.from(bottomPlayerSheetLayout);
        bottomContextMenuBehavior = BottomSheetBehavior.from(bottomContextMenuLayout);
        viewPager.setAdapter(tabFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        createNotificationChannel();
        wasPaused = false;
        isFavorite = false;
        repeatType = RepeatType.REPEAT_ALL;
    }

    private void linkViewsById() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.tabs_pager_view);
        seekBar = findViewById(R.id.seek_bar_sheet);
        bottomPlayerSheetLayout = findViewById(R.id.layout_bottom_player_sheet);
        quickSongAccessLayout = findViewById(R.id.layout_quick_song_access_container);
        bottomContextMenuLayout = findViewById(R.id.layout_bottom_context_menu);
        contextMenuContainerLayout = findViewById(R.id.layout_context_menu_container);
        contextMenuHiddenHelper = findViewById(R.id.view_context_menu_hideable_helper);
        horizontalRuleImage = findViewById(R.id.image_sheet_horizontal_rule);
        playImage = findViewById(R.id.image_sheet_play);
        nextImage = findViewById(R.id.image_sheet_next);
        previousImage = findViewById(R.id.image_sheet_previous);
        quickPlayImageButton = findViewById(R.id.image_button_sheet_quick_play);
        favoriteImageButton = findViewById(R.id.image_button_sheet_favorite);
        repeatImageButton = findViewById(R.id.image_button_sheet_repeat);
        moreOptionsImageButton = findViewById(R.id.image_button_sheet_options);
        quickSongNameText = findViewById(R.id.text_sheet_quick_song_name);
        songNameText = findViewById(R.id.text_sheet_song_name);
        songAlbumText = findViewById(R.id.text_sheet_song_album);
        songCurrentTimeText = findViewById(R.id.text_sheet_current_time);
        songEndTimeText = findViewById(R.id.text_sheet_end_time);
        contextMenuAddToPlaylistText = findViewById(R.id.text_context_menu_add_to_playlist);
        contextMenuSendSongText = findViewById(R.id.text_context_menu_send_song);
        contextMenuSetAsRingtoneText = findViewById(R.id.text_context_menu_set_as_ringtone);
        contextMenuViewDetailsText = findViewById(R.id.text_context_menu_view_details);
        contextMenuDeleteSongText = findViewById(R.id.text_context_menu_delete_song);
        contextMenuCancelText = findViewById(R.id.text_context_menu_cancel);
    }

    private void setListeners() {
        bottomPlayerSheetLayout.setOnClickListener(v -> {
        });
        bottomContextMenuLayout.setOnClickListener(v -> {
        });
        horizontalRuleImage.setOnClickListener(v -> {
            if (bottomPlayerSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomPlayerSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else
                bottomPlayerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        quickSongAccessLayout.setOnClickListener(v -> {
            if (bottomPlayerSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomPlayerSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        playImage.setOnClickListener(v -> {
            songIsPaused(wasPaused);
        });
        nextImage.setOnClickListener(v -> {
            songsFragment.manageNextSong();
        });
        previousImage.setOnClickListener(v -> {
            songsFragment.managePreviousSong();
        });
        quickPlayImageButton.setOnClickListener(v -> {
            songIsPaused(wasPaused);
        });
        favoriteImageButton.setOnClickListener(v -> {
            if (!isFavorite) {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_filled);
                Toast.makeText(MainActivity.this, "Added to Favorites !!", Toast.LENGTH_SHORT).show();
                isFavorite = true;
            } else {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(MainActivity.this, "Removed from Favorites !!", Toast.LENGTH_SHORT).show();
                isFavorite = false;
            }
        });
        repeatImageButton.setOnClickListener(v -> {
            if (repeatType == RepeatType.REPEAT_ALL) {
                repeatImageButton.setImageResource(R.drawable.ic_repeat_one);
                repeatType = RepeatType.REPEAT_ONE;
                Toast.makeText(MainActivity.this, "Repeat This !", Toast.LENGTH_SHORT).show();
            } else if (repeatType == RepeatType.REPEAT_ONE) {
                repeatImageButton.setImageResource(R.drawable.ic_shuffle);
                repeatType = RepeatType.SHUFFLE;
                Toast.makeText(MainActivity.this, "Shuffle List !", Toast.LENGTH_SHORT).show();
            } else {
                repeatImageButton.setImageResource(R.drawable.ic_repeat_all);
                repeatType = RepeatType.REPEAT_ALL;
                Toast.makeText(MainActivity.this, "Repeat List !", Toast.LENGTH_SHORT).show();
            }
        });
        moreOptionsImageButton.setOnClickListener(v -> {
            manageContextMenu(true);
        });
        contextMenuHiddenHelper.setOnClickListener(v -> {
            manageContextMenu(true);
        });
        contextMenuAddToPlaylistText.setOnClickListener(v -> {
            Toast.makeText(this, "ADD TO PLAYLIST CLICKED", Toast.LENGTH_SHORT).show();
        });
        contextMenuSendSongText.setOnClickListener(v -> {
            Toast.makeText(this, "SEND SONG CLICKED", Toast.LENGTH_SHORT).show();
        });
        contextMenuSetAsRingtoneText.setOnClickListener(v -> {
            Toast.makeText(this, "SET AS RINGTONE CLICKED", Toast.LENGTH_SHORT).show();
        });
        contextMenuViewDetailsText.setOnClickListener(v -> {
            Toast.makeText(this, "VIEW DETAILS CLICKED", Toast.LENGTH_SHORT).show();
        });
        contextMenuDeleteSongText.setOnClickListener(v -> {
            Toast.makeText(this, "DELETE SONG CLICKED", Toast.LENGTH_SHORT).show();
        });
        contextMenuCancelText.setOnClickListener(v -> {
            manageContextMenu(true);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getExtras().getString(NotificationReceiver.BROADCAST_ACTION_NAME);
                    switch (action) {
                        case MediaNotification.ACTION_PREVIOUS:
                            songsFragment.managePreviousSong();
                            break;
                        case MediaNotification.ACTION_PLAY:
                            songIsPaused(wasPaused);
                            break;
                        case MediaNotification.ACTION_NEXT:
                            songsFragment.manageNextSong();
                            break;
                    }
                }
            };
            NotificationChannel channel = new NotificationChannel(MediaNotification.CHANNEL_ID,
                    MediaNotification.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
            registerReceiver(broadcastReceiver, new IntentFilter(NotificationReceiver.INTENT_ACTION_NAME));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
    }

    public void manageContextMenu(boolean fromSheet) {
        if (fromSheet)
            contextMenuContainerLayout.setBackgroundColor(getColor(R.color.colorPrimary));
        else
            contextMenuContainerLayout.setBackgroundColor(getColor(R.color.colorFont2));

        if (bottomContextMenuBehavior.getState() == bottomContextMenuBehavior.STATE_COLLAPSED)
            bottomContextMenuBehavior.setState(bottomContextMenuBehavior.STATE_EXPANDED);
        else
            bottomContextMenuBehavior.setState(bottomContextMenuBehavior.STATE_COLLAPSED);
    }

    public void setInPlayerSongInfo(Song song) {
        songNameText.setText(song.getName());
        songAlbumText.setText(song.getAlbum());
        quickSongNameText.setText(song.getName());
        playImage.setImageResource(R.drawable.ic_pause_arrow);
        quickPlayImageButton.setImageResource(R.drawable.ic_pause_arrow);
        wasPaused = true;
        seekBar.setMax((int) song.getDuration() / 1000);
        songEndTimeText.setText(getFormattedTime((int) song.getDuration()));
        this.currentSong = song;
        MediaNotification.createNotification(MainActivity.this, currentSong,
                R.drawable.ic_pause_arrow);
    }

    public void songIsPaused(boolean paused) {
        if (songsFragment.getMediaPlayer() != null){
            if (paused) {
                playImage.setImageResource(R.drawable.ic_play_arrow);
                quickPlayImageButton.setImageResource(R.drawable.ic_play_arrow);
                wasPaused = false;
                if (currentSong!=null)
                    MediaNotification.createNotification(MainActivity.this, currentSong,
                            R.drawable.ic_play_arrow);
            } else {
                playImage.setImageResource(R.drawable.ic_pause_arrow);
                quickPlayImageButton.setImageResource(R.drawable.ic_pause_arrow);
                wasPaused = true;
                if (currentSong!=null)
                    MediaNotification.createNotification(MainActivity.this, currentSong,
                            R.drawable.ic_pause_arrow);
            }
        }
        songsFragment.manageSongState();
    }

    public void setSongsFragment(SongsFragment songsFragment) {
        this.songsFragment = songsFragment;
        Handler handler = new Handler();
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (songsFragment.getMediaPlayer() != null) {
                    seekBar.setProgress(songsFragment.getMediaPlayer().getCurrentPosition() / 1000);
                    songCurrentTimeText.setText(getFormattedTime(songsFragment.getMediaPlayer().getCurrentPosition()));
                }
                handler.postDelayed(this, 1000);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (songsFragment.getMediaPlayer() != null && fromUser) {
                    songsFragment.getMediaPlayer().seekTo(progress * 1000);
                    songCurrentTimeText.setText(getFormattedTime(progress * 1000));
                }
            }
        });
    }

    private String getFormattedTime(int milliseconds) {
        String formattedString = "";
        int minutes = milliseconds / 60000;
        int seconds = (milliseconds % 60000) / 1000;
        if (minutes < 10) formattedString += "0";
        formattedString += String.valueOf(minutes);
        formattedString += ":";
        if (seconds < 10) formattedString += "0";
        formattedString += String.valueOf(seconds);
        return formattedString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.cancelAll();
        unregisterReceiver(broadcastReceiver);
    }
}