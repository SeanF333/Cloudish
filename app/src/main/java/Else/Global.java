package Else;

import androidx.fragment.app.Fragment;

import com.example.jean.jcplayer.view.JcPlayerView;
import com.music.cloudish.Album_Result_F;
import com.music.cloudish.Library_F;

import java.util.ArrayList;
import java.util.List;

import Adapter.SongRecyclerAdaptor;

public class Global {
    public static String curAlbum="";
    public static String extAlbum="";
    public static String ownerUser="";
    public static String curCat="";
    public static String extCat="";
    public static String album="";
    public static String cat="";
    public static int modealbum = 0;
    public static int modealbumglobal = 0;
    public static List<Song> li=new ArrayList<>();
    public static SongRecyclerAdaptor adapter;
    public  static Library_F flib = new Library_F();
    public static Album_Result_F arf = new Album_Result_F();
    public static JcPlayerView jcpg;
    public static int innermode=0;
    public static String songid="";
    public static int postoset=0;
    public static int idx=0;
}
