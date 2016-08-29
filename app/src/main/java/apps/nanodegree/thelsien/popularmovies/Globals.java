package apps.nanodegree.thelsien.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class Globals {
    public static final String MOVIE_DB_API_KEY = "YOURAPIKEY";
    public static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";

    public static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo();
    }

    public static String getSortMainListBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_default));
    }
}
