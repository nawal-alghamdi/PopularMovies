package com.example.popularmovies.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.popularmovies.data.Review;
import com.example.popularmovies.utilities.NetworkJsonUtilities;

import java.util.List;

/**
 * To define the ReviewLoader class, we extend AsyncTaskLoader and specify List as the generic parameter,
 * which explains what type of data is expected to be loaded.
 * In this case, the loader is loading a list of Review objects.
 * <p>
 * Loads a list of reviews by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = ReviewLoader.class.getName();
    /* This List will hold and help cache our review data */
    List<Review> reviewsData = null;
    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link ReviewLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public ReviewLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    // We override the onStartLoading() method to call forceLoad() which is a required
    // step to actually trigger the loadInBackground() method to execute.
    @Override
    protected void onStartLoading() {
        if (reviewsData != null) {
            deliverResult(reviewsData);
        } else {
            forceLoad();
        }
    }

    /**
     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
     * from NetworkJsonUtilities in the background.
     *
     * @return Review data from NetworkJsonUtilities as a List of Reviews.
     * null if an error occurs
     */
    @Override
    public List<Review> loadInBackground() {
        try {
            List<Review> jsonReviewResponse = NetworkJsonUtilities.fetchReviewData(url);
            return jsonReviewResponse;

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
    public void deliverResult(List<Review> data) {
        reviewsData = data;
        super.deliverResult(data);
    }
}

