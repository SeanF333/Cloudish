package Else;

import java.io.Serializable;
import java.util.List;

public class Performance implements Serializable {

    private User user;
    private List<Song> songList;

    public Performance(User user, List<Song> songList) {
        this.user = user;
        this.songList = songList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
