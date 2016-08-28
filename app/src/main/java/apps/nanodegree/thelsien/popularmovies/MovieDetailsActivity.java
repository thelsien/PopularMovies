package apps.nanodegree.thelsien.popularmovies;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import apps.nanodegree.thelsien.popularmovies.fragments.MovieDetailFragment;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            Parcelable parcelable = getIntent().hasExtra(getString(R.string.intent_extra_movie)) ?
                    getIntent().getParcelableExtra(getString(R.string.intent_extra_movie)) :
                    getIntent().getParcelableExtra(getString(R.string.intent_extra_movie_uri));

            getSupportFragmentManager().beginTransaction()
                    .add(
                            R.id.movie_detail_container,
                            MovieDetailFragment.newInstance(
                                    this,
                                    parcelable
                            )
                    )
                    .commit();
        }
    }
}
