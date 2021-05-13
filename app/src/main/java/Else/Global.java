package Else;

import androidx.fragment.app.Fragment;

import com.example.jean.jcplayer.view.JcPlayerView;
import com.music.cloudish.Library_F;

import java.util.ArrayList;
import java.util.List;

import Adapter.SongRecyclerAdaptor;

public class Global {
    public static String curAlbum="";
    public static String curCat="";
    public static String album="";
    public static String cat="";
    public static List<Song> li=new ArrayList<>();
    public static SongRecyclerAdaptor adapter;
    public  static Library_F flib = new Library_F();
    public static JcPlayerView jcpg;
}
