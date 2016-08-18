package apps.nanodegree.thelsien.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends Fragment implements MovieQueryAsyncTask.MovieQueryAsyncTaskListener {

    private static final String LOG_TAG = "MainFragment";
    private ArrayAdapter<Movie> mAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container);

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
            case R.id.action_refresh:
                updateMoviesList();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesList() {
        MovieQueryAsyncTask movieQueryAsyncTask = new MovieQueryAsyncTask(this);
        movieQueryAsyncTask.execute("popular");
    }

    @Override
    public void onResult(JSONArray result) {
        mAdapter.clear();
        for (int i = 0; i < result.length(); i++) {
            JSONObject movieJSON = result.optJSONObject(i);
            Movie movie = new Movie(movieJSON.optString("original_title"), movieJSON.optString("poster_path"), movieJSON.optString("overview"), movieJSON.optDouble("vote_average"), movieJSON.optString("release_date"));
            mAdapter.add(movie);
        }
    }
}
