package com.brandonlee.instagram.Database;

/**
 * Created by BrandonLee on 3/5/18.
 */

public class Photo {

    private String image_path;
    private String photo_id;
    private String user_id;
    private String date_created;

    public Photo() {

    }

    public Photo(String image_path, String photo_id, String user_id, String date_created) {
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}



