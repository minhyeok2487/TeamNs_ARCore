package com.example.teamns_arcore.Record.Model;


import java.sql.Date;

public class RecordModel {
    String id;
    Date date;
    int correctNum, Level;
    float timer;
    float score;

    public RecordModel(String id, Date date, int correctNum, float timer, float score, int Level) {
        this.id = id;
        this.date = date;
        this.correctNum = correctNum;
        this.timer = timer;
        this.score = score;
        this.Level = Level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        String Stringdate = String.valueOf(date);
        return Stringdate;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCorrectNum() {
        String StringCorrectNum = String.valueOf(correctNum);
        return StringCorrectNum;
    }

    public void setCorrectNum(int correctNum) {
        this.correctNum = correctNum;
    }

    public String getTimer() {
        String StringTimer = String.valueOf(timer);
        return StringTimer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    public String getScore() {
        String StringScore = String.valueOf(score);
        return StringScore;
    }

    public void setScore() {
        this.score = (correctNum/timer)*100;
    }
}
