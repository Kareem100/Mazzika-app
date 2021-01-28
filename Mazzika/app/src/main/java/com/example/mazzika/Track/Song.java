package com.example.mazzika.Track;

public class Song {

    private String name;
    private String album;
    private float duration;
    private String path;

    public Song(String name, String album, String duration, String path){
        this.name = name;
        this.album = album;
        this.duration = Float.valueOf(duration);
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public float getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }
}
