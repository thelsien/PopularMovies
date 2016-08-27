package apps.nanodegree.thelsien.popularmovies.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "apps.nanodegree.thelsien.popularmovies";
    public static final String PATH_MOVIE = "movie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri getMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }
}
