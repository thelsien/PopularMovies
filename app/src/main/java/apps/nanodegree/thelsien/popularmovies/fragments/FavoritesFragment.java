package apps.nanodegree.thelsien.popularmovies.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import apps.nanodegree.thelsien.popularmovies.MovieDetailsActivity;
import apps.nanodegree.thelsien.popularmovies.R;
import apps.nanodegree.thelsien.popularmovies.adapters.FavoriteMoviesCursorAdapter;
import apps.nanodegree.thelsien.popularmovies.model.MovieContract;

public class FavoritesFragment extends Fragment {

    private static final String LOG_TAG = FavoritesFragment.class.getName();
    private FavoriteMoviesCursorAdapter mAdapter;

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume called");

        if (mAdapter != null) {
            onFavoritesChanged();
        }
    }

    private void onFavoritesChanged() {
        Cursor c = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        mAdapter.swapCursor(c);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        Cursor c = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        mAdapter = new FavoriteMoviesCursorAdapter(getActivity(), c, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.gv_movies);

        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        } else if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            gridView.setNumColumns(4);
        }
        gridView.setEmptyView(rootView.findViewById(R.id.empty_list));
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (getActivity() instanceof FavoriteMovieClickedListener) {
                    ((FavoriteMovieClickedListener) getActivity())
                            .onFavoriteMovieClicked(mAdapter.getItem(position));
                } else {
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra(
                            getString(R.string.intent_extra_movie_uri),
                            mAdapter.getItem(position)
                    );

                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    public interface FavoriteMovieClickedListener {
        void onFavoriteMovieClicked(Uri uri);
    }
}
