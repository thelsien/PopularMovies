package apps.nanodegree.thelsien.popularmovies.background;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import apps.nanodegree.thelsien.popularmovies.Globals;

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

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            String resultString = buffer.toString();

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
