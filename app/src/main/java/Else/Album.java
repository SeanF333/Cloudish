package Else;

public class Album {
    String albumname;
    String imageurl;
    String category;

    public Album(String albumname, String category, String imageurl) {
        this.albumname = albumname;
        this.imageurl = imageurl;
        this.category = category;
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
