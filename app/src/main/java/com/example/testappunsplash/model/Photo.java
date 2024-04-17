package com.example.testappunsplash.model;

import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("id")
    private String id;

    @SerializedName("urls")
    private Urls urls;

    public String getId() {
        return id;
    }

    public Urls getUrls() {
        return urls;
    }

    public class Urls {
        @SerializedName("regular")
        private String regularUrl;

        public String getRegularUrl() {
            return regularUrl;
        }
    }
}
