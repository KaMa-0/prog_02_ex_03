package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieEntity {
    private long id;
    private String apild;
    private String title;
    private String description;
    private String genres;
    private String imgUrl;
    private int lengthInMinutes;
    private double rating;

    public String genresToString(List<Genre> genres) {
        StringBuilder genreString = new StringBuilder();
        for (Genre genre : genres) {
            genreString.append(genre).append(",");
        }
        return genreString.toString();
    }

    public List<MovieEntity> fromMovies (List<Movie> movies) {
        List<MovieEntity> movieEntities = new ArrayList<>();
        // add logic for movieEntity conversion
        return movieEntities;
    }

    public List<Movie> toMovies (List<MovieEntity> movieEntities) {
        List<Movie> movies = new ArrayList<>();
        // add logic for Movie conversion
        return movies;
    }
}
