package Else;

public class Album {
    String albumid;
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

    public Album(){

    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
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
