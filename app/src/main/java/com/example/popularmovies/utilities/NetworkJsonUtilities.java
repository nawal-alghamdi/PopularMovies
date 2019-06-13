package com.example.popularmovies.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.popularmovies.data.Movie;
import com.example.popularmovies.data.Review;
import com.example.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving Movie data from The Movie Database.
 */
public final class NetworkJsonUtilities {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = NetworkJsonUtilities.class.getSimpleName();

    /**
     * Keys for the json response for Movie
     */
    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String VOTE = "vote_average";
    private static final String TITLE = "title";
    private static final String POSTER = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";

    /**
     * Keys for the json response for Trailer
     */
    private static final String KEY = "key";

    /**
     * Keys for the json response for Review
     */
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";


    /**
     * Return a list of {@link Movie} objects.
     */
    public static List<Movie> fetchMovieData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movie}s
        List<Movie> movies = extractMovieFeatureFromJson(jsonResponse);

        // Return the list of {@link Movie}s
        return movies;
    }

    /**
     * Return a list of {@link Trailer} objects.
     */
    public static List<Trailer> fetchTrailerData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Trailer}s
        List<Trailer> trailers = extractTrailerFeatureFromJson(jsonResponse);

        // Return the list of {@link Trailer}s
        return trailers;
    }

    /**
     * Return a list of {@link Review} objects.
     */
    public static List<Review> fetchReviewData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Review}s
        List<Review> reviews = extractReviewFeatureFromJson(jsonResponse);

        // Return the list of {@link Review}s
        return reviews;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Movie> extractMovieFeatureFromJson(String movieJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding movies to
        List<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of movies.
            JSONArray movieArray = baseJsonResponse.getJSONArray(RESULTS);

            // For each movie in the movieArray, create an {@link Movie} object
            for (int i = 0; i < movieArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = movieArray.getJSONObject(i);

                // Extract the value for the key called "id"
                int movieId = currentMovie.getInt(ID);

                // Extract the value for the key called "vote_average"
                double voteAverage = currentMovie.getDouble(VOTE);

                // Extract the value for the key called "title"
                String title = currentMovie.getString(TITLE);

                // Extract the value for the key called "poster_path"
                String poster = currentMovie.getString(POSTER);

                // Extract the value for the key called "overview"
                String overview = currentMovie.getString(OVERVIEW);

                // Extract the value for the key called "release_date"
                String releaseDate = currentMovie.getString(RELEASE_DATE);

                // Create a new {@link Movie} object with the poster, title, releaseDate, voteAverage,
                // and overview from the JSON response.
                Movie movie = new Movie(poster, title, releaseDate, voteAverage, overview, movieId);

                // Add the new {@link Movie} to the list of movies.
                movies.add(movie);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworkJsonUtilities", "Problem parsing the movie JSON results", e);
        }

        // Return the list of movies
        return movies;
    }

    /**
     * Return a list of {@link Trailer} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Trailer> extractTrailerFeatureFromJson(String trailerJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(trailerJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding trailers to
        List<Trailer> trailers = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(trailerJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of trailers.
            JSONArray trailerArray = baseJsonResponse.getJSONArray(RESULTS);

            // For each trailer in the trailerArray, create an {@link Trailer} object
            for (int i = 0; i < trailerArray.length(); i++) {

                // Get a single trailer at position i within the list of trailers
                JSONObject currentTrailer = trailerArray.getJSONObject(i);

                // Extract the value for the key called "key"
                String videoKey = currentTrailer.getString(KEY);

                // Create a new {@link Trailer} object with the videoKey from the JSON response.
                Trailer trailer = new Trailer(videoKey);

                // Add the new {@link Trailer} to the list of trailers.
                trailers.add(trailer);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworkJsonUtilities", "Problem parsing the trailer JSON results", e);
        }

        // Return the list of trailers
        return trailers;
    }

    /**
     * Return a list of {@link Review} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Review> extractReviewFeatureFromJson(String reviewJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(reviewJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding reviews to
        List<Review> reviews = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(reviewJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of reviews.
            JSONArray reviewArray = baseJsonResponse.getJSONArray(RESULTS);

            // For each review in the reviewArray, create an {@link Review} object
            for (int i = 0; i < reviewArray.length(); i++) {

                // Get a single review at position i within the list of reviews
                JSONObject currentReview = reviewArray.getJSONObject(i);

                // Extract the value for the key called "author"
                String author = currentReview.getString(AUTHOR);

                // Extract the value for the key called "content"
                String content = currentReview.getString(CONTENT);

                // Create a new {@link Review} object with the author and content from the JSON response.
                Review review = new Review(author, content);

                // Add the new {@link Review} to the list of reviews.
                reviews.add(review);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworkJsonUtilities", "Problem parsing the review JSON results", e);
        }

        // Return the list of reviews
        return reviews;
    }

}


