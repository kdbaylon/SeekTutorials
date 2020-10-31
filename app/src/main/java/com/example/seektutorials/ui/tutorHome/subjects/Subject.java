package com.example.seektutorials.ui.tutorHome.subjects;

public class Subject {
    public String name, description, weekly_sched, time, fee, subjUUID;
    public Subject(){}

    public Subject(String name, String description, String weekly_sched, String time, String fee, String subjUUID) {
        this.name = name;
        this.description = description;
        this.weekly_sched = weekly_sched;
        this.time = time;
        this.fee = fee;
        this.subjUUID = subjUUID;
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

    public String getSubjUUID() { return subjUUID; }

    public void setSubjectUUID(String subjUUID) { this.subjUUID = subjUUID; }
}
