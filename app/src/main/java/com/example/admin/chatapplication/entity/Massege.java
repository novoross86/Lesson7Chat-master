package com.example.admin.chatapplication.entity;

public class Massege {

    private String t_profile_fullname;
    private String txt;
    private String t_profile_photo_50;
    private String t_photo_photo_130;


    public String getT_photo_photo_130() {
        return t_photo_photo_130;
    }

    public void setT_photo_photo_130(String t_photo_photo_130) {
        this.t_photo_photo_130 = t_photo_photo_130;
    }

    public Massege(String t_profile_fullname, String txt, String t_profile_photo_50, String t_photo_photo_130){
        this.t_profile_fullname = t_profile_fullname;
        this.txt = txt;
        this.t_profile_photo_50 = t_profile_photo_50;
        this.t_photo_photo_130 = t_photo_photo_130;


    }

    public Massege(){}

    public String getT_profile_fullname() {
        return t_profile_fullname;
    }

    public void setT_profile_fullname(String t_profile_fullname) {
        this.t_profile_fullname = t_profile_fullname;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getT_profile_photo_50() {
        return t_profile_photo_50;
    }

    public void setT_profile_photo_50(String t_profile_photo_50) {
        this.t_profile_photo_50 = t_profile_photo_50;
    }
}
