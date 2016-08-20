package apps.nanodegree.thelsien.popularmovies.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import apps.nanodegree.thelsien.popularmovies.Globals;
import apps.nanodegree.thelsien.popularmovies.MovieDetailsActivity;
import apps.nanodegree.thelsien.popularmovies.MoviesAdapter;
import apps.nanodegree.thelsien.popularmovies.R;
import apps.nanodegree.thelsien.popularmovies.SettingsActivity;
import apps.nanodegree.thelsien.popularmovies.background.MoviesListQueryAsyncTask;
import apps.nanodegree.thelsien.popularmovies.model.Movie;

public class MainFragment extends Fragment
        implements MoviesListQueryAsyncTask.MoviesListQueryAsyncTaskListener {

    private static final String LOG_TAG = "MainFragment";
    private MoviesAdapter mAdapter;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container);

        Button refreshButton = (Button) rootView.findViewById(R.id.btn_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMoviesList();
            }
        });

        mAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        GridView gridView = (GridView) rootView.findViewById(R.id.gv_movies);
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        } else if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            gridView.setNumColumns(4);
        }
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(getString(R.string.intent_extra_movie), mAdapter.getItem(position));

                startActivity(intent);
            }
        });

        NetworkInfo netInfo = Globals.getNetworkInfo(getActivity());
        if (netInfo == null || !netInfo.isConnected()) {
            gridView.setVisibility(View.GONE);
            rootView.findViewById(R.id.container_no_internet).setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesList() {
        NetworkInfo netInfo = Globals.getNetworkInfo(getActivity());

        if (netInfo != null && netInfo.isConnected()) {
            if (getView() != null) {
                getView().findViewById(R.id.gv_movies).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.container_no_internet).setVisibility(View.GONE);
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            MoviesListQueryAsyncTask moviesListQueryAsyncTask = new MoviesListQueryAsyncTask(this);
            moviesListQueryAsyncTask.execute(prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default)));
        } else {
            if (getView() != null) {
                getView().findViewById(R.id.gv_movies).setVisibility(View.GONE);
                getView().findViewById(R.id.container_no_internet).setVisibility(View.VISIBLE);
            }

            Toast.makeText(getActivity(), getString(R.string.toast_error_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResult(JSONArray result) {
        mAdapter.clear();
        for (int i = 0; i < result.length(); i++) {
            JSONObject movieJSON = result.optJSONObject(i);
            Movie movie = new Movie(
                    movieJSON.optInt("id"),
                    movieJSON.optString("original_title"),
                    movieJSON.optString("poster_path"),
                    movieJSON.optString("overview"),
                    movieJSON.optDouble("vote_average"),
                    movieJSON.optString("release_date")
            );
            mAdapter.add(movie);
        }
    }
}
