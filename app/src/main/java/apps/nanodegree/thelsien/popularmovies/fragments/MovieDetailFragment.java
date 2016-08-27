package apps.nanodegree.thelsien.popularmovies.fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import apps.nanodegree.thelsien.popularmovies.Globals;
import apps.nanodegree.thelsien.popularmovies.R;
import apps.nanodegree.thelsien.popularmovies.adapters.MoviesAdapter;
import apps.nanodegree.thelsien.popularmovies.adapters.VideosAdapter;
import apps.nanodegree.thelsien.popularmovies.background.MovieQueryAsyncTask;
import apps.nanodegree.thelsien.popularmovies.model.Movie;
import apps.nanodegree.thelsien.popularmovies.model.MovieContract;

public class MovieDetailFragment extends Fragment
        implements MovieQueryAsyncTask.MovieQueryAsyncTaskListener {

    private Movie mMovie;
    private static final String LOG_TAG = "MovieDetailFragment";
    private VideosAdapter mVideosAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(getString(R.string.intent_extra_movie))) {
            mMovie = intent.getParcelableExtra(getString(R.string.intent_extra_movie));
            mMovie.posterImageUrlPart = MoviesAdapter.POSTER_IMAGE_BASE_URL + mMovie.posterImageUrlPart;
        } else {
            Uri uri = intent.getParcelableExtra(getString(R.string.intent_extra_movie_uri));
            Cursor c = null;
            try {
                c = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    mMovie = new Movie(
                            c.getInt(c.getColumnIndex(MovieContract.MovieEntry._ID)),
                            c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)),
                            c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL)),
                            c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)),
                            c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVG)),
                            c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE))
                    );
                }
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "Failed to query uri: " + uri);
            } finally {
                if (c != null && !c.isClosed()) {
                    c.close();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        NetworkInfo netInfo = Globals.getNetworkInfo(getActivity());

        if (netInfo != null && netInfo.isConnected()) {
            MovieQueryAsyncTask movieQueryAsyncTask = new MovieQueryAsyncTask(this);
            movieQueryAsyncTask.execute(mMovie.id);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container);

        RecyclerView videosView = (RecyclerView) rootView.findViewById(R.id.rv_videos);
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        videosView.setLayoutManager(videosLayoutManager);
        mVideosAdapter = new VideosAdapter(getActivity(), new JSONArray());
        videosView.setAdapter(mVideosAdapter);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.iv_movie_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.tv_movie_title);
        TextView plotView = (TextView) rootView.findViewById(R.id.tv_movie_plot_synopsis);
        TextView voteView = (TextView) rootView.findViewById(R.id.tv_vote_avg);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.tv_release_date);

        Picasso.with(getActivity())
                .load(mMovie.posterImageUrlPart)
                .placeholder(R.drawable.default_movie_poster)
                .into(posterView);
        titleView.setText(mMovie.originalTitle);
        plotView.setText(mMovie.plotSynopsis);
        voteView.setText(String.format(getString(R.string.vote_average_placeholder), String.valueOf(mMovie.voteAverage)));
        releaseDateView.setText(mMovie.releaseDate);

        NetworkInfo netInfo = Globals.getNetworkInfo(getActivity());

        if (netInfo == null || !netInfo.isConnected()) {
            TextView runtimeView = (TextView) rootView.findViewById(R.id.tv_runtime);
            runtimeView.setVisibility(View.GONE);
        }

        Button addToFavorites = (Button) rootView.findViewById(R.id.btn_add_to_favorites);
        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry._ID, mMovie.id);
                cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovie.originalTitle);
                cv.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, MoviesAdapter.POSTER_IMAGE_BASE_URL + mMovie.posterImageUrlPart);
                cv.put(MovieContract.MovieEntry.COLUMN_DURATION, mMovie.runTime);
                cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.releaseDate);
                cv.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, mMovie.plotSynopsis);
                cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, mMovie.voteAverage);

                Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);

                Log.d(LOG_TAG, "inserted uri: " + insertedUri);

                Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });

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

        mVideosAdapter.changeAdapterData(result.optJSONArray("videos"));

        LinearLayout reviewsContainer = (LinearLayout) getView().findViewById(R.id.container_reviews);
        JSONArray reviews = result.optJSONArray("reviews");
        for (int i = 0; i < reviews.length(); i++) {
            JSONObject review = reviews.optJSONObject(i);
            View reviewRowView = LayoutInflater.from(getActivity()).inflate(R.layout.list_review_row_item, reviewsContainer, false);
            ((TextView) reviewRowView.findViewById(R.id.tv_review_author)).setText(review.optString("author"));
            ((TextView) reviewRowView.findViewById(R.id.tv_review_text)).setText(review.optString("content"));

            reviewsContainer.addView(reviewRowView);
        }
    }
}
