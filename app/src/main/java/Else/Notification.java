package Else;

public class Notification {

    private String userid,publisherid,text,album_name,mode;

    public Notification(String userid, String publisherid, String text, String album_name, String mode) {
        this.userid = userid;
        this.publisherid = publisherid;
        this.text = text;
        this.album_name = album_name;
        this.mode = mode;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
