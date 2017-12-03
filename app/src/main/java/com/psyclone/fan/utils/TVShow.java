package com.psyclone.fan.utils;

public class TVShow {
    private String poster, name, channel, time, details;

    public String getPoster() {
        return poster;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getChannel() {
        return channel;
    }

    public String getDetails() {
        return details;
    }

    public void setAll(String poster, String name, String time, String details) {
        this.poster = poster;
        this.name = name;
        this.time = time;
        this.details = details;
    }

    public TVShow(String poster, String name, String time, String details) {
        this.poster = poster;
        this.name = name;
        this.time = time;
        this.details = details;
    }

    public TVShow(String poster, String name, String channel, String time, String details) {
        this.poster = poster;
        this.name = name;
        this.channel = channel;
        this.time = time;
        this.details = details;
    }
}
