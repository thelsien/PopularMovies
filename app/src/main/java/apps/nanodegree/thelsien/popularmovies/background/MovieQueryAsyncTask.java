package apps.nanodegree.thelsien.popularmovies.background;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
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
        HttpURLConnection conn;
        Uri uri;

        uri = Uri.parse(Globals.MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter("api_key", Globals.MOVIE_DB_API_KEY)
                .build();

        URL url;
        try {
            url = new URL(uri.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            String resultString = response.body().string();

            return new JSONObject(resultString);

        } catch (IOException | JSONException e) {
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
