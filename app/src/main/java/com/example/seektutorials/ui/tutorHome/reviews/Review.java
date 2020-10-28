package com.example.seektutorials.ui.tutorHome.reviews;

public class Review {
    public String fname, lname, subject, comment;
            float rate;
    public Review(){}

    public Review(String fname, String lname, String subject, String comment, int rate) {
        this.fname = fname;
        this.lname = lname;
        this.subject = subject;
        this.comment = comment;
        this.rate = rate;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
