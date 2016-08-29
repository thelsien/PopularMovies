package apps.nanodegree.thelsien.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import apps.nanodegree.thelsien.popularmovies.fragments.FavoritesFragment;
import apps.nanodegree.thelsien.popularmovies.fragments.MainFragment;
import apps.nanodegree.thelsien.popularmovies.fragments.MovieDetailFragment;
import apps.nanodegree.thelsien.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity
        implements MainFragment.MovieClickListener, FavoritesFragment.FavoriteMovieClickedListener,
        MovieDetailFragment.OnFavoriteMovieModifiedListener {

    private static final String MAIN_FRAGMENT_TAG = "main_fragment_tag";
    private static final String DETAIL_FRAGMENT_TAG = "movie_detail_fragment_tag";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    private MainFragment mMainFragment;
    private FavoritesFragment mFavoritesFragment;
    private int mMainGridPosition = GridView.INVALID_POSITION;
    private String mSortMainListBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortMainListBy = Globals.getSortMainListBy(this);
        Log.d(LOG_TAG, "mSortMainListBy: " + mSortMainListBy);

        if (findViewById(R.id.movies_list_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                if (mMainFragment == null) {
                    mMainFragment = new MainFragment();
                }

                if (mFavoritesFragment == null) {
                    mFavoritesFragment = new FavoritesFragment();
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_list_container, mMainFragment, MAIN_FRAGMENT_TAG)
                        .commit();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortByInPrefs = Globals.getSortMainListBy(this);

        Log.d(LOG_TAG, "onResume - mSortMainListBy: " + mSortMainListBy);
        Log.d(LOG_TAG, "sortByInPrefs: " + sortByInPrefs);

        if (!mSortMainListBy.equals(sortByInPrefs)) {
            mSortMainListBy = sortByInPrefs;

            if (mMainFragment == null) {
                mMainFragment = new MainFragment();
            } else {
                mMainFragment.onSortingChanged();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMainFragment != null) {
            outState.putInt(getString(R.string.instancestate_grid_position), mMainFragment.getGridPosition());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMainGridPosition = savedInstanceState.getInt(getString(R.string.instancestate_grid_position), GridView.INVALID_POSITION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem favMenu = menu.findItem(R.id.action_favorites);
        if (mTwoPane) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
            if (f instanceof MainFragment) {
                favMenu.setIcon(R.drawable.ic_favorite_border_white_48dp);
            } else if (f instanceof FavoritesFragment) {
                favMenu.setIcon(R.drawable.ic_favorite_white_48dp);
            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;
            case R.id.action_favorites:
                if (mTwoPane) {
                    Fragment f = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
                    if (f instanceof MainFragment) {
                        if (mFavoritesFragment == null) {
                            mFavoritesFragment = new FavoritesFragment();
                        }

                        item.setIcon(R.drawable.ic_favorite_white_48dp);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movies_list_container, mFavoritesFragment, MAIN_FRAGMENT_TAG)
                                .commit();
                    } else if (f instanceof FavoritesFragment) {
                        if (mMainFragment == null) {
                            mMainFragment = new MainFragment();
                            mMainFragment.setGridPosition(mMainGridPosition);
                        }
                        item.setIcon(R.drawable.ic_favorite_border_white_48dp);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movies_list_container, mMainFragment, MAIN_FRAGMENT_TAG)
                                .commit();
                    }

                } else {
                    intent = new Intent(this, FavoritesActivity.class);
                    startActivity(intent);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemClicked(Movie movie) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.newInstance(this, movie), DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(getString(R.string.intent_extra_movie), movie);

            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteMovieClicked(Uri uri) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.newInstance(this, uri), DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(getString(R.string.intent_extra_movie_uri), uri);

            startActivity(intent);
        }
    }

    @Override
    public void onFavoriteModified() {
        if (mTwoPane) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
            if (f instanceof FavoritesFragment) {
                ((FavoritesFragment) f).onFavoritesChanged();
            }
        }
    }
}
