package com.example.popularmovies.data;

public class Trailer {

    private String video;

    public Trailer(String video) {
        this.video = buildVideoUrl(video);
    }

    public String getVideo() {
        return video;
    }

    private String buildVideoUrl(String videoKey) {
        String baseUrl = "https://www.youtube.com/watch?v=";
        return baseUrl + videoKey;
    }
}
