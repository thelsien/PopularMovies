package apps.nanodegree.thelsien.popularmovies;

/**
 * Created by frodo on 2016. 08. 18..
 */

public class Movie {
    String originalTitle;
    String posterImageUrlPart;
    String plotSynopsis;
    double voteAverage;
    String releaseDate;

    public Movie(String originalTitle, String posterImageUrlPart, String plotSynopsis, double voteAverage, String releaseDate) {
        this.originalTitle = originalTitle;
        this.posterImageUrlPart = posterImageUrlPart;
        this.plotSynopsis = plotSynopsis;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }
}
