package com.example.teamns_arcore.SelectLevel.Model;

public class StractEn {
    public String english;
    public String means;
    public String flagtime;

    public StractEn(String english, String means, String flagtime) {
        this.english = english;
        this.means = means;
        this.flagtime = flagtime;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getMeans() {
        return means;
    }

    public void setMeans(String means) {
        this.means = means;
    }

    public String getFlagtime() {
        return flagtime;
    }

    public void setFlagtime(String flagtime) {
        this.flagtime = flagtime;
    }
}
