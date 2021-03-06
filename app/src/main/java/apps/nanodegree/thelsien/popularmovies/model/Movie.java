package apps.nanodegree.thelsien.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public int id;
    public String originalTitle;
    public String posterImageUrlPart;
    public String plotSynopsis;
    public double voteAverage;
    public String releaseDate;
    public int runTime = 0;

    public Movie(int id, String originalTitle, String posterImageUrlPart, String plotSynopsis, double voteAverage, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterImageUrlPart = posterImageUrlPart;
        this.plotSynopsis = plotSynopsis;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        posterImageUrlPart = in.readString();
        plotSynopsis = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        parcel.writeString(posterImageUrlPart);
        parcel.writeString(plotSynopsis);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
