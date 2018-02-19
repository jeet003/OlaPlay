package com.ola.jeet.olamusicplay.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jeet on 12/18/2017.
 */

public class SongItem implements Parcelable {
    private String songImage;
    private String songName;
    private String songArtist;
    private String url;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SongItem(){

    }
    protected SongItem(Parcel in){
        songImage=in.readString();
        songName=in.readString();
        songArtist=in.readString();
        url=in.readString();
        index=in.readInt();
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static final Creator<SongItem> CREATOR = new Creator<SongItem>() {
        @Override
        public SongItem createFromParcel(Parcel in) {
            return new SongItem(in);
        }

        @Override
        public SongItem[] newArray(int size) {
            return new SongItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songImage);
        dest.writeString(songName);
        dest.writeString(songArtist);
        dest.writeString(url);
        dest.writeInt(index);
    }

}
