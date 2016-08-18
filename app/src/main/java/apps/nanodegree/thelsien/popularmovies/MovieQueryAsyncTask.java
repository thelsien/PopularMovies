package apps.nanodegree.thelsien.popularmovies;

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

public class MovieQueryAsyncTask extends AsyncTask<String, Void, JSONArray> {

    private static final String API_KEY = "YOURAPIKEYHERE";
    private static final String LOG_TAG = "MovieQueryAsyncTask";

    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    private MovieQueryAsyncTaskListener mListener;

    public MovieQueryAsyncTask(MovieQueryAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {
        String filterType = strings[0];
        HttpURLConnection conn;
        Uri uri;

        uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(filterType)
                .appendQueryParameter("api_key", API_KEY)
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

            JSONObject resultObject = new JSONObject(resultString);
            JSONArray moviesArray = new JSONArray(resultObject.optString("results"));

            return moviesArray;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        super.onPostExecute(result);

        mListener.onResult(result);
    }

    interface MovieQueryAsyncTaskListener {
        void onResult(JSONArray result);
    }
}
