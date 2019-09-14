package com.example.popularmovies.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

// Let this class be a table
@Entity(tableName = "movie")
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    // Unique id for each item in the favorite DB
    @PrimaryKey(autoGenerate = true)
    private int id;
    // The movie poster path
    private String poster;
    // The movie title
    private String title;
    // The movie release date
    private String releaseDate;
    // The movie vote average
    private double voteAverage;
    // The movie overview
    private String overview;
    // The movie id that will be fetched from theMovieDb
    private int movieId;

    // Ignore this constructor
    @Ignore
    public Movie(String poster, String title, String releaseDate, double voteAverage, String overview, int movieId) {
        this.poster = buildMoviePosterUrl(poster);
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.movieId = movieId;
    }


    public Movie(int id, String poster, String title, String releaseDate, double voteAverage, String overview, int movieId) {
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.movieId = movieId;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        movieId = in.readInt();
    }

    public String getPoster() {
        return poster;
    }

    public String buildMoviePosterUrl(String poster) {
        String baseUrl = "https://image.tmdb.org/t/p/";
        String size = "w185/";
        return baseUrl + size + poster;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public int getMovieId() {
        return movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
        dest.writeInt(movieId);
    }
}
