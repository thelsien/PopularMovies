package apps.nanodegree.thelsien.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import apps.nanodegree.thelsien.popularmovies.fragments.MainFragment;
import apps.nanodegree.thelsien.popularmovies.fragments.MovieDetailFragment;

public class MainActivity extends AppCompatActivity {

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
}
