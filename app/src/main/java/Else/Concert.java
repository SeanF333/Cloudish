package Else;

import java.sql.Time;
import java.util.Date;

public class Concert {

    private String id, imageurl, main_genre, name, description, time, userId;
    private Integer duration;
    private Date date;


    public Concert(String id, String imageurl, String main_genre, String name, String description, Date date, String time, String userId, Integer duration) {
        this.id = id;
        this.imageurl = imageurl;
        this.main_genre = main_genre;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getMain_genre() {
        return main_genre;
    }

    public void setMain_genre(String main_genre) {
        this.main_genre = main_genre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
