package apps.nanodegree.thelsien.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container);

        Intent intent = getActivity().getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.intent_extra_movie));

        ImageView posterView = (ImageView) rootView.findViewById(R.id.iv_movie_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.tv_movie_title);
        TextView plotView = (TextView) rootView.findViewById(R.id.tv_movie_plot_synopsis);
        TextView voteView = (TextView) rootView.findViewById(R.id.tv_vote_avg);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.tv_release_date);

        Picasso.with(getActivity()).load(MoviesAdapter.POSTER_IMAGE_BASE_URL + movie.posterImageUrlPart).placeholder(R.drawable.default_movie_poster).into(posterView);
        titleView.setText(movie.originalTitle);
        plotView.setText(movie.plotSynopsis);
        voteView.setText(String.valueOf(movie.voteAverage));
        releaseDateView.setText(movie.releaseDate);

        return rootView;
    }
}
