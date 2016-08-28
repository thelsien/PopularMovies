package apps.nanodegree.thelsien.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import apps.nanodegree.thelsien.popularmovies.fragments.MainFragment;
import apps.nanodegree.thelsien.popularmovies.fragments.MovieDetailFragment;
import apps.nanodegree.thelsien.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity implements MainFragment.MovieClickListener {

    private static final String MAIN_FRAGMENT_TAG = "main_fragment_tag";
    private static final String DETAIL_FRAGMENT_TAG = "movie_detail_fragment_tag";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movies_list_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movies_list_container, new MainFragment(), MAIN_FRAGMENT_TAG)
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
                    //TODO change fragment in movielistcontainer
                    intent = new Intent(this, FavoritesActivity.class);
                    startActivity(intent);
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
}
