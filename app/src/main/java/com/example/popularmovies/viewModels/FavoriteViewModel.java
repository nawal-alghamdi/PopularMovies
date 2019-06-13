package com.example.popularmovies.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.popularmovies.data.AppDatabase;


public class FavoriteViewModel extends ViewModel {

    private LiveData<Integer> movieId;

    // A constructor where you call loadMovieId of the movieDao to initialize the movieId variable
    // Note: The constructor should receive the database and the movieId
    public FavoriteViewModel(AppDatabase database, int id) {
        movieId = database.movieDao().loadMovieId(id);
    }

    // A getter for the movieId variable
    public LiveData<Integer> getMovieId() {

        return movieId;
    }
}
