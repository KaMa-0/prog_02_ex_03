package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

public class MovieRepository {
    private Dao<MovieEntity, Long> dao;

    public List<MovieEntity> getAllMovies() {
        // TEMPORARY, TODO: Add proper functionality
        List<MovieEntity> movies = new ArrayList<>();
        return movies;
    }

    public int removeAll() {
        // TEMPORARY, TODO: Add proper functionality
        return -1;
    }

    public MovieEntity getMovie() {
        // TEMPORARY, TODO: Add proper functionality
        return new MovieEntity();
    }

    public int addAllMovies(List<Movie> movies) {
        // TEMPORARY, TODO: Add proper functionality
        return -1;
    }
}
