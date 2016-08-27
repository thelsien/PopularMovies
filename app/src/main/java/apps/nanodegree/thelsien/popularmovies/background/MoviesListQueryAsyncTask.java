package apps.nanodegree.thelsien.popularmovies.background;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import apps.nanodegree.thelsien.popularmovies.Globals;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviesListQueryAsyncTask extends AsyncTask<String, Void, JSONArray> {

    private static final String LOG_TAG = "MoviesListQueryATask";

    private MoviesListQueryAsyncTaskListener mListener;

    public MoviesListQueryAsyncTask(MoviesListQueryAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {
        String filterType = strings[0];
        Uri uri = Uri.parse(Globals.MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath("movie")
                .appendPath(filterType)
                .appendQueryParameter("api_key", Globals.MOVIE_DB_API_KEY)
                .build();

        URL url;
        try {
            url = new URL(uri.toString());

            Log.d(LOG_TAG, url.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            String resultString = response.body().string();

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
