package com.example.popularmovies.UI;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.R;
import com.example.popularmovies.adapters.MovieAdapter;
import com.example.popularmovies.data.Movie;
import com.example.popularmovies.settings.SettingsActivity;
import com.example.popularmovies.utilities.NetworkJsonUtilities;
import com.example.popularmovies.viewModels.MainViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;
    // Boolean flag for preference updates and initialize it to false
    private static boolean PREFERENCE_IS_POPULAR_OR_TOP_RATED = false;
    MainViewModel viewModel;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private TextView errorMessageTextView;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        recyclerView = findViewById(R.id.movie_recyclerView);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        errorMessageTextView = findViewById(R.id.error_message_textView);

        final int GRID_LAYOUT_NUMBER_OF_COLUMNS = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_LAYOUT_NUMBER_OF_COLUMNS);

        recyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        recyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        movieAdapter = new MovieAdapter(MainActivity.this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        recyclerView.setAdapter(movieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        loadingIndicator = findViewById(R.id.loading_indicator);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        Log.d(TAG, "onCreate: registering preference changed listener");

        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a

         * SharedPreference has changed.
         */
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String order = sharedPreferences.getString(getString(R.string.pref_order_key),
                getString(R.string.popular_value));

        if (order.equals(getString(R.string.favorite_value))) {
            setupViewModel();
        } else {

            PREFERENCE_IS_POPULAR_OR_TOP_RATED = true;
            /*
             * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
             * on our Loader at a later point in time through the support LoaderManager.
             */
            int loaderId = MOVIE_LOADER_ID;
            /*
             * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
             * created and (if the activity/fragment is currently started) starts the loader. Otherwise
             * the last created loader is re-used.
             */
            LoaderManager.getInstance(this).initLoader(loaderId, null, MainActivity.this);
        }

    }

    private void setupViewModel() {
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(TAG, "Updating list of movies from LiveData in ViewModel");
                movieAdapter.setMoviesData(movies);
            }
        });
    }


    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            /* This List will hold and help cache our movie data */
            List<Movie> moviesData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (moviesData != null) {
                    deliverResult(moviesData);
                } else {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from NetworkJsonUtilities in the background.
             *
             * @return Movie data from NetworkJsonUtilities as a List of Movies.
             *         null if an error occurs
             */
            @Override
            public List<Movie> loadInBackground() {

                String requestUrl = buildUrl();

                try {
                    List<Movie> jsonMovieResponse = NetworkJsonUtilities.fetchMovieData(requestUrl);
                    return jsonMovieResponse;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            @Override
            public void deliverResult(List<Movie> data) {
                moviesData = data;
                super.deliverResult(data);
            }
        };
    }

    // Called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        movieAdapter.setMoviesData(movies);
        if (null == movies) {
            showErrorMessage();
        } else {
            showMovieDataView();
        }


    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        movieAdapter.clear();
    }

    private String buildUrl() {

        // Here we build the url and we change it depending on the user preference.
        // For example this is the url if you want to request movies and sort it by top rated
        // https://api.themoviedb.org/3/movie/top_rated?api_key=91ce7f376d9174b6b3c16f10a8b5c413

        final String BASE_URL = "https://api.themoviedb.org/3/movie";
        final String API_KEY_PARAMETER = "api_key";

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(getString(R.string.pref_order_key),
                getString(R.string.popular_value));

        if (orderBy.equals(getString(R.string.popular_value))) {
            builder.appendPath("popular");
        } else {
            builder.appendPath("top_rated");
        }

        builder.appendQueryParameter(API_KEY_PARAMETER, getString(R.string.api_key));

        return builder.toString();
    }

    @Override
    public void onClick(Movie movie) {
        // If a poster is selected open DetailActivity
        //Toast.makeText(this, "poster" + movie.getPoster() + "Movie title" + movie.getTitle(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intent);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        errorMessageTextView.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        recyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    // In onStart, if preferences have been changed, refresh the data
    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCE_IS_POPULAR_OR_TOP_RATED) {
            Log.d(TAG, "onStart: preferences were updated");
            LoaderManager.getInstance(this).restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        } else {
            setupViewModel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String orderBy = sharedPreferences.getString(getString(R.string.pref_order_key),
                getString(R.string.popular_value));
        if (orderBy.equals(getString(R.string.favorite_value))) {
            setupViewModel();
            PREFERENCE_IS_POPULAR_OR_TOP_RATED = false;
        } else {
            //Set this flag to true so that when control returns to MainActivity, it can refresh the data
            PREFERENCE_IS_POPULAR_OR_TOP_RATED = true;
        }

    }
}
