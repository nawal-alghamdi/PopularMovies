package com.example.popularmovies.viewModels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.popularmovies.data.AppDatabase;


public class FavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase db;
    private final int id;

    //Initialize the member variables in the constructor with the parameters received
    public FavoriteViewModelFactory(AppDatabase database, int movieId) {
        db = database;
        id = movieId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FavoriteViewModel(db, id);
    }
}
