package com.lawrence254.autosign.model;

public class Attendance {
    private String date;
    private String module;
    private String student;
    private String time;

    public Attendance(){}

    public Attendance(String mdate, String mModule,String mStudent,String mtime){
        this.date = mdate;
        this.module = mModule;
        this.student = mStudent;
        this.time = mtime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
