package apps.nanodegree.thelsien.popularmovies.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import apps.nanodegree.thelsien.popularmovies.MoviesAdapter;
import apps.nanodegree.thelsien.popularmovies.R;
import apps.nanodegree.thelsien.popularmovies.background.MovieQueryAsyncTask;
import apps.nanodegree.thelsien.popularmovies.model.Movie;

public class MovieDetailFragment extends Fragment
        implements MovieQueryAsyncTask.MovieQueryAsyncTaskListener {

    private Movie mMovie;
    private static final String LOG_TAG = "MovieDetailFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        mMovie = intent.getParcelableExtra(getString(R.string.intent_extra_movie));

        MovieQueryAsyncTask movieQueryAsyncTask = new MovieQueryAsyncTask(this);
        movieQueryAsyncTask.execute(mMovie.id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.iv_movie_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.tv_movie_title);
        TextView plotView = (TextView) rootView.findViewById(R.id.tv_movie_plot_synopsis);
        TextView voteView = (TextView) rootView.findViewById(R.id.tv_vote_avg);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.tv_release_date);

        Picasso.with(getActivity())
                .load(MoviesAdapter.POSTER_IMAGE_BASE_URL + mMovie.posterImageUrlPart)
                .placeholder(R.drawable.default_movie_poster)
                .into(posterView);
        titleView.setText(mMovie.originalTitle);
        plotView.setText(mMovie.plotSynopsis);
        voteView.setText(String.format(getString(R.string.vote_average_placeholder), String.valueOf(mMovie.voteAverage)));
        releaseDateView.setText(mMovie.releaseDate);

        return rootView;
    }

    @Override
    public void onMovieQueryResult(JSONObject result) {
        Log.d(LOG_TAG, result.toString());

        mMovie.runTime = result.optInt("runtime");
        if (getView() != null) {
            TextView runtimeView = (TextView) getView().findViewById(R.id.tv_runtime);
            runtimeView.setText(String.format(getString(R.string.runtime_placeholder), mMovie.runTime));
        }
    }
}
