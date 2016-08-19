package apps.nanodegree.thelsien.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import apps.nanodegree.thelsien.popularmovies.model.Movie;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    public static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";

    private static final String LOG_TAG = "MoviesAdapter";

    public MoviesAdapter(Context context, Movie[] objects) {
        super(context, 0, objects);
    }

    public MoviesAdapter(Context context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movieObject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_list_row_item, parent, false);
        }

        ImageView moviePosterView = (ImageView) convertView.findViewById(R.id.iv_movie_poster);
        if (movieObject != null) {
            Picasso.with(getContext()).load(POSTER_IMAGE_BASE_URL + movieObject.posterImageUrlPart).placeholder(R.drawable.default_movie_poster).into(moviePosterView);
        }

        return convertView;
    }
}
