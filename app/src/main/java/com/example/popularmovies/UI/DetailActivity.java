package com.example.popularmovies.UI;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.R;
import com.example.popularmovies.adapters.ReviewAdapter;
import com.example.popularmovies.adapters.TrailerAdapter;
import com.example.popularmovies.data.AppDatabase;
import com.example.popularmovies.data.Movie;
import com.example.popularmovies.data.Review;
import com.example.popularmovies.data.Trailer;
import com.example.popularmovies.loaders.ReviewLoader;
import com.example.popularmovies.loaders.TrailerLoader;
import com.example.popularmovies.viewModels.FavoriteViewModel;
import com.example.popularmovies.viewModels.FavoriteViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks {

    // Extra for the favorite value to be received after rotation
    public static final String FAVORITE_VALUE_KEY = "instanceFavoriteValue";
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int TRAILER_LOADER_ID = 1;
    private static final int REVIEW_LOADER_ID = 2;
    // Constant for default isFavorite variable
    private static final boolean DEFAULT_FAVORITE_VALUE = true;
    private static int movieId;
    private AppDatabase database;
    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private Movie movie;
    // Check if the floating action button is clicked, and if so
    // we will add or remove a movie from favorite database and change the favorite image
    private boolean isFavorite = DEFAULT_FAVORITE_VALUE;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView poster = findViewById(R.id.imageView);
        TextView title = findViewById(R.id.title_textView);
        TextView releaseDate = findViewById(R.id.releaseDate_textView);
        TextView overView = findViewById(R.id.overView_textView);
        TextView voteAverage = findViewById(R.id.voteAverage_textView);

        database = AppDatabase.getInstance(getApplicationContext());
        fab = findViewById(R.id.fab);

        // Set the RecyclerView to its corresponding view
        trailerRecyclerView = findViewById(R.id.trailer_recyclerView);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        trailerAdapter = new TrailerAdapter(this, this);
        trailerRecyclerView.setAdapter(trailerAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        trailerRecyclerView.addItemDecoration(decoration);

        // Set the RecyclerView to its corresponding view
        reviewRecyclerView = findViewById(R.id.review_recyclerView);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        reviewAdapter = new ReviewAdapter(this);
        reviewRecyclerView.setAdapter(reviewAdapter);

        reviewRecyclerView.addItemDecoration(decoration);

        if (savedInstanceState != null && savedInstanceState.containsKey(FAVORITE_VALUE_KEY)) {
            isFavorite = savedInstanceState.getBoolean(FAVORITE_VALUE_KEY);
        }

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
                movieId = movie.getMovieId();
                Picasso.get().load(movie.getPoster()).into(poster);
                title.setText(movie.getTitle());
                releaseDate.setText(movie.getReleaseDate());
                overView.setText(movie.getOverview());
                voteAverage.setText(String.valueOf(movie.getVoteAverage()));
            }
        }

        // Declare a FavoriteViewModelFactory using database and movieId
        FavoriteViewModelFactory factory = new FavoriteViewModelFactory(database, movieId);
        // Declare a FavoriteViewModel variable and initialize it by calling ViewModelProviders.of
        // for that use the factory created above FavoriteViewModel
        final FavoriteViewModel viewModel
                = ViewModelProviders.of(this, factory).get(FavoriteViewModel.class);

        // Observe the LiveData object in the ViewModel. Use it also when removing the observer
        viewModel.getMovieId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                Log.d(TAG, "movieId in detail = " + id);
                if (null != id) {
                    fab.setImageResource(R.drawable.add_favorite);
                    isFavorite = false;

                } else {
                    fab.setImageResource(R.drawable.remove_favorite);
                    isFavorite = true;

                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isFavorite) {
                            fab.setImageResource(R.drawable.add_favorite);
                            database.movieDao().insertMovie(movie);
                            Log.d(TAG, "movieId insert " + movieId);
                            isFavorite = false;
                        } else {
                            fab.setImageResource(R.drawable.remove_favorite);
                            database.movieDao().deleteMovie(movieId);
                            Log.d(TAG, "movieId delete " + movieId);
                            isFavorite = true;
                        }
                    }
                });

            }
        });

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager.getInstance(this).initLoader(TRAILER_LOADER_ID, null, this);
        LoaderManager.getInstance(this).initLoader(REVIEW_LOADER_ID, null, this);

    }


    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        String requestUrl;

        if (id == TRAILER_LOADER_ID) {
            requestUrl = buildUrl("videos");
            return new TrailerLoader(this, requestUrl);

        } else if (id == REVIEW_LOADER_ID) {
            requestUrl = buildUrl("reviews");
            return new ReviewLoader(this, requestUrl);
        }
        return null;
    }

    // Called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        // Id to find which loader is called
        int id = loader.getId();

        if (id == TRAILER_LOADER_ID) {
            trailerAdapter.setTrailersData((List<Trailer>) data);

        } else if (id == REVIEW_LOADER_ID) {
            reviewAdapter.setReviewsData((List<Review>) data);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

        int id = loader.getId();

        if (id == TRAILER_LOADER_ID) {
            // Loader reset, so we can clear out our existing data.
            trailerAdapter.clear();

        } else if (id == REVIEW_LOADER_ID) {
            // Loader reset, so we can clear out our existing data.
            reviewAdapter.clear();

        }

    }

    private String buildUrl(String path) {

        // Here we build the url and we change it depending if it's trailer or review.
        // For example this is the url if you want to request trailers
        // https://api.themoviedb.org/3/movie/movieId/videos?api_key=getString(R.string.api_key)
        // And this is the url if you want to request reviews
        // https://api.themoviedb.org/3/movie/movieId/reviews?api_key=getString(R.string.api_key)

        final String BASE_URL = "https://api.themoviedb.org/3/movie/" + movieId;
        final String API_KEY_PARAMETER = "api_key";

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();

        builder.appendPath(path);

        builder.appendQueryParameter(API_KEY_PARAMETER, getString(R.string.api_key));

        return builder.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FAVORITE_VALUE_KEY, isFavorite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(Trailer trailer) {

        Uri uri = Uri.parse(trailer.getVideo());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);

        } else {
            Log.d(TAG, "Couldn't call " + uri.toString()
                    + ", no receiving apps installed!");
        }

    }


}
