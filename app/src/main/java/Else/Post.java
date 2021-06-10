package Else;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post {

    private String imageurl, caption, poster_id, poster_name, poster_imageurl, post_id;
    private LocalDateTime datetime;

    public Post(String post_id, String imageurl, String caption, String poster_id, LocalDateTime datetime, String poster_imageurl, String poster_name){
        this.post_id = post_id;
        this.imageurl = imageurl;
        this.caption = caption;
        this.poster_id = poster_id;
        this.datetime = datetime;
        this.poster_name = poster_name;
        this.poster_imageurl = poster_imageurl;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getPoster_imageurl() {
        return poster_imageurl;
    }

    public void setPoster_imageurl(String poster_imageurl) {
        this.poster_imageurl = poster_imageurl;
    }
}
