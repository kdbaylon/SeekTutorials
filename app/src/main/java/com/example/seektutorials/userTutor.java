package com.example.seektutorials;

public class userTutor {
    private String uid;
    private String fname;
    private String lname;
    private String email;
    private String desc;
    private double rating;
    private String location;
    private String course;
    private String school;

    public userTutor(String uid, String fname, String lname, String email, String desc, double rating, String location, String course, String school) {
        this.uid = uid;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.desc = desc;
        this.rating = rating;
        this.location = location;
        this.course = course;
        this.school = school;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
