package apps.nanodegree.thelsien.popularmovies.background;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import apps.nanodegree.thelsien.popularmovies.Globals;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieQueryAsyncTask extends AsyncTask<Integer, Void, JSONObject> {
    private MovieQueryAsyncTaskListener mListener;

    public MovieQueryAsyncTask(MovieQueryAsyncTaskListener listener) {
        this.mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(Integer... integers) {
        int movieId = integers[0];
        Uri uriMovie = Uri.parse(Globals.MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter("api_key", Globals.MOVIE_DB_API_KEY)
                .build();

        Uri uriMovieVideos = Uri.parse(Globals.MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendPath("videos")
                .appendQueryParameter("api_key", Globals.MOVIE_DB_API_KEY)
                .build();

        Uri uriMovieReviews = Uri.parse(Globals.MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendPath("reviews")
                .appendQueryParameter("api_key", Globals.MOVIE_DB_API_KEY)
                .build();

        try {
            URL url = new URL(uriMovie.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            String resultStringForMovie = response.body().string();

            url = new URL(uriMovieVideos.toString());
            request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();

            String resultStringForMovieVideos = response.body().string();

            url = new URL(uriMovieReviews.toString());
            request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();

            String resultStringForMovieReviews = response.body().string();


            return getMovieDataJSON(resultStringForMovie, resultStringForMovieVideos, resultStringForMovieReviews);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject getMovieDataJSON(String resultStringForMovie, String resultStringForMovieVideos, String resultStringForMovieReviews) {
        try {
            JSONObject movieData = new JSONObject(resultStringForMovie);
            JSONObject movieVideos = new JSONObject(resultStringForMovieVideos);
            JSONObject movieReviews = new JSONObject(resultStringForMovieReviews);

            movieData.put("videos", movieVideos.getJSONArray("results"));
            movieData.put("reviews", movieReviews.getJSONArray("results"));

            return movieData;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        mListener.onMovieQueryResult(result);
        super.onPostExecute(result);
    }

    public interface MovieQueryAsyncTaskListener {
        void onMovieQueryResult(JSONObject result);
    }
}
