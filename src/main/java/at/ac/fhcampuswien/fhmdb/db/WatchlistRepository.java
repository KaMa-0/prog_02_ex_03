package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WatchlistRepository {
    private Dao<WatchlistMovieEntity, Long> dao;
    private MovieRepository movieRepository;

    public WatchlistRepository(DatabaseManager databaseManager, MovieRepository movieRepository) {
        this.dao = databaseManager.getWatchlistDao();
        this.movieRepository = movieRepository;
    }

    public List<WatchlistMovieEntity> getWatchlist() throws DatabaseException {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error getting watchlist from database", e);
        }
    }

    // Entspricht dem vorgeschlagenen getAll()
    public List<WatchlistMovieEntity> getAll() throws DatabaseException {
        return getWatchlist();
    }

    public List<Movie> getWatchlistMovies() throws DatabaseException {
        List<WatchlistMovieEntity> watchlistEntities = getWatchlist();
        List<Movie> watchlistMovies = new ArrayList<>();

        for (WatchlistMovieEntity entity : watchlistEntities) {
            MovieEntity movieEntity = movieRepository.getMovie(entity.getApiId());
            if (movieEntity != null) {
                watchlistMovies.add(movieEntity.toMovie());
            }
        }

        return watchlistMovies;
    }

    public int addToWatchlist(Movie movie) throws DatabaseException {
        try {
            // Prüfe, ob Film bereits in der Watchlist ist
            WatchlistMovieEntity existingEntry = getWatchlistEntry(movie.getId());
            if (existingEntry != null) {
                // Film ist bereits in der Watchlist
                return 0;
            }

            // Stelle sicher, dass der Film in der movie-Tabelle ist
            MovieEntity movieEntity = movieRepository.getMovie(movie.getId());
            if (movieEntity == null) {
                // Film ist nicht in der Datenbank, füge ihn hinzu
                List<Movie> movies = new ArrayList<>();
                movies.add(movie);
                movieRepository.addAllMovies(movies);
            }

            // Füge Film zur Watchlist hinzu
            WatchlistMovieEntity watchlistEntry = new WatchlistMovieEntity(movie.getId());
            return dao.create(watchlistEntry);
        } catch (SQLException e) {
            throw new DatabaseException("Error adding movie to watchlist", e);
        }
    }

    // Ergänzung: Direkt mit MovieEntity arbeiten
    public int addToWatchlist(MovieEntity movieEntity) throws DatabaseException {
        try {
            // Prüfe, ob Film bereits in der Watchlist ist
            WatchlistMovieEntity existingEntry = getWatchlistEntry(movieEntity.getApiId());
            if (existingEntry != null) {
                // Film ist bereits in der Watchlist
                return 0;
            }

            // Füge Film zur Watchlist hinzu
            WatchlistMovieEntity watchlistEntry = new WatchlistMovieEntity(movieEntity.getApiId());
            return dao.create(watchlistEntry);
        } catch (SQLException e) {
            throw new DatabaseException("Error adding movie to watchlist", e);
        }
    }

    public int removeFromWatchlist(String apiId) throws DatabaseException {
        try {
            DeleteBuilder<WatchlistMovieEntity, Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("apiId", apiId);
            return deleteBuilder.delete();
        } catch (SQLException e) {
            throw new DatabaseException("Error removing movie from watchlist", e);
        }
    }

    // Ergänzung: Entfernen über ID
    public int removeFromWatchlist(Long watchlistId) throws DatabaseException {
        try {
            return dao.deleteById(watchlistId);
        } catch (SQLException e) {
            throw new DatabaseException("Error removing movie from watchlist", e);
        }
    }

    private WatchlistMovieEntity getWatchlistEntry(String apiId) throws SQLException {
        QueryBuilder<WatchlistMovieEntity, Long> queryBuilder = dao.queryBuilder();
        queryBuilder.where().eq("apiId", apiId);
        return queryBuilder.queryForFirst();
    }
}