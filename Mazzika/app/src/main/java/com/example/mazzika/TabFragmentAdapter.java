package com.example.mazzika;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mazzika.Fragments.AlbumsFragment;
import com.example.mazzika.Fragments.ArtistsFragment;
import com.example.mazzika.Fragments.PlaylistsFragment;
import com.example.mazzika.Fragments.SongsFragment;
import com.example.mazzika.Activities.MainActivity;

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private final int numOfFragments = 4;
    private MainActivity mainActivity;
    public TabFragmentAdapter(@NonNull FragmentManager fm, MainActivity mainActivity) {
        super(fm);
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                SongsFragment songsFragment = new SongsFragment(mainActivity);
                mainActivity.setSongsFragment(songsFragment);
                return songsFragment;
            case 1:
                ArtistsFragment artistsFragment = new ArtistsFragment();
                return artistsFragment;
            case 2:
                AlbumsFragment albumsFragment = new AlbumsFragment();
                return albumsFragment;
            case 3:
                PlaylistsFragment playlistsFragment = new PlaylistsFragment();
                return playlistsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfFragments;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Songs";
            case 1:
                return "Artists";
            case 2:
                return "Albums";
            case 3:
                return "Playlists";
            default:
                return null;
        }
    }
}
