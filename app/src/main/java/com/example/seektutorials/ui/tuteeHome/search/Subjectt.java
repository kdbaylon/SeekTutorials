package com.example.seektutorials.ui.tuteeHome.search;

public class Subjectt {
    public String name, description, weekly_sched, time, fee, tutorFname, tutorLname, tutorUID;
    public Subjectt(){}

    public Subjectt(String name, String description, String weekly_sched, String time, String fee, String tutorFname, String tutorLname, String tutorUID) {
        this.name = name;
        this.description = description;
        this.weekly_sched = weekly_sched;
        this.time = time;
        this.fee = fee;
        this.tutorFname = tutorFname;
        this.tutorLname = tutorLname;
        this.tutorUID = tutorUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeekly_sched() {
        return weekly_sched;
    }

    public void setWeekly_sched(String weekly_sched) {
        this.weekly_sched = weekly_sched;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTutorFname() {
        return tutorFname;
    }

    public void setTutorFname(String tutorFname) {
        this.tutorFname = tutorFname;
    }

    public String getTutorLname() {
        return tutorLname;
    }

    public void setTutorLname(String tutorLname) {
        this.tutorLname = tutorLname;
    }

    public String getTutorUID() {
        return tutorUID;
    }

    public void setTutorUID(String tutorUID) {
        this.tutorUID = tutorUID;
    }
}
