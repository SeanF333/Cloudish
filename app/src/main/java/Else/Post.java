package Else;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post {

    private String imageurl, caption, poster_id, poster_name, poster_imageurl;
    private LocalDateTime datetime;
    private Integer countLikes;

    public Post(String imageurl, String caption, String poster_id, LocalDateTime datetime, String poster_imageurl, String poster_name, Integer countLikes ){
        this.imageurl = imageurl;
        this.caption = caption;
        this.poster_id = poster_id;
        this.datetime = datetime;
        this.countLikes = countLikes;
        this.poster_name = poster_name;
        this.poster_imageurl = poster_imageurl;
    }

    public String getPoster_name() {
        return poster_name;
    }

    public void setPoster_name(String poster_name) {
        this.poster_name = poster_name;
    }

    public Integer getCountLikes() {
        return countLikes;
    }

    public void setCountLikes(Integer countLikes) {
        this.countLikes = countLikes;
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
