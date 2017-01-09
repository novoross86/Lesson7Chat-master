package com.example.admin.chatapplication.entity;


public class Post {

    private String channel, title, chat_id, username, image, postDate;

    public Post(String channel, String title, String chat_id, String username, String image,
                String postDate) {
        this.channel = channel;
        this.title = title;
        this.chat_id = chat_id;
        this.username = username;
        this.image = image;
        this.postDate = postDate;


    }

    public Post() {
    }

    public String getChatId() {
        return chat_id;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setChatId(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

}
