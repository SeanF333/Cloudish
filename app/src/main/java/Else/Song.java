package Else;

public class Song {

    public String songsCategory, songTitle, artist, album_name, songDuration, songLink, mKey, imgLink;

    public Song(String songsCategory, String songTitle, String artist, String album_name, String songDuration, String songLink, String imgLink) {
        if (songTitle==null || songTitle.trim().equals("")){
            songTitle="No Title";
        }
        if (artist==null||artist.trim().equals("")){
            artist="No Artist";
        }

        this.songsCategory = songsCategory;
        this.songTitle = songTitle;
        this.artist = artist;
        this.album_name = album_name;
        this.songDuration = songDuration;
        this.songLink = songLink;
        this.imgLink = imgLink;

    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getSongsCategory() {
        return songsCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
