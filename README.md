# PopularMovies
Popular Movies project for Udacity Nanodegree program.

Please add your themoviedb.org API_KEY to the project.

You can add it in the MovieQueryAsyncTask:

```java

public class MovieQueryAsyncTask extends AsyncTask<String, Void, JSONArray> {

    private static final String API_KEY = "YOURAPIKEY";
    ...
```
