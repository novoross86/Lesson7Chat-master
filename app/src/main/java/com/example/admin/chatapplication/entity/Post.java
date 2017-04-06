package com.example.admin.chatapplication.entity;


public class Post {

    private String comments, groups_name, groups_photo_50, likes, tag, txt, date;



    public Post(String comments, String groups_name, String groups_photo_50, String likes,
                String txt, String date) {
        this.comments = comments;
        this.groups_name = groups_name;
        this.groups_photo_50 = groups_photo_50;
        this.likes = likes;
        this.txt = txt;
        this.date = date;

    }

    public Post() {
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getGroups_name() {
        return groups_name;
    }

    public void setGroups_name(String groups_name) {
        this.groups_name = groups_name;
    }

    public String getGroups_photo_50() {
        return groups_photo_50;
    }

    public void setGroups_photo_50(String groups_photo_50) {
        this.groups_photo_50 = groups_photo_50;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
