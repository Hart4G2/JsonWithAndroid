package com.example.lr5;

import java.io.Serializable;
import java.time.LocalTime;

public class Record implements Serializable, Comparable<Record> {
    private String title;
    private String text;
    private LocalTime time;
    private String image;

    public Record(String title, String text, LocalTime time, String image) {
        this.title = title;
        this.text = text;
        this.time = time;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int compareTo(Record other) {
        return this.time.compareTo(other.time);
    }
}
