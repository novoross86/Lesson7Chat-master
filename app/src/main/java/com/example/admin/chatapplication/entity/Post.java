package com.example.admin.chatapplication.entity;


public class Post {

    private String channel, title, chat_id, user_name, image, postDate, user_photo;

    public Post(String channel, String title, String chat_id, String user_name, String image,
                String postDate, String user_photo) {
        this.channel = channel;
        this.title = title;
        this.chat_id = chat_id;
        this.user_name = user_name;
        this.image = image;
        this.postDate = postDate;
        this.user_photo = user_photo;

    }

    public Post() {
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
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
        return user_name;
    }

    public void setUsername(String user_name) {
        this.user_name = user_name;
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
