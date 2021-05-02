package Adapter;

import android.view.View;

import java.util.ArrayList;

import Else.Song;
import Listener.SongListener;

public class SongRecylerAdapter {

    ArrayList<Song> songArrayList;
    SongListener songListener;
    View view;

    public SongRecylerAdapter(ArrayList<Song> songArrayList, SongListener songListener) {
        this.songArrayList = songArrayList;
        this.songListener = songListener;
    }


    // commit




}
