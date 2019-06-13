package com.example.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Query("DELETE FROM movie WHERE movieId = :id")
    void deleteMovie(int id);

    @Query("SELECT movieId FROM movie WHERE movieId = :id")
    LiveData<Integer> loadMovieId(int id);

}
