package com.example.seektutorials.ui.tuteeHome.bookings;


public class Session {
    public Session(){}
    public String subject, uid, bookingUUID, fee, sched, status;

    public Session(String subject, String uid, String bookingUUID, String fee, String sched, String status) {
        this.subject = subject;
        this.uid = uid;
        this.bookingUUID = bookingUUID;
        this.fee = fee;
        this.sched = sched;
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSched() {
        return sched;
    }

    public void setSched(String sched) {
        this.sched = sched;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBookingUUID() {
        return bookingUUID;
    }

    public void setBookingUUID(String bookingUUID) {
        this.bookingUUID = bookingUUID;
    }
}
