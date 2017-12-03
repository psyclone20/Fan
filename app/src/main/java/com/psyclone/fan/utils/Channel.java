package com.psyclone.fan.utils;

public class Channel {
    private String name, logo;
    private int code;

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getLogo() {
        return logo;
    }

    public Channel(String name, int code, String logo) {
        this.name = name;
        this.code = code;
        this.logo = logo;
    }
}
