package apps.nanodegree.thelsien.popularmovies.background;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import apps.nanodegree.thelsien.popularmovies.Globals;
import apps.nanodegree.thelsien.popularmovies.adapters.MoviesAdapter;
import apps.nanodegree.thelsien.popularmovies.model.MovieContract;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviesListQueryAsyncTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = "MoviesListQueryATask";

    private MoviesListQueryAsyncTaskListener mListener;
    private Context mContext;

    public MoviesListQueryAsyncTask(Context context, MoviesListQueryAsyncTaskListener listener) {
        mListener = listener;
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
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

            JSONArray resultArray = new JSONArray(resultObject.optString("results"));

            Vector<ContentValues> cvVector = new Vector<>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject movieObject = resultArray.optJSONObject(i);
                ContentValues v = new ContentValues();
                v.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieObject.optInt("id"));
                v.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieObject.optString("original_title"));
                v.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieObject.optString("release_date"));
                v.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieObject.optString("overview"));
                v.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, movieObject.optDouble("vote_average"));
                v.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, MoviesAdapter.POSTER_IMAGE_BASE_URL + "/" + movieObject.optString("poster_path"));

                cvVector.add(v);
            }

            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);

                mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        mListener.onMoviesListQueryFinished();
    }

    public interface MoviesListQueryAsyncTaskListener {
        void onMoviesListQueryFinished();
    }
}
