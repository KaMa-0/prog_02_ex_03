package at.ac.fhcampuswien.fhmdb.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
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

    public static DatabaseManager getInstance() {
        // Diese Methode stellt sicher, dass es während der gesamten Ausführung der Anwendung nur
        // eine DatabaseManager-Instanz gibt. Dadurch wird die Datenbankverbindung an einer
        //zentralen Stelle verwaltet und verhindert, dass bei jedem Zugriff neue Verbindungen geöffnet werden.
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    private DatabaseManager() {
        try {
            createConnectionsSource();
            createTables();
        }
        catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public void createConnectionsSource() throws SQLException { // Öffnet die JDBC-Verbindung
        connectionSource = new JdbcConnectionSource(DB_URL, username, password);
    }

    public void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, MovieEntity.class); //Erstellt automatisch Tabellen basierend auf den Entity-Klassen (falls sie noch nicht existieren)
        TableUtils.createTableIfNotExists(connectionSource, WatchlistMovieEntity.class);

        movieDao = DaoManager.createDao(connectionSource, MovieEntity.class); //Erstellt DAO-Objekte für jede Entity
        watchlistDao = DaoManager.createDao(connectionSource, WatchlistMovieEntity.class);

    }

    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }

    public Dao<MovieEntity, Long> getMovieDao() {
        return this.movieDao;
    }

    private Dao<WatchlistMovieEntity, Long> getWatchlistDao() {
        return this.watchlistDao;
    }
}
