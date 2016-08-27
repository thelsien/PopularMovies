package apps.nanodegree.thelsien.popularmovies.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final int MOVIE = 100;
    private static final int FAVORITE = 111;
    private static final int VIDEO = 125;
    private static final int REVIEW = 150;

    private static final int MOVIE_WITH_ID = 200;
    private static final int FAVORITE_WITH_ID = 250;

    private static final int VIDEOS_FOR_MOVIE = 300;
    private static final int REVIEWS_FOR_MOVIE = 400;

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private MovieDbHelper mMovieDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE,
                MOVIE
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITES,
                FAVORITE
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_VIDEOS,
                VIDEO
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_REVIEWS,
                REVIEW
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/#",
                MOVIE_WITH_ID
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_FAVORITES + "/#",
                FAVORITE_WITH_ID
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_VIDEOS + "/#",
                VIDEOS_FOR_MOVIE
        );

        matcher.addURI(
                MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_REVIEWS + "/#",
                REVIEWS_FOR_MOVIE
        );

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (mUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITE: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEO: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID: {
                long id = ContentUris.parseId(uri);
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null
                );
                break;
            }
            case FAVORITE_WITH_ID: {
                long id = ContentUris.parseId(uri);
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null
                );
                break;
            }
            case VIDEOS_FOR_MOVIE: {
                long id = ContentUris.parseId(uri);
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        MovieContract.VideoEntry.TABLE_NAME + "." + MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null
                );
                break;
            }
            case REVIEWS_FOR_MOVIE: {
                long id = ContentUris.parseId(uri);
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;

            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            case FAVORITE_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;

            case VIDEO:
            case VIDEOS_FOR_MOVIE:
                return MovieContract.VideoEntry.CONTENT_TYPE;

            case REVIEW:
            case REVIEWS_FOR_MOVIE:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, _id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case FAVORITE: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.FavoriteEntry.CONTENT_URI, _id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case VIDEO: {
                long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.VideoEntry.CONTENT_URI, _id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.ReviewEntry.CONTENT_URI, _id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknows uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEO:
                rowsDeleted = db.delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEO:
                rowsUpdated = db.update(MovieContract.VideoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
