package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

public class WatchlistRepository {
    private Dao<WatchlistMovieEntity, Long> dao;

    public List<WatchlistMovieEntity> getWatchlist() {
        // TEMPORARY, TODO: Add proper functionality
        return new ArrayList<>();
    }

    int addToWatchlist(WatchlistMovieEntity movie) {
        // TEMPORARY, TODO: Add proper functionality
        return -1;
    }

    int removeFromWatchlist(String aplid) {
        // TEMPORARY, TODO: Add proper functionality
        return -1;
    }
}
