package apps.nanodegree.thelsien.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import apps.nanodegree.thelsien.popularmovies.R;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private JSONArray mVideos;
    private Context mContext;

    public VideosAdapter(Context context, JSONArray videos) {
        mContext = context;
        mVideos = videos;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_video_row_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder videoHolder, final int position) {
        Picasso.with(mContext)
                .load("http://img.youtube.com/vi/" + mVideos.optJSONObject(position).optString("key") + "/0.jpg")
                .placeholder(R.drawable.video_placeholder)
                .error(R.drawable.video_placeholder)
                .into(videoHolder.previewImageView);
        videoHolder.videoTitleView.setText(mVideos.optJSONObject(position).optString("name"));

        videoHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.youtube.com/watch")
                        .buildUpon()
                        .appendQueryParameter("v", mVideos.optJSONObject(position).optString("key"))
                        .build();
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(youtubeIntent);
            }
        });
    }

    public void changeAdapterData(JSONArray newVideos) {
        mVideos = newVideos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVideos.length();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView previewImageView;
        public TextView videoTitleView;

        public VideoViewHolder(View itemView) {
            super(itemView);

            previewImageView = (ImageView) itemView.findViewById(R.id.iv_video_preview);
            videoTitleView = (TextView) itemView.findViewById(R.id.tv_video_title);
        }
    }
}
