package apps.nanodegree.thelsien.popularmovies.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DURATION + " INTEGER NOT NULL DEFAULT -1, " +
                "UNIQUE(" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                ");";

        final String CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_DURATION + " INTEGER NOT NULL DEFAULT -1, " +
                "UNIQUE(" + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                ");";

        final String CREATE_VIDEOS_TABLE = "CREATE TABLE " + MovieContract.VideoEntry.TABLE_NAME + " (" +
                MovieContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_VIDEO_URL + " TEXT NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_PREVIEW_IMAGE_URL + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + MovieContract.VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ")" +
                ");";

        final String CREATE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ")" +
                ");";

        sqLiteDatabase.execSQL(CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Empty until first upgrade
    }
}
