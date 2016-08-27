package apps.nanodegree.thelsien.popularmovies.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import apps.nanodegree.thelsien.popularmovies.Globals;
import apps.nanodegree.thelsien.popularmovies.MovieDetailsActivity;
import apps.nanodegree.thelsien.popularmovies.R;
import apps.nanodegree.thelsien.popularmovies.SettingsActivity;
import apps.nanodegree.thelsien.popularmovies.adapters.FavoriteMoviesCursorAdapter;
import apps.nanodegree.thelsien.popularmovies.background.MoviesListQueryAsyncTask;
import apps.nanodegree.thelsien.popularmovies.model.MovieContract;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainFragment extends Fragment
        implements MoviesListQueryAsyncTask.MoviesListQueryAsyncTaskListener {

    private static final String LOG_TAG = "MainFragment";
    private FavoriteMoviesCursorAdapter mAdapter;
    private boolean mIsFavoritesVisible = false;
    private GridView mGridView;
    private LinearLayout mNoInternetContainer;
    private String mSortByPreference;
    private int mPosition = GridView.INVALID_POSITION;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "saving mIsFavoritesVisible: " + mIsFavoritesVisible);

        outState.putBoolean(getString(R.string.is_favorites_visible_key), mIsFavoritesVisible);

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(getString(R.string.list_position_key), mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsFavoritesVisible = savedInstanceState.getBoolean(getString(R.string.is_favorites_visible_key));
            Log.d(LOG_TAG, "mIsFavoritesVisible loaded back: " + mIsFavoritesVisible);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container);

//        mAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        mAdapter = new FavoriteMoviesCursorAdapter(getActivity(), null, 0);
        mGridView = (GridView) rootView.findViewById(R.id.gv_movies);
        mNoInternetContainer = (LinearLayout) rootView.findViewById(R.id.container_no_internet);

        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mGridView.setNumColumns(2);
        } else if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            mGridView.setNumColumns(4);
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mPosition = position;
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(getString(R.string.intent_extra_movie_uri), MovieContract.MovieEntry.getMovieUriWithId(mAdapter.getItem(position)));

                startActivity(intent);
            }
        });

        Button refreshButton = (Button) rootView.findViewById(R.id.btn_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoviesFromInternet();
            }
        });

        updateMoviesList();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        if (mIsFavoritesVisible) {
            menu.findItem(R.id.action_favorites).setIcon(R.drawable.ic_favorite_white_48dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);

                return true;
            case R.id.action_favorites:
                if (!mIsFavoritesVisible) {
                    item.setIcon(R.drawable.ic_favorite_white_48dp);
                    mIsFavoritesVisible = true;

                    showFavoriteMovies();
                } else {
                    item.setIcon(R.drawable.ic_favorite_border_white_48dp);
                    mIsFavoritesVisible = false;

                    showMoviesFromInternet();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesList() {
        if (!mIsFavoritesVisible) {
            showMoviesFromInternet();
        } else {
            showFavoriteMovies();
        }
    }

    private void showFavoriteMovies() {
        mAdapter.swapCursor(null);
        Cursor c = getActivity().getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI, null, null, null, null);

        if (mNoInternetContainer.getVisibility() == VISIBLE) {
            mNoInternetContainer.setVisibility(GONE);
            mGridView.setVisibility(VISIBLE);
        }

        mAdapter.swapCursor(c);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(null);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mPosition = position;
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(getString(R.string.intent_extra_movie_uri), MovieContract.FavoriteEntry.getFavoriteUriWithId(mAdapter.getItem(position)));

                startActivity(intent);
            }
        });

        mGridView.smoothScrollToPosition(mPosition);
    }

    private void showMoviesFromInternet() {
        NetworkInfo netInfo = Globals.getNetworkInfo(getActivity());

        mAdapter.swapCursor(null);

        if (netInfo != null && netInfo.isConnected()) {
            Log.d(LOG_TAG, "networkInfo - we have connection");
            changeVisibilityOfGridViewAndNoInternetContainer(VISIBLE, GONE);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString(getString(R.string.pref_sort_by_preference_key), getString(R.string.pref_sort_by_preference_default));
            if (mSortByPreference == null || !mSortByPreference.equals(sortBy)) {
                mSortByPreference = sortBy;
                MoviesListQueryAsyncTask moviesListQueryAsyncTask = new MoviesListQueryAsyncTask(getActivity(), this);
                moviesListQueryAsyncTask.execute(mSortByPreference);
            } else {
                onMoviesListQueryFinished();
            }

        } else {
            Log.d(LOG_TAG, "networkInfo - no connection");
            Log.d(LOG_TAG, "networkInfo - empty adapter");
            changeVisibilityOfGridViewAndNoInternetContainer(GONE, VISIBLE);
        }
    }

    private void changeVisibilityOfGridViewAndNoInternetContainer(int gridViewVisibility, int noInternetContainerVisibility) {
        mGridView.setVisibility(gridViewVisibility);
        mNoInternetContainer.setVisibility(noInternetContainerVisibility);
    }

    @Override
    public void onMoviesListQueryFinished() {
//        mAdapter.clear();
//        for (int i = 0; i < result.length(); i++) {
//            JSONObject movieJSON = result.optJSONObject(i);
//            Movie movie = new Movie(
//                    movieJSON.optInt("id"),
//                    movieJSON.optString("original_title"),
//                    movieJSON.optString("poster_path"),
//                    movieJSON.optString("overview"),
//                    movieJSON.optDouble("vote_average"),
//                    movieJSON.optString("release_date")
//            );
//            mAdapter.add(movie);
//        }

        Cursor c = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );


        mAdapter.swapCursor(c);

        setAdapterForInternet();
    }

    private void setAdapterForInternet() {
//        mGridView.setAdapter(mAdapter);
        mGridView.smoothScrollToPosition(mPosition);
        mGridView.setOnItemClickListener(null);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(getString(R.string.intent_extra_movie_uri), MovieContract.MovieEntry.getMovieUriWithId(mAdapter.getItem(position)));

                startActivity(intent);
            }
        });
    }
}
