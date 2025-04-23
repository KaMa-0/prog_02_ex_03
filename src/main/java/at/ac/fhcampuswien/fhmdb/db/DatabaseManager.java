package at.ac.fhcampuswien.fhmdb.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseManager {
    private final String DB_URL = "";
    private String username = "user";
    private String password = "pass";

    private ConnectionSource conn;

    private Dao<MovieEntity, Long> movieDao;
    private Dao<WatchlistMovieEntity, Long> watchlistDao;

    public void createConnectionsSource() {

    }

    public void createTables() {

    }

    public ConnectionSource getConnectionSource() {
        return this.conn;
    }

    public Dao<MovieEntity, Long> getMovieDao() {
        return this.movieDao;
    }

    private Dao<WatchlistMovieEntity, Long> getWatchlistDao() {
        return this.watchlistDao;
    }
}
