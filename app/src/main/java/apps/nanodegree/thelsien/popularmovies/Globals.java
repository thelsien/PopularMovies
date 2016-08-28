package apps.nanodegree.thelsien.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Globals {
    public static final String MOVIE_DB_API_KEY = "YOURAPIKEY";
    public static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";

    public static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo();
    }
}
