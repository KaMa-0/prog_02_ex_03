package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseManager {
    private final String DB_URL = "jdbc:h2:file:./fhmdbDatabase";
    private String username = "user";
    private String password = "pass";
    private static DatabaseManager instance;
    private ConnectionSource connectionSource;
    private Dao<MovieEntity, Long> movieDao;
    private Dao<WatchlistMovieEntity, Long> watchlistDao;

    private DatabaseManager() throws DatabaseException{
        try {
            createConnectionSource();
            createTables();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() throws DatabaseException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void createConnectionSource() throws DatabaseException {
        try {
            connectionSource = new JdbcConnectionSource(DB_URL, username, password);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        } finally { // connection cleanup
            if(connectionSource != null) {
                try {
                    connectionSource.close();
                } catch (Exception e) {
                    throw new DatabaseException(e);
                }
            }
        }
    }

    public void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MovieEntity.class);
        TableUtils.createTableIfNotExists(connectionSource, WatchlistMovieEntity.class);

        movieDao = DaoManager.createDao(connectionSource, MovieEntity.class);
        watchlistDao = DaoManager.createDao(connectionSource, WatchlistMovieEntity.class);
    }

    public ConnectionSource getConnectionSource() throws DatabaseException {
        return connectionSource;
    }

    public Dao<MovieEntity, Long> getMovieDao() {
        return movieDao;
    }

    public Dao<WatchlistMovieEntity, Long> getWatchlistDao() {
        return watchlistDao;
    }

    // Hilfsmethode zum Schließen der Verbindung
    public void closeConnection() throws DatabaseException {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                throw new DatabaseException(e.getMessage());
            }
        }
    }
}