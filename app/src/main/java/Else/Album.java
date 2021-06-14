package Else;

public class Album {
    String albumname;
    String imageurl;
    String category;
    public Boolean isSelected;
    int likecount;

    public Album(String albumname, String category, String imageurl) {
        this.albumname = albumname;
        this.imageurl = imageurl;
        this.category = category;
        this.isSelected = false;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public Album() {
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getAlbumcategory() {
        return category;
    }

    public void setAlbumcategory(String albumcategory) {
        this.category = albumcategory;
    }
}
