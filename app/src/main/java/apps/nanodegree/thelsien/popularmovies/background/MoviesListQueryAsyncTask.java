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

public class MoviesListQueryAsyncTask extends AsyncTask<String, Void, JSONArray> {

//    private static final String LOG_TAG = "MoviesListQueryAsyncTask";

    private MoviesListQueryAsyncTaskListener mListener;

    public MoviesListQueryAsyncTask(MoviesListQueryAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {
        String filterType = strings[0];
        HttpURLConnection conn;
        Uri uri;

        uri = Uri.parse(Globals.MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(filterType)
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

            JSONObject resultObject = new JSONObject(resultString);

            return new JSONArray(resultObject.optString("results"));

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

    public interface MoviesListQueryAsyncTaskListener {
        void onResult(JSONArray result);
    }
}
