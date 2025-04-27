package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {
    private Dao<MovieEntity, Long> dao;

    public MovieRepository(DatabaseManager databaseManager) {
        this.dao = databaseManager.getMovieDao();
    }

    public List<MovieEntity> getAllMovies() throws DatabaseException {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error getting all movies from database", e);
        }
    }

    public MovieEntity getMovie(String apiId) throws DatabaseException {
        try {
            QueryBuilder<MovieEntity, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("apiId", apiId);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving movie with ID " + apiId, e);
        }
    }

    public int addAllMovies(List<Movie> movies) throws DatabaseException {
        try {
            // Konvertiere Movie-Objekte zu MovieEntity-Objekte
            List<MovieEntity> movieEntities = MovieEntity.fromMovies(movies);

            // Zähler für erfolgreich hinzugefügte Filme
            int count = 0;

            // Füge jeden Film einzeln hinzu
            for (MovieEntity entity : movieEntities) {
                // Prüfe, ob Film bereits in der Datenbank existiert
                MovieEntity existingMovie = getMovie(entity.getApiId());

                if (existingMovie == null) {
                    // Film existiert nicht, füge hinzu
                    dao.create(entity);
                    count++;
                }
            }

            return count;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding movies to database", e);
        }
    }
}