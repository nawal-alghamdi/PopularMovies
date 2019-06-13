package com.example.popularmovies.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.popularmovies.data.Trailer;
import com.example.popularmovies.utilities.NetworkJsonUtilities;

import java.util.List;

//To define the TrailerLoader class, we extend AsyncTaskLoader and specify List as the generic parameter,
// which explains what type of data is expected to be loaded.
// In this case, the loader is loading a list of Trailer objects.

/**
 * To define the TrailerLoader class, we extend AsyncTaskLoader and specify List as the generic parameter,
 * which explains what type of data is expected to be loaded.
 * In this case, the loader is loading a list of Trailer objects.
 * Loads a list of trailers by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = TrailerLoader.class.getName();
    /* This List will hold and help cache our trailer data */
    List<Trailer> trailersData = null;
    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link TrailerLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public TrailerLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    // We override the onStartLoading() method to call forceLoad() which is a required
    // step to actually trigger the loadInBackground() method to execute.
    @Override
    protected void onStartLoading() {
        if (trailersData != null) {
            deliverResult(trailersData);
        } else {
            forceLoad();
        }
    }

    /**
     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
     * from NetworkJsonUtilities in the background.
     *
     * @return Trailer data from NetworkJsonUtilities as a List of Trailers.
     * null if an error occurs
     */
    @Override
    public List<Trailer> loadInBackground() {
        try {
            List<Trailer> jsonTrailerResponse = NetworkJsonUtilities.fetchTrailerData(url);
            return jsonTrailerResponse;

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
    public void deliverResult(List<Trailer> data) {
        trailersData = data;
        super.deliverResult(data);
    }
}




