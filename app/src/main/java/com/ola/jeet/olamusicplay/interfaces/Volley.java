package com.ola.jeet.olamusicplay.interfaces;

import com.ola.jeet.olamusicplay.models.SongItem;

import java.util.ArrayList;

/**
 * Created by jeet on 12/18/2017.
 */

public interface Volley {
    interface GetSong{
        void onGetSong(ArrayList<SongItem> songItems,String rtrnValue);
    }
}
