package apps.nanodegree.thelsien.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import apps.nanodegree.thelsien.popularmovies.R;
import apps.nanodegree.thelsien.popularmovies.model.MovieContract;

/**
 * Created by frodo on 2016. 08. 27..
 */

public class FavoriteMoviesCursorAdapter extends CursorAdapter {

    public FavoriteMoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.iv_movie_poster);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_list_row_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();

        Picasso.with(context)
                .load(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL)))
                .placeholder(R.drawable.default_movie_poster)
                .error(R.drawable.default_movie_poster)
                .into(vh.posterView);
    }

    @Override
    public Long getItem(int position) {
        Cursor c = (Cursor) super.getItem(position);

        return c.getLong(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
    }
}
