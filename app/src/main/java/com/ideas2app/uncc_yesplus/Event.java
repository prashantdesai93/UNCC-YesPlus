package com.ideas2app.uncc_yesplus;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 5/19/2018.
 */

public class Event implements Serializable{
    public String eTitle, eDescription, eDate, eStartTime, eEndTime, eUniversity, ePlace, eId;

    public Event() {
    }

    public Event(String eTitle, String eDescription, String eDate, String eStartTime, String eEndTime, String eUniversity, String ePlace,  String eId) {
        this.eTitle = eTitle;
        this.eDescription = eDescription;
        this.eDate = eDate;
        this.eStartTime = eStartTime;
        this.eEndTime = eEndTime;
        this.eId = eId;
        this.eUniversity = eUniversity;
        this.ePlace = ePlace;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("eTitle", eTitle);
        result.put("eDescription", eDescription);
        result.put("eDate", eDate);
        result.put("eStartTime", eStartTime);
        result.put("eEndTime", eEndTime);
        result.put("eUniversity", eUniversity);
        result.put("ePlace", ePlace);
        result.put("eId", eId);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eTitle='" + eTitle + '\'' +
                ", eDescription='" + eDescription + '\'' +
                ", eDate='" + eDate + '\'' +
                ", eStartTime='" + eStartTime + '\'' +
                ", eEndTime='" + eEndTime + '\'' +
                ", eUniversity='" + eUniversity + '\'' +
                ", ePlace='" + ePlace + '\'' +
                ", eId='" + eId + '\'' +
                '}';
    }
}
